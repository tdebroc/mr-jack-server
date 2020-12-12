package com.grooptown.mrjack.actions.behavior

import com.grooptown.mrjack.actions.exceptions.WrongInputException
import com.grooptown.mrjack.actions.input.{ActionInput, MoveDetectiveInput}
import com.grooptown.mrjack.board.DetectiveName
import com.grooptown.mrjack.board.DetectiveName.DetectiveName
import com.grooptown.mrjack.board.District.districtIdToName
import com.grooptown.mrjack.game.Game

import scala.collection.mutable

trait MoveDetectiveAction extends Action {
  override def getInputFromString(moveCount: String): ActionInput = {
    if (moveCount.isEmpty || !moveCount.charAt(0).isDigit) throw WrongInputException("You should enter a valid digit")
    MoveDetectiveInput(getDetective, moveCount.charAt(0).asDigit)
  }

  override def isValidAction(actionInput: ActionInput, game: Game): Boolean = {
    val moveDetectiveInput = actionInput.asInstanceOf[MoveDetectiveInput]
    if (moveDetectiveInput.moveCount > 0 && moveDetectiveInput.moveCount <= 2) true
    else throw WrongInputException("Your move should be between 1 and 2")
  }

  override def generatePossibleInputString(game: Game): Array[String] = Array("1", "2")

  override def playAction(actionInput: ActionInput, game: Game): Unit = {
    val moveDetectiveInput = actionInput.asInstanceOf[MoveDetectiveInput]
    game.board.moveDetective(getDetective, moveDetectiveInput.moveCount)
    game.addMessageToHistory(getDetective.toString + " moved " + moveDetectiveInput.moveCount + " position(s).")
  }

  def getDetective: DetectiveName
}
