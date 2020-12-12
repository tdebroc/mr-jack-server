package com.grooptown.mrjack.players

import com.grooptown.mrjack.game.Game

import scala.collection.mutable.ListBuffer
import scala.util.Random

case class DetectivePlayer(
                            var alibiCardsParam : ListBuffer[AlibiCard] = new ListBuffer[AlibiCard]
                          ) extends Player(alibiCardsParam) {
  def launchTokenAction(game: Game): Unit = {
    game.actionTokens.foreach(_.isRecto = Random.nextInt() % 2 == 0)
  }

  override def hasReachObjective(game: Game): Boolean = {
      game.board.getDistricts.count(_.isRecto) == 1
  }

  def getAlibiCards: Array[String] = alibiCards.map(_.name.toString).toArray
  
  override def printName: String =  "🕵 Detective "

  override def copyPlayer: DetectivePlayer = DetectivePlayer(alibiCards.map(_.copy()))
}
