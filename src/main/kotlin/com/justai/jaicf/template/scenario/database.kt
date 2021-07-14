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
import org.bson.Document

class MongoBotContextManagerGlobal(
    private val collection: MongoCollection<Document>
): BotContextManager {

    @Suppress("DEPRECATION")
    private val mapper = jacksonObjectMapper().enableDefaultTyping()
    var clientId = String()

    override fun loadContext(request: BotRequest, requestContext: RequestContext): BotContext {
        return collection
            .find(Filters.eq("_id", clientId))
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
        if (request?.telephony?.caller != null) {
            clientId = request.telephony?.caller.toString()
        } else if (request?.telegram?.message?.contact?.phoneNumber != null) {
            clientId = request.telegram?.message?.contact?.phoneNumber!!
        }
        BotContextModel(
            _id = clientId,
            result = botContext.result,
            client = botContext.client.toMap(),
            session = botContext.session.toMap(),
            dialogContext = botContext.dialogContext
        ).apply {
            val doc = Document.parse(mapper.writeValueAsString(this))
            collection.replaceOne(Filters.eq("_id", _id), doc, ReplaceOptions().upsert(true))
        }
    }
}