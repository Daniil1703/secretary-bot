package com.justai.jaicf.template.scenario.telephony

import com.justai.jaicf.builder.Scenario
import com.justai.jaicf.channel.jaicp.channels.TelephonyEvents
import com.justai.jaicf.channel.jaicp.reactions.telephony
import com.justai.jaicf.model.scenario.Scenario
import com.justai.jaicf.model.scenario.getValue

object TelephonyScenario : Scenario {
    override val model by Scenario {

        state("acceptedCall") {
            activators {
                event(TelephonyEvents.SPEECH_NOT_RECOGNISED)
                event(TelephonyEvents.NO_DTMF_ANSWER)
                intent("Hello")
            }
            action {
                reactions.telephony?.run {
                    say("Здравствуйте! Я виртуальный секретарь ${context.client["userName"]}, чем могу Вам помочь?")
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
                        reactions.telephony?.say("К сожалению, ${context.client["userName"]} сейчас не может вам ответить. Он на собрании." +
                                "- Скажите пожалуйста, что ему передать?")
                    }

                    state("conveyThatCallBack") {
                        activators {
                            intent("callBack")
                        }
                        action {
                            reactions.telephony?.say("есть ли пожелания по времени, в которое вам будет удобно?")
                            reactions.telephony?.go("/goodbye")
                        }

                        state("yes") {
                            activators {
                                intent("anyWord")
                            }

                            action {
                                reactions.telephony?.run {
                                    say("Хорошо.")
                                    go("/goodbye")
                                }
                            }
                        }

                        state("no") {
                            activators {
                                intent("nothing")
                            }
                            action { reactions.telephony?.go("/goodbye") }
                        }
                    }

                    state("conveyThatNothing") {
                        activators {
                            intent("nothing")
                        }
                        action {
                            reactions.telephony?.run {
                                say("Я тогда просто передам, что вы звонили.")
                                go("/goodbye")
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
                                    go("/goodbye")
                                }
                            }
                        }

                        state("no") {
                            activators {
                                intent("nothing")
                            }
                            action { reactions.telephony?.go("/goodbye") }
                        }
                    }
                }

                state("delivery") {

                }
            }
            state("goodbye") {
                action {
                    reactions.telephony?.run {
                        say("До свидания.")
                        hangup()
                        go("route")
                    }
                }
            }
        }
    }
}