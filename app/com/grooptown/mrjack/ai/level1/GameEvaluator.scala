package com.grooptown.mrjack.ai.level1

import com.grooptown.mrjack.ai.AIPlayer
import com.grooptown.mrjack.board.Board
import com.grooptown.mrjack.game.Game
import com.grooptown.mrjack.players.DetectivePlayer

object GameEvaluator {

  val SECOND_FACTOR: Int = 100 * 1000

  def evaluateGame(game: Game, aiPlayer: AIPlayer): Int = {
    val score = evaluateGameHigherIsForDetective(game, aiPlayer)
    if (aiPlayer.isPlayerMrJack) -1 * score else score
  }


  def evaluateGameHigherIsForDetective(game: Game, aiPlayer: AIPlayer): Int = {
    if (game.winner.nonEmpty) {
      // Be careful if you want to replace by equals on .getClass => It works badly with $ at the end for one of them ...
      if ((new DetectivePlayer).printName.equals(game.winner.get.printName)) return 1 * 1000 * 1000 else return -1 * 1000 * 1000
    }
    var score = 0

    score += game.board.getDistricts.count(!_.isRecto) * SECOND_FACTOR
    if (aiPlayer.isPlayerMrJack) {
      score -= (game.mrJackPlayer.countHourGlass() * 1.3 * SECOND_FACTOR).toInt
    }
    score += 1000 * countOpenAngles(game)
    score
  }

  def countOpenAngles(game: Game): Int = {
    Board.detectivePositions.count(pos => {
      game.board.getVisibleCellsFromOneDetective(pos).count(_.district.get.isRecto) > 0
    })
  }

}
