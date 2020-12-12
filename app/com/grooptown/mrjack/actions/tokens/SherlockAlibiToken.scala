package com.grooptown.mrjack.actions.tokens

import com.grooptown.mrjack.actions.behavior.{Action, AlibiCardAction, MoveSherlockAction}

case class SherlockAlibiToken(
                               var isRectoParam: Boolean = true,
                               var isUsedParam: Boolean = false
                             ) extends ActionToken(isRectoParam, isUsedParam) {
  override def getCurrentAction: Action = if (isRecto) MoveSherlockAction else AlibiCardAction

  override def copyToken: ActionToken = JokerRotateToken(this.isRecto, this.isUsed)
}
