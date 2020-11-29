package com.grooptown.mrjack.actions.tokens

import com.grooptown.mrjack.actions.behavior.{Action, RotateAction, SwapDistrictAction}

class SwapRotateToken extends ActionToken {
  override def getCurrentAction: Action = if (isRecto) SwapDistrictAction else RotateAction
}
