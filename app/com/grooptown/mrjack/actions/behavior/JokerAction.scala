package com.grooptown.mrjack.actions.behavior

import com.grooptown.mrjack.actions.exceptions.WrongInputException
import com.grooptown.mrjack.actions.input.{ActionInput, JokerInput}
import com.grooptown.mrjack.board.DetectiveName
import com.grooptown.mrjack.game.Game

object JokerAction extends Action {
  override def getInputFromString(move: String): ActionInput = {
    if (move.equals("NOTHING")) return JokerInput(null, doNothing = true)
    val detectiveFound = DetectiveName.values.find(_.toString == move)
    if (detectiveFound.nonEmpty) return JokerInput(detectiveFound.get, doNothing = false)
    throw WrongInputException("Input String is Wrong")
  }

  override def isValidAction(actionInput: ActionInput, game: Game): Boolean = {
    val jokerInput = actionInput.asInstanceOf[JokerInput]
    if (jokerInput.doNothing && game.isDetectiveCurrentPlayer) throw WrongInputException("A detective can't do nothing with Joker.")
    true
  }

  override def generatePossibleInputString: Array[String] = {
    Array.concat(Array("NOTHING"), DetectiveName.values.map(_.toString).toArray)
  }

  override def playAction(actionInput: ActionInput, game: Game): Unit = {
    val jokerInput = actionInput.asInstanceOf[JokerInput]
    if (jokerInput.doNothing) return
    game.board.moveDetective(jokerInput.detectiveName, 1)
  }
}
