package com.grooptown.mrjack.players

import com.grooptown.mrjack.game.Game

import scala.collection.mutable.ListBuffer

case class MrJackPlayer(var alibiCard: AlibiCard = null,
                        var alibiCardsParam: ListBuffer[AlibiCard] = new ListBuffer[AlibiCard])
  extends Player(alibiCardsParam) {

  override def hasReachObjective(game: Game): Boolean = countHourGlass() >= 6

  def swapActionsToken(game: Game): Unit = {
    game.actionTokens.foreach(token => token.isRecto = if (token.isRecto) false else true)
  }

  def countHourGlass(): Int = alibiCards.map(_.hourGlassCount).sum + turnTokens.length

  override def printName: String = "\uD83D\uDD74 Mr Jack"

  override def copyPlayer: MrJackPlayer = MrJackPlayer(alibiCard, alibiCards.map(_.copy()))
}
