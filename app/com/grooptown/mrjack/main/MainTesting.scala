package com.grooptown.mrjack.main

import com.grooptown.mrjack.actions.ActionDetails
import com.grooptown.mrjack.actions.behavior.{AlibiCardAction, MoveTobbyAction, RotateAction}
import com.grooptown.mrjack.actions.input.{MoveDetectiveInput, RotateInput}
import com.grooptown.mrjack.actions.tokens.{JokerRotateToken, SherlockAlibiToken, SwapRotateToken, WatsonTobbyToken}
import com.grooptown.mrjack.board.DetectiveName.TOBBY
import com.grooptown.mrjack.board.Orientation.NORTH
import com.grooptown.mrjack.game.Game

object MainTesting {

  def main(args: Array[String]): Unit = {
    val game: Game = new Game
    game.board.printBoard()
    println("MrJack is " + game.mrJackPlayer.alibiCard + " : " + game.mrJackPlayer.alibiCard.asChar)
    game.initTurn()
    val action1 = ActionDetails(isValid = true, AlibiCardAction, null, new SherlockAlibiToken)
    action1.action.playAction(action1.actionInput, game)
    val action2 = ActionDetails(isValid = true, MoveTobbyAction, MoveDetectiveInput(TOBBY, 1), new WatsonTobbyToken)
    game.board.printBoard()
    action2.action.playAction(action2.actionInput, game)
    game.board.printBoard()
    val action3 = ActionDetails(isValid = true, RotateAction, RotateInput(0, NORTH), new JokerRotateToken)
    action3.action.playAction(action3.actionInput, game)
    game.board.printBoard()
    val action4 = ActionDetails(isValid = true, RotateAction, RotateInput(1, NORTH), new SwapRotateToken)
    action4.action.playAction(action4.actionInput, game)
    game.board.printBoard()
    game.witnessCall()
    game.board.printBoard()
    game.playTurn()

  }
}
