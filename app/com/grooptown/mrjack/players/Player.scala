package com.grooptown.mrjack.players

import com.grooptown.mrjack.ai.AIPlayer
import com.grooptown.mrjack.game.{Game, TurnToken}

import scala.collection.mutable.ListBuffer

abstract class Player(
                       var alibiCards: ListBuffer[AlibiCard] = new ListBuffer[AlibiCard],
                       var turnTokens: ListBuffer[TurnToken] = new ListBuffer[TurnToken]
                     ) {

  var aiBrain : Option[AIPlayer] = None

  def getAIName: String = if (aiBrain.isEmpty) "NoAI" else aiBrain.get.getClass.toString

  def getTurnTokenCount: Int = turnTokens.length

  def getAlibiCardsCount: Int = alibiCards.length

  def hasReachObjective(game: Game): Boolean

  def printName: String

  def copyPlayer: Player
}
