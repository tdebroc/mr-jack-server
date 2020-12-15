package com.grooptown.mrjack.ai.level1

import com.grooptown.mrjack.actions.ActionService
import com.grooptown.mrjack.actions.behavior.AlibiCardAction
import com.grooptown.mrjack.ai.AIPlayer
import com.grooptown.mrjack.game.Game
import com.grooptown.mrjack.players.AlibiCard
import com.grooptown.mrjack.players.AlibiName.AlibiName

import scala.collection.mutable.ListBuffer

object PossibilityPlayer {

  def playPossibilities(game: Game, possibilities: List[List[String]], aiPlayer: AIPlayer): List[Game] = {
    val gamesGenerated = new ListBuffer[Game]()
    possibilities.foreach(moves => {
      gamesGenerated += playPossibility(game, moves, aiPlayer)
    })
    gamesGenerated.toList
  }

  def playPossibility(game: Game, moves: List[String], aiPlayer: AIPlayer): Game = {
    val shouldHandleEndTurn = game.getUnusedTokens.length == 1
    val newGame = Game.clone(game)
    moves.foreach(move => playOneMove(newGame, move))
    if (shouldHandleEndTurn) {
      handleEndTurn(newGame, aiPlayer)
    }
    newGame
  }


  def playOneMove(game: Game, move: String): Game = {
    val actionDetails = ActionService.getActionDetails(game, move)
    if (actionDetails.action.getActionName.equals(AlibiCardAction.getActionName)) {
      actionDetails.actionToken.isUsed = true
      playMostProbableAlibiCard(game)
    } else {
      game.playAction(actionDetails, shouldHandleEndOfTurn = false)
      game
    }
  }

  def playMostProbableAlibiCard(game: Game): Game = {
    val cards = new ListBuffer[AlibiCard]
    cards ++= game.alibiCards
    if (game.isDetectiveCurrentPlayer) {
      cards += game.mrJackPlayer.alibiCard
      cards ++= game.mrJackPlayer.alibiCards
    }
    val card = pickMostProbableAlibiCard(cards)
    game.alibiCards -= card
    game.getCurrentPlayer.alibiCards += card
    if (game.isDetectiveCurrentPlayer) {
      game.board.innocentAlibi(card.name)
    }
    game
  }

  def pickMostProbableAlibiCard(cards: ListBuffer[AlibiCard]): AlibiCard = {
    val average = cards.map(_.hourGlassCount).sum.toDouble / cards.length.toDouble
    if (average <= 0.5) {
      cards.filter(_.hourGlassCount == 0).head
    } else if (average >= 1.5) {
      cards.filter(_.hourGlassCount == 2).head
    } else {
      cards.filter(_.hourGlassCount == 1).head
    }
  }

  def handleEndTurn(game: Game, aiPlayer: AIPlayer): Game = {
    handleWitnessCall(game, aiPlayer)
    game.winner = game.findWinner()
    game.initTurn()
    game
  }

  def handleWitnessCall(game: Game, aiPlayer: AIPlayer): Game = {
    if (aiPlayer.isPlayerMrJack) {
      if (game == null || game.mrJackPlayer == null || game.mrJackPlayer.alibiCard == null) {
        println("TEST")
      }
      game.witnessCall(game.mrJackPlayer.alibiCard.name)
      game
    } else {
      val guess = detectiveTryingToGuess(game)
      game.witnessCall(guess)
      game
    }
  }

  def detectiveTryingToGuess(game: Game): AlibiName = {
    // By default, we'll say Jack has succeed to remain hidden.
    // If there is too many visible characters, we take a visible character.
    val allPossibleAlibis = game.board.getDistricts.map(_.name)
    val visibleCells = game.board.calculateVisibleCellsFromAllDetective
    val visibleAlibis = visibleCells.map(_.district.get).filter(_.isRecto).map(_.name).toSet
    val weThinkMrJackIsVisible = visibleAlibis.size == allPossibleAlibis.size || visibleAlibis.size * 2 > allPossibleAlibis.size
    if (weThinkMrJackIsVisible) {
      visibleAlibis.head
    } else {
      game.board.getNonVisibleDistrictsFromVisibleCells(visibleCells).head.name
    }
  }
}
