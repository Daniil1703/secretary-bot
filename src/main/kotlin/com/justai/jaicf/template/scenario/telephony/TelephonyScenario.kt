package com.justai.jaicf.template.scenario.telephony

import com.justai.jaicf.activator.event.AnyEventActivationRule
import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.jaicp.channels.TelephonyEvents
import com.justai.jaicf.channel.jaicp.dto.TelephonyEventRequest
import com.justai.jaicf.channel.jaicp.dto.telephony
import com.justai.jaicf.channel.jaicp.reactions.telephony
import com.justai.jaicf.channel.telegram.TelegramTextRequest
import com.justai.jaicf.channel.telegram.telegram
import com.justai.jaicf.channel.telegram.text
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.getValue

object TelephonyScenario : Scenario {
    override val model by Scenario {

        state("acceptedCall") {
            activators {

            }
            action {
                reactions.telephony?.run {
                    say("Алло. - здравствуйте.")
                }
            }
        }

        state("anyReply") {
            activators {
                event(TelephonyEvents.NO_DTMF_ANSWER)
                intent("anyWord")
            }

            action {
                reactions.telephony?.say("Я виртуальный секретарь Данила, чем могу Вам помочь?")
            }
        }

        state("talkTo") {
            activators {
                intent("canITalkTo")
            }

            action {
                reactions.telephony?.say("Уточните пожалуйста, - по какому вопросу вы звоните?")
            }

            state("anyWord") {
                activators {
                    catchAll()
                }

                action {
                    reactions.telephony?.say("К сожалению, Даниил сейчас не может вам ответить. Он на собрании." +
                            "- Скажите пожалуйста, что ему передать?")
                }

                state("conveyThatCallBack") {
                    activators {
                        intent("callBack")
                    }
                    action {
                        reactions.telephony?.say("есть ли пожелания по времени, в которое вам будет удобно?")
                        reactions.telephony?.go("goodbye")
                    }

                    state("yes") {
                        activators {
                            intent("anyWord")
                        }

                        action {
                            reactions.telephony?.run {
                                say("Хорошо.")
                                go("goodbye")
                            }
                        }
                    }

                    state("no") {
                        activators {
                            intent("nothing")
                        }
                        action { reactions.telephony?.go("goodbye") }
                    }
                }

                state("conveyThatNothing") {
                    activators {
                        intent("nothing")
                    }
                    action {
                        reactions.telephony?.run {
                            say("Я тогда просто передам, что вы звонили.")
                            go("goodbye")
                        }
                    }
                }

                state("conveyThatAnyThing") {
                    activators {
                        intent("anyWord")
                    }
                    action {
                        reactions.telephony?.say("Хорошо, я все передам. Что-то еще?")
                    }

                    state("yes") {
                        activators {
                            intent("anyWord")
                        }

                        action {
                            reactions.telephony?.run {
                                say("Поняла, - передам")
                                go("goodbye")
                            }
                        }
                    }

                    state("no") {
                        activators {
                            intent("nothing")
                        }
                        action { reactions.telephony?.go("goodbye") }
                    }
                }
            }

            state("delivery") {

            }
        }

        state("goodbye") {
            action {
                reactions.telephony?.say("До свидания.")
                reactions.telephony?.hangup()
            }
        }
    }
}