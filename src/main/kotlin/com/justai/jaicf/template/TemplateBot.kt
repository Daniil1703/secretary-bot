package com.justai.jaicf.template

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.caila.CailaIntentActivator
import com.justai.jaicf.activator.caila.CailaNLUSettings
import com.justai.jaicf.activator.regex.RegexActivator
import com.justai.jaicf.channel.jaicp.logging.JaicpConversationLogger
import com.justai.jaicf.context.manager.mongo.MongoBotContextManager
import com.justai.jaicf.logging.Slf4jConversationLogger
import com.justai.jaicf.template.scenario.mainScenario
import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import java.util.*


val accessToken: String = System.getenv("JAICP_API_TOKEN") ?: Properties().run {
    load(CailaNLUSettings::class.java.getResourceAsStream("/jaicp.properties"))
    getProperty("apiToken")
}

val cailaActivator = CailaIntentActivator.Factory(
    CailaNLUSettings(
        accessToken = accessToken,
        confidenceThreshold = 0.2
    )
)


var connectionString =
    ConnectionString("mongodb+srv://denny170300:Passwordformongo@cluster0.jcxkg.mongodb.net/myFirstDatabase?retryWrites=true&w=majority")
var settings = MongoClientSettings.builder()
    .applyConnectionString(connectionString)
    .build()
var mongoClient = MongoClients.create(settings)
var database = mongoClient.getDatabase("test")


val manager = MongoBotContextManager(mongoClient.getDatabase("jaicf").getCollection("contexts"))

val templateBot = BotEngine(
    scenario = mainScenario,
    defaultContextManager  = manager,
    conversationLoggers = arrayOf(
        JaicpConversationLogger(accessToken),
        Slf4jConversationLogger()
    ),
    activators = arrayOf(
        cailaActivator,
        RegexActivator
    )
)
