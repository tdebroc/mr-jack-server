package com.grooptown.mrjack.players

import com.grooptown.mrjack.game.Game

import scala.util.Random

case class DetectivePlayer() extends Player {
  def launchTokenAction(game: Game): Unit = {
    game.actionTokens.foreach(_.isRecto = Random.nextInt() % 2 == 0)
  }

  override def hasReachObjective(game: Game): Boolean = {
      game.board.getDistricts.count(_.isRecto) == 1
  }

  override def printName: String =  "🕵 Detective "
}
