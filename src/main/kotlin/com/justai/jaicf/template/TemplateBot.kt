package com.justai.jaicf.template

import com.justai.jaicf.BotEngine
import com.justai.jaicf.activator.caila.CailaIntentActivator
import com.justai.jaicf.activator.caila.CailaNLUSettings
import com.justai.jaicf.activator.regex.RegexActivator
import com.justai.jaicf.channel.jaicp.logging.JaicpConversationLogger
import com.justai.jaicf.logging.Slf4jConversationLogger
import com.justai.jaicf.template.scenario.mainScenario
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

val templateBot = BotEngine(
    scenario = mainScenario,
    conversationLoggers = arrayOf(
        JaicpConversationLogger(accessToken),
        Slf4jConversationLogger()
    ),
    activators = arrayOf(
        cailaActivator,
        RegexActivator
    )
)
