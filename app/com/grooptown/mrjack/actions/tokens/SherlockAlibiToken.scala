package com.grooptown.mrjack.actions.tokens

import com.grooptown.mrjack.actions.behavior.{Action, AlibiCardAction, MoveSherlockAction}

class SherlockAlibiToken extends ActionToken {
  override def getCurrentAction: Action = if (isRecto) MoveSherlockAction else AlibiCardAction
}
