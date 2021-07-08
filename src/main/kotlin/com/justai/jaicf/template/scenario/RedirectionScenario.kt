package com.justai.jaicf.template.scenario

import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.getValue

object RedirectionScenario : Scenario {

    const val settingRedirection = "/settingRedirection"

    override val model by Scenario {

        state(settingRedirection) {
            action {
                reactions.say("Отлично! я буду представлять вас, как ${context.client["name"]}")
            }
        }
    }
}