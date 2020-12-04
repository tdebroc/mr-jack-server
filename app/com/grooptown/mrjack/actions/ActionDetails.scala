package com.grooptown.mrjack.actions

import com.grooptown.mrjack.actions.behavior.Action
import com.grooptown.mrjack.actions.input.ActionInput
import com.grooptown.mrjack.actions.tokens.ActionToken

case class ActionDetails(
                          isValid: Boolean,
                          errorMessage: String,
                          action: Action = null,
                          actionInput: ActionInput = null,
                          actionToken: ActionToken = null
                        )
