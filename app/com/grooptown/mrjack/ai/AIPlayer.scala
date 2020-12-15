package com.grooptown.mrjack.ai

import com.grooptown.mrjack.actions.ActionService
import com.grooptown.mrjack.ai.level1.SmartAI
import com.grooptown.mrjack.ai.random.RandomAI
import com.grooptown.mrjack.game.Game

abstract class AIPlayer(isMrJack: Boolean) {
  def isPlayerMrJack: Boolean = isMrJack

  def getNextMove(game: Game): String

  def playMove(game: Game): Unit = {
    val nextMove = this.getNextMove(game)
    val actionDetails = ActionService.getActionDetails(game, nextMove)
    if (!actionDetails.isValid) {
      throw new Error("Error while AI. Move is wrong: " + nextMove)
    }
    game.playAction(actionDetails)
  }

  def launchAI(game: Game): Unit = {
    new Thread(new AIThread(this, game)).start()
  }
}

object AIPlayer {
  def buildNewPlayer(aiLevel: String, isMrJack: Boolean): AIPlayer = {
    aiLevel match {
      case "0" => RandomAI(isMrJack)
      case "1" => SmartAI(isMrJack)
      case _ => null
    }
  }
}
