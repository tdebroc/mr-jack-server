package com.grooptown.mrjack.players

import com.grooptown.mrjack.game.Game

case class MrJackPlayer(alibiCard: AlibiCard) extends Player {

  override def hasReachObjective(game : Game): Boolean = countHourGlass() >= 6

  def swapActionsToken(game : Game) : Unit = {
    game.actionTokens.foreach(token => token.isRecto = if (token.isRecto) false else true)
  }

  def countHourGlass() : Int = alibiCards.map(_.hourGlassCount).sum + turnTokens.length

  override def printName: String = "\uD83D\uDD74 Mr Jack"
}
