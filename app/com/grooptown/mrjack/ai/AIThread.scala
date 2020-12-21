package com.grooptown.mrjack.ai

import com.grooptown.mrjack.game.Game

import java.util.Date

class AIThread(aiPlayer: AIPlayer, game: Game) extends Runnable {

  def run() {
    var gameOn = true
    val startTime = (new Date).getTime
    while (gameOn) {
      if (game.winner.nonEmpty) {
        gameOn = false
      } else if (shouldAIPlay()) {
        aiPlayer.playMove(game)
      } else if (startTime + 60 * 60 * 1000 * 3 < (new Date).getTime) {
        gameOn = false
      }
      Thread.sleep(1000)
    }
    println("AI ended for Game " + game.gameId + " game winner is " + game.winner + " and time : "
        + (startTime + 60 * 60 * 1000 * 3 < (new Date).getTime) + " ")
  }

  def shouldAIPlay(): Boolean = {
    game.isDetectiveCurrentPlayer && !aiPlayer.isPlayerMrJack || !game.isDetectiveCurrentPlayer && aiPlayer.isPlayerMrJack
  }

}
