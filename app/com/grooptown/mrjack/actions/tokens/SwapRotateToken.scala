package com.grooptown.mrjack.actions.tokens

import com.grooptown.mrjack.actions.behavior.{Action, RotateAction, SwapDistrictAction}

case class SwapRotateToken(
                            var isRectoParam: Boolean = true,
                            var isUsedParam: Boolean = false
                          ) extends ActionToken(isRectoParam, isUsedParam) {
  override def getCurrentAction: Action = if (isRecto) SwapDistrictAction else RotateAction

  override def copyToken: ActionToken = JokerRotateToken(this.isRecto, this.isUsed)
}
