package com.grooptown.mrjack.actions.behavior

import com.grooptown.mrjack.actions.exceptions.WrongInputException
import com.grooptown.mrjack.actions.input.{ActionInput, RotateInput}
import com.grooptown.mrjack.board.Orientation
import com.grooptown.mrjack.game.Game

import scala.collection.mutable

object RotateAction extends Action {
  override def getInputFromString(move: String): RotateInput = {
    if (!move.contains("-")) throw WrongInputException("Rotate Action should contain a -")
    val districtId = move.split("-")(0)
    if (districtId.length == 0 || !districtId.charAt(0).isDigit) throw WrongInputException("You should enter a valid digit")
    val orientationInput = move.split("-")(1)
    val orientationFound = Orientation.values.find(_.toString == orientationInput)
    if  (orientationFound.isEmpty) throw WrongInputException("This isn't a valid detective : " + orientationInput)
    RotateInput(districtId.charAt(0).asDigit, orientationFound.get)
  }

  override def isValidAction(actionInput: ActionInput, game: Game): Boolean = {
    val input = actionInput.asInstanceOf[RotateInput]
    if (game.board.getDistricts(input.districtId).isAlreadyRotated) {
      println("District has already been rotated")
      return false
    }
    true
  }

  override def generatePossibleInputString: Array[String] = {
    val moves = new mutable.ListBuffer[String]()
    0 to 8 foreach (districtId => {
      Orientation.values.map(_.toString).foreach(orientation => {
        moves += districtId + "-" + orientation
      })
    })
    moves.toArray
  }

  override def playAction(actionInput: ActionInput, game: Game): Unit = {
    val rotateInput = actionInput.asInstanceOf[RotateInput]
    game.board.getDistricts(rotateInput.districtId).orientation = rotateInput.orientation
    game.board.getDistricts(rotateInput.districtId).isAlreadyRotated = true
  }
}
