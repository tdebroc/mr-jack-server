package com.grooptown.mrjack.actions.tokens

import com.grooptown.mrjack.actions.behavior.{Action, JokerAction, RotateAction}

class JokerRotateToken extends ActionToken {
  override def getCurrentAction : Action = if (isRecto) JokerAction else RotateAction
}
