package com.grooptown.mrjack.actions.behavior

import com.grooptown.mrjack.actions.exceptions.WrongInputException
import com.grooptown.mrjack.actions.input.{ActionInput, SwapDistrictInput}
import com.grooptown.mrjack.board.District.districtIdToName
import com.grooptown.mrjack.board.{Board, District}
import com.grooptown.mrjack.game.{Game, Position}

object SwapDistrictAction extends Action {
  override def getInputFromString(move: String): ActionInput = {
    if (move.length != 3) throw WrongInputException("the input should be a length of 3.")
    if (!move.contains("-")) throw WrongInputException("the input should contain a -")
    val char1 = move.split("-")(0).charAt(0)
    val char2 = move.split("-")(1).charAt(0)
    if (!char1.isDigit || !char2.isDigit) throw WrongInputException("please enter valid digits.")
    SwapDistrictInput(char1.asDigit, char2.asDigit)
  }

  override def isValidAction(actionInput: ActionInput, game: Game): Boolean = {
    val swapInput = actionInput.asInstanceOf[SwapDistrictInput]
    if (swapInput.district1 != swapInput.district2 &&
      swapInput.district1 >= 0 && swapInput.district1 < 9 &&
      swapInput.district2 >= 0 && swapInput.district2 < 9) true
    else throw WrongInputException("Your district ID should be between 0 and 8 inclusive")
  }

  override def generatePossibleInputString(game: Game): Array[String] = {
    Array(
      "0-1", "0-2", "0-3", "0-4", "0-5", "0-6", "0-7", "0-8",
      "1-2", "1-3", "1-4", "1-5", "1-6", "1-7", "1-8",
      "2-3", "2-4", "2-5", "2-6", "2-7", "2-8",
      "3-4", "3-5", "3-6", "3-7", "3-8",
      "4-5", "4-6", "4-7", "4-8",
      "5-6", "5-7", "5-8",
      "6-7", "6-8",
      "7-8")
  }

  override def playAction(actionInput: ActionInput, game: Game): Unit = {
    val swapDistrictInput: SwapDistrictInput = actionInput.asInstanceOf[SwapDistrictInput]
    val district1: District = game.board.getDistricts(swapDistrictInput.district1)
    val district2: District = game.board.getDistricts(swapDistrictInput.district2)
    val district1Pos: Position = Board.districtIdToPosition(swapDistrictInput.district1)
    val district2Pos: Position = Board.districtIdToPosition(swapDistrictInput.district2)
    game.board.cells(district2Pos.line)(district2Pos.col).district = Option.apply(district1)
    game.board.cells(district1Pos.line)(district1Pos.col).district = Option.apply(district2)
    game.addMessageToHistory("District " + districtIdToName(swapDistrictInput.district1) +
      " switched with " + districtIdToName(swapDistrictInput.district2))
  }
}
