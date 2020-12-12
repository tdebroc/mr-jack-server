package com.grooptown.mrjack.ai

import com.grooptown.mrjack.ai.AIPlayer.buildNewPlayer
import com.grooptown.mrjack.game.Game

object AIService {
  def getNextAiMoveFromUserInput(userInput: String, game: Game): String = {
    val AILevel = userInput.replace("AI_", "")
    buildNewPlayer(AILevel).getNextMove(game)
  }
}
