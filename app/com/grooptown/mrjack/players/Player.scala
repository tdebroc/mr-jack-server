package com.grooptown.mrjack.players

import com.grooptown.mrjack.game.{Game, TurnToken}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

trait Player {
  var alibiCards : ListBuffer[AlibiCard] = new ListBuffer[AlibiCard]
  var turnTokens : ListBuffer[TurnToken] = new ListBuffer[TurnToken]
  def getTurnTokenCount: Int = turnTokens.length
  def getAlibiCardsCount: Int = alibiCards.length
  def hasReachObjective(game : Game): Boolean
  def printName : String

}
