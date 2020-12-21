package com.grooptown.mrjack.players

import com.grooptown.mrjack.game.Game

case class PlayerSecret(
                         pseudo: String,
                         isMrJack: Boolean,
                         game: Game
                       ) {
  def getMrJackIdentity: String = if (isMrJack) game.mrJackPlayer.alibiCard.name.toString else null

  def getAlibiCards: Array[AlibiCard] = if (isMrJack) game.mrJackPlayer.alibiCards.toArray else
    game.detectivePlayer.alibiCards.toArray

  override def toString: String = getMrJackIdentity + " " + getAlibiCards.length
}
