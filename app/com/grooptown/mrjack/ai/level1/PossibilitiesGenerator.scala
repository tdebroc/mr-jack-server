package com.grooptown.mrjack.ai.level1

import com.grooptown.mrjack.actions.behavior.RotateAction
import com.grooptown.mrjack.actions.tokens.ActionToken
import com.grooptown.mrjack.game.Game

import scala.collection.mutable.ListBuffer

object PossibilitiesGenerator {

  def getNextMovePossible(game: Game): List[List[String]] = {
    val playableTokens = game.getActionTokens.filter(!_.isUsed)
    if (playableTokens.length != 3) {
      playableTokens
        .zipWithIndex
        .flatMap { case (token, index) => token.getCurrentAction.generatePossibleInputString(game).map(move => index + "_" + move) }
        .map(List(_)).toList
    } else {
      val moves = new ListBuffer[List[String]]()
      moves ++= generateCombinations(game, playableTokens, 0, 1)
      moves ++= generateCombinations(game, playableTokens, 0, 2)
      moves ++= generateCombinations(game, playableTokens, 1, 2)
      moves.toList
    }
  }

  def generateCombinations(game: Game, playableTokens: Array[ActionToken], index1: Int, index2: Int): List[List[String]] = {
    val moves1 = playableTokens(index1).getCurrentAction.generatePossibleInputString(game).map(index1 + "_" + _)
    val moves2 = playableTokens(index2).getCurrentAction.generatePossibleInputString(game).map((index2 - 1) + "_" + _)

    (for {
      x <- moves1
      y <- moves2
      if !isRotatingSameDistrict(playableTokens, index1, index2, x, y)
    } yield List(x, y)
      ).toList
  }

  def isRotatingSameDistrict(playableTokens: Array[ActionToken], index1: Int, index2: Int, move1: String, move2: String): Boolean = {
    playableTokens(index1).getCurrentAction.getClass.equals(RotateAction.getClass) &&
      playableTokens(index2).getCurrentAction.getClass.equals(RotateAction.getClass) &&
      move1.charAt(2) == move2.charAt(2)

  }

}
