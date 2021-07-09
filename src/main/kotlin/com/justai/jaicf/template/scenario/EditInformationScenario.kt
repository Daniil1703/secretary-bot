package com.justai.jaicf.template.scenario

import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.getValue

object EditInformationScenario : Scenario {

    const val editInformation = "/editInformation"

    override val model by Scenario {

        state(editInformation) {

        }
    }
}