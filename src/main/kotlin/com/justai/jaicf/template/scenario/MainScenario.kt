package com.justai.jaicf.template.scenario

import com.github.kotlintelegrambot.entities.KeyboardReplyMarkup
import com.github.kotlintelegrambot.entities.ReplyKeyboardRemove
import com.github.kotlintelegrambot.entities.keyboard.KeyboardButton
import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.telegram.TelegramEvent
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.reactions.buttons

val mainScenario = Scenario {

    append(RedirectionScenario)

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
                    replyMarkup = KeyboardReplyMarkup(KeyboardButton("Отправить контакт", requestContact = true))
                )
            }

            state("allow") {
                activators {
                    event(TelegramEvent.CONTACT)
                }
                action {
                    // val phoneNumber = request.telegram?.message?.contact?.phoneNumber
                    reactions.telegram?.say("Как вас представлять?", replyMarkup = ReplyKeyboardRemove())
                    reactions.telegram?.go("getName")
                }
            }
        }

        state("getName") {
            activators {
                catchAll()
            }
            action {
                context.client["name"] = request.input
                reactions.telegram?.say("Отлично, я вас буду представлять, как '${context.client["name"]}'")
                reactions.telegram?.buttons("Да, все верно" to RedirectionScenario.settingRedirection , "Нет, меняем" to "changeName")
            }

            state("changeName") {
                action {
                    reactions.telegram?.say("Хорошо, как вас представлять?")
                }
            }
        }
    }
}