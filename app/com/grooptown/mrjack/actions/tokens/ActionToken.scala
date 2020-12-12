package com.grooptown.mrjack.actions.tokens

import com.grooptown.mrjack.actions.behavior.Action

abstract class ActionToken(
                                 var isRecto: Boolean = true,
                                 var isUsed: Boolean = false
                               ) {

  def getCurrentAction: Action

  def getTokenName: String = this.getClass.getSimpleName

  def copyToken: ActionToken
}
