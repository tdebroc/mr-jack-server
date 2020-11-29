package com.grooptown.mrjack.actions.tokens

import com.grooptown.mrjack.actions.behavior.Action

abstract class ActionToken {
  var isRecto =  true
  var isUsed = false
  def getCurrentAction: Action
  def getTokenName: String =  this.getClass.getSimpleName
}
