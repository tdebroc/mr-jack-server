package com.grooptown.mrjack.ai.random

import com.grooptown.mrjack.actions.ActionService
import com.grooptown.mrjack.actions.tokens.ActionToken
import com.grooptown.mrjack.ai.AIPlayer
import com.grooptown.mrjack.game.Game

import scala.util.Random

class RandomAI extends AIPlayer {

  override def getNextMove(game: Game): String = {
    val tokensToUse = game.getActionTokens.filter(!_.isUsed)
    val tokenChosenIndex = Random.nextInt(tokensToUse.length)
    val tokenChosen: ActionToken = tokensToUse(tokenChosenIndex)

    val inputStrings = tokenChosen.getCurrentAction.generatePossibleInputString(game)
    val inputString = inputStrings(Random.nextInt(inputStrings.length))

    tokenChosenIndex + "_" + inputString
  }
}
