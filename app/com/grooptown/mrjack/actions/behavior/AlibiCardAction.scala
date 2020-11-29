package com.grooptown.mrjack.actions.behavior

import com.grooptown.mrjack.actions.input.ActionInput
import com.grooptown.mrjack.game.Game

object AlibiCardAction extends Action {
  override def getInputFromString(move: String): ActionInput = null

  override def isValidAction(actionInput: ActionInput, game: Game): Boolean = true

  override def generatePossibleInputString: Array[String] = Array("")

  override def playAction(actionInput: ActionInput, game: Game): Unit = {
    if (game.isDetectiveCurrentPlayer) {
      game.board.innocentAlibi(game.pickAlibiCard().name)
    } else {
      game.getCurrentPlayer.alibiCards += game.pickAlibiCard()
    }
  }
}
