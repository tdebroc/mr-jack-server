package com.grooptown.mrjack.ai

import com.grooptown.mrjack.game.Game

import java.util.Date

class AIThread(aiPlayer: AIPlayer, game: Game, isMrJack: Boolean) extends Runnable {

  def run() {
    var gameOn = true
    val startTime = (new Date).getTime
    while (gameOn) {
      if (game.findWinner().nonEmpty) {
        gameOn = false
      } else if (shouldAIPlay()) {
        aiPlayer.playMove(game)
      } else if (startTime + 60 * 60 * 1000 * 3 < (new Date).getTime) {
        gameOn = false
      }
      Thread.sleep(1000)
    }
  }

  def shouldAIPlay(): Boolean = {
    game.isDetectiveCurrentPlayer && !isMrJack || !game.isDetectiveCurrentPlayer && isMrJack
  }

}
