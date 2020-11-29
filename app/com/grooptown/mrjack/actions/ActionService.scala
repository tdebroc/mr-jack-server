package com.grooptown.mrjack.actions

import com.grooptown.mrjack.actions.exceptions.WrongInputException
import com.grooptown.mrjack.game.Game

object ActionService {


  def getActionDetails(game: Game, action: String): ActionDetails = {
    if (action.length == 0 || !action.charAt(0).isDigit) {
      println("the action index should be a digit")
      return ActionDetails(isValid = false)
    }
    val actionIndex = action.charAt(0).asDigit
    if (actionIndex < 0 || actionIndex >= game.countUnusedToken()) {
      println("the action index should be between 0 and " + game.countUnusedToken())
      return ActionDetails(isValid = false)
    }
    val actionToken = game.actionTokens.filter(!_.isUsed)(actionIndex)
    val actionInputString = if (action.length >= 2) action.substring(2) else ""
    try {
      val inputAction = actionToken.getCurrentAction.getInputFromString(actionInputString)
      val isValidMove = actionToken.getCurrentAction.isValidAction(inputAction, game)
      ActionDetails(isValid = isValidMove, actionToken.getCurrentAction, inputAction, actionToken)
    } catch {
      case e: WrongInputException => handleWrongInputException(e)
    }
  }

  def handleWrongInputException(e: WrongInputException) : ActionDetails = {
    println("Input is Wrong: " + e.msg)
    ActionDetails(isValid = false)
  }
}
