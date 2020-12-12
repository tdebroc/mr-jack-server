package com.grooptown.mrjack.actions.behavior

import com.grooptown.mrjack.actions.input.ActionInput
import com.grooptown.mrjack.game.Game

trait Action {
  def getInputFromString(move: String): ActionInput

  def isValidAction(actionInput: ActionInput, game: Game): Boolean

  def generatePossibleInputString(game: Game): Array[String]

  def playAction(actionInput: ActionInput, game: Game): Unit

  def getActionName: String = this.getClass.getSimpleName
}
