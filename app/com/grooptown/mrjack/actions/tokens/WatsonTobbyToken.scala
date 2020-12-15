package com.grooptown.mrjack.actions.tokens

import com.grooptown.mrjack.actions.behavior.{Action, MoveTobbyAction, MoveWatsonAction}

case class WatsonTobbyToken(
                             var isRectoParam: Boolean = true,
                             var isUsedParam: Boolean = false
                           ) extends ActionToken(isRectoParam, isUsedParam) {
  override def getCurrentAction: Action = if (isRecto) MoveWatsonAction else MoveTobbyAction

  override def copyToken: ActionToken = WatsonTobbyToken(this.isRecto, this.isUsed)
}
