package com.grooptown.mrjack.actions.tokens

import com.grooptown.mrjack.actions.behavior.{Action, MoveTobbyAction, MoveWatsonAction}

class WatsonTobbyToken extends ActionToken {
  override def getCurrentAction: Action = if (isRecto) MoveWatsonAction else MoveTobbyAction
}
