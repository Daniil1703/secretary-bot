package com.justai.jaicf.template.scenario.telegram

import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.getValue

object RedirectionScenario : Scenario {

    const val settingRedirection = "/settingRedirection"

    override val model by Scenario {

        append(EditInformationScenario)

        state(settingRedirection) {
            action {
                reactions.telegram?.run {
                    say("Теперь давайте настроим переадресацию с вашего номера на мой. Это безопасно.")
                    say("Шаг 1: Скопируйте номер ниже.")
                    say("**67*+74950493678#")
                    say("Шаг 2: Вставьте в поле для набора номера и нажмите \"вызов\"")
                    say("Шаг 4: Позвоните себе с другого номера и сбросьте. Так вы проверите мою работу.")
                    go(EditInformationScenario.editInformation)
                }
            }
        }
    }
}