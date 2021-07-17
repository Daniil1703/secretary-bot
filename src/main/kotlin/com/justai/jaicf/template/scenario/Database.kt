package com.justai.jaicf.template.scenario

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.justai.jaicf.api.BotRequest
import com.justai.jaicf.api.BotResponse
import com.justai.jaicf.channel.jaicp.dto.telephony
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.context.BotContext
import com.justai.jaicf.context.RequestContext
import com.justai.jaicf.context.manager.BotContextManager
import com.justai.jaicf.context.manager.mongo.BotContextModel
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import kotlinx.serialization.json.jsonObject
import org.bson.Document

class MongoBotContextManagerGlobal(
    private val collection: MongoCollection<Document>
): BotContextManager {

    @Suppress("DEPRECATION")
    private val mapper = jacksonObjectMapper().enableDefaultTyping()

    private var clientIdTg = String()
    private var clientIdTel = String()
    override fun loadContext(request: BotRequest, requestContext: RequestContext): BotContext {
        if (request.telephony?.jaicp?.rawRequest?.get("headers") != null) {
            val stingWithSip = request.telephony?.jaicp?.rawRequest?.get("headers")?.jsonObject?.get("Diversion").toString()
            clientIdTel = stingWithSip.split(":")[1].split("@")[0]
        }
        return collection
            .find(Filters.eq("_id", clientIdTel))
            .iterator().tryNext()?.let { doc ->
                val model = mapper.readValue(doc.toJson(), BotContextModel::class.java)

                BotContext(model._id, model.dialogContext).apply {
                    result = model.result
                    client.putAll(model.client)
                    session.putAll(model.session)
                }

            } ?: BotContext(request.clientId)
    }

    override fun saveContext(
        botContext: BotContext,
        request: BotRequest?,
        response: BotResponse?,
        requestContext: RequestContext
    ) {

        if (request?.telegram?.message?.contact?.phoneNumber != null) {
            val bufferTelephone = request.telegram?.message?.contact?.phoneNumber?.split("+")?.get(0)
            clientIdTg = "700$bufferTelephone"
        }
        BotContextModel(
            _id = clientIdTg,
            result = botContext.result,
            client = botContext.client.toMap(),
            session = botContext.session.toMap(),
            dialogContext = botContext.dialogContext
        ).apply {
            val doc = Document.parse(mapper.writeValueAsString(this))
            collection.replaceOne(Filters.eq("_id", null), doc)
            collection.replaceOne(Filters.eq("_id", _id), doc, ReplaceOptions().upsert(true))
        }
    }
}