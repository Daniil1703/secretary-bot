package com.justai.jaicf.template.scenario.telegram

import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.jaicp.dto.telephony
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.context.DialogContext
import com.justai.jaicf.context.manager.mongo.BotContextModel
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.getValue
import com.justai.jaicf.template.scenario.telegram.EditInformationScenario.editInformation

object RedirectionScenario : Scenario {

    const val settingRedirection = "/settingRedirection"

    private var getPhoneNumber: String? = null
    private var getClientId: String? = null
    private var getChatId: Long? = null

    override val model by Scenario {

        state(settingRedirection) {
            action {
                if (request.telephony?.caller != null) {
                    getPhoneNumber = request.telegram?.message?.contact?.phoneNumber
                    getClientId = request.telegram?.clientId
                    getChatId = request.telegram?.chatId
                    BotContextModel(_id = "$getPhoneNumber",
                                    result = "telegram",
                                    client = mapOf("client" to getClientId, "chatId" to getChatId),
                                    dialogContext = DialogContext(),
                                    session = mapOf())
                }
                reactions.telegram?.run {
                    say("Теперь давайте настроим переадресацию с вашего номера на мой. Это безопасно.")
                    say("Шаг 1: Скопируйте номер ниже.")
                    say("**67*+74950493678#")
                    say("Шаг 2: Вставьте в поле для набора номера и нажмите \"вызов\"")
                    say("Шаг 4: Позвоните себе с другого номера и сбросьте. Так вы проверите мою работу.")
                    go(editInformation)
                }
            }
        }
    }
}