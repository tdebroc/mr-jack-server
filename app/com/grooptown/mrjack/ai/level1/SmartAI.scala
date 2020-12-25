package com.grooptown.mrjack.ai.level1

import com.grooptown.mrjack.ai.AIPlayer
import com.grooptown.mrjack.game.Game


case class SmartAI(isMrJackVal: Boolean) extends AIPlayer(isMrJackVal) {

  override def getNextMove(game: Game): String = {
    val possibilities = PossibilitiesGenerator.getNextMovePossible(game)
    val allMoveAndScores: Seq[MoveAndScore] = possibilities.map(moves => calculateMoveScore(game, moves))
    val allMoveAndScoresSorted = allMoveAndScores.sortBy(_.score).reverse
    println("Chosing move: " + allMoveAndScoresSorted.head.move + " with score " + allMoveAndScoresSorted.head.score)
    allMoveAndScoresSorted.head.move
  }

  def calculateMoveScore(game: Game, moves: List[String]): MoveAndScore = {
    println("Calculating for move: " + moves)
    val newGame = PossibilityPlayer.playPossibility(game, moves, this)
    val currentScore = minMax(newGame, tokenCountToDepth(game.countUnusedToken()), maximizeAction = false)
    println("For move: " + moves + " score is : " + currentScore)
    MoveAndScore(currentScore, moves.head)
  }

  def tokenCountToDepth = Map(4 -> 2, 3 -> 1, 2 -> 1, 1 -> 0)
//  def tokenCountToDepth = Map(4 -> (2+3), 3 -> (1+3), 2 -> (1+3), 1 -> (0+3))

  def minMax(game: Game, depth: Int, maximizeAction: Boolean): Int = {
    if (depth == 0) {
      getGameValue(game)
    } else if (maximizeAction) {
      getAllChildrenGame(game).map(minMax(_, depth - 1, maximizeAction = false)).max
    } else {
      getAllChildrenGame(game).map(minMax(_, depth - 1, maximizeAction = true)).min
    }
  }

  def getGameValue(game: Game): Int = {
    GameEvaluator.evaluateGame(game, this)
  }

  def getAllChildrenGame(game: Game): List[Game] = {
    val possibilities = PossibilitiesGenerator.getNextMovePossible(game)
    PossibilityPlayer.playPossibilities(game, possibilities, this)
  }
}
