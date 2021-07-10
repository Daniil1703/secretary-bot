package com.justai.jaicf.template.scenario

import com.github.kotlintelegrambot.entities.InlineKeyboardMarkup
import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.ReplyKeyboardRemove
import com.github.kotlintelegrambot.entities.keyboard.InlineKeyboardButton
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.jaicp.channels.TelephonyEvents
import com.justai.jaicf.channel.jaicp.dto.telephony
import com.justai.jaicf.channel.jaicp.reactions.telephony
import com.justai.jaicf.channel.jaicp.telephony
import com.justai.jaicf.channel.telegram.TelegramEvent
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.context.DialogContext
import com.justai.jaicf.context.manager.mongo.BotContextModel
import com.justai.jaicf.context.manager.mongo.MongoBotContextManager
import com.justai.jaicf.reactions.buttons
import com.justai.jaicf.template.scenario.telegram.RedirectionScenario
import com.justai.jaicf.template.scenario.telephony.TelephonyScenario
import javax.swing.text.Document

val mainScenario = Scenario {

    append(RedirectionScenario)
    append(TelephonyScenario)

    state("start") {
        activators {
            regex("/start")
            intent("Hello")
        }
        action {
            reactions.telegram?.run {
                say(
                    "Здравствуйте! Я ваш помощник, бот-секретарь. Я буду отвечать на ваши звонки, если вы не" +
                            " можете или не хотите отвечать."
                )
                go("getPhoneNumber")
            }
        }

        state ("getPhoneNumber") {
            action {
                reactions.telegram?.say(
                    "Чтобы начать настройку, поделитесь со мной номером телефона",
                    replyMarkup = KeyboardReplyMarkup(
                        listOf(listOf(KeyboardButton("Отправить контакт", requestContact = true)))
                    )
                )
            }

            state("allow") {
                activators {
                    event(TelegramEvent.CONTACT)
                }
                action {
                    // val phoneNumber = request.telegram?.message?.contact?.phoneNumber
                    reactions.telegram?.run {
                        say("Как вас представлять?", replyMarkup = ReplyKeyboardRemove())
                        go("getName")
                    }
                }
            }
        }

        state("getName") {
            activators {
                intent("anyWord")
            }
            action {
                context.client["name"] = request.input
                reactions.telegram?.run {
                    say("Отлично, я вас буду представлять, как '${context.client["name"]}'")
                    buttons("Да, все верно" to RedirectionScenario.settingRedirection , "Нет, меняем" to "changeName")
                }
            }

            state("changeName") {
                action {
                    reactions.telegram?.run {
                        say("Хорошо, как вас представлять?")
                        go("getName")
                    }
                }
            }
        }
    }
}
