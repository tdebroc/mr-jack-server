package com.grooptown.mrjack.ai.level1

import com.grooptown.mrjack.ai.AIPlayer
import com.grooptown.mrjack.game.Game
import com.grooptown.mrjack.players.DetectivePlayer

object GameEvaluator {

  def evaluateGame(game: Game, aiPlayer: AIPlayer): Int = {
    val score = evaluateGameHigherIsForDetective(game)
    if (aiPlayer.isPlayerMrJack ) -1 * score else score
  }

  def evaluateGameHigherIsForDetective(game: Game): Int = {
    if (game.winner.nonEmpty) {
      if (game.winner.get.getClass.equals(DetectivePlayer.getClass)) return 1*1000*1000 else return - 1*1000*1000
    }
    game.board.getDistricts.count(!_.isRecto) * 10 * 1000

    // TODO: Add evaluation on hourglass count for MrJack
    // TODO: Check "almost visible alibis"
  }
}
