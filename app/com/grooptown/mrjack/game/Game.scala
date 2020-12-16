package com.grooptown.mrjack.game

import com.grooptown.mrjack.actions.tokens._
import com.grooptown.mrjack.actions.{ActionDetails, ActionService}
import com.grooptown.mrjack.ai.AIPlayer.buildNewPlayer
import com.grooptown.mrjack.board.Board
import com.grooptown.mrjack.players.AlibiName.AlibiName
import com.grooptown.mrjack.players._

import java.util
import java.util.Date
import java.util.UUID.randomUUID
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Game(
                 board: Board = Board.buildBoard(),
                 alibiCards: mutable.ListBuffer[AlibiCard],
                 turnTokens: mutable.ListBuffer[TurnToken],
                 actionTokens: mutable.ListBuffer[ActionToken],
                 detectivePlayer: DetectivePlayer = DetectivePlayer(),
                 mrJackPlayer: MrJackPlayer = MrJackPlayer(),
                 var winner: Option[Player] = Option.empty,
                 secrets: util.Map[String, PlayerSecret] = new util.HashMap[String, PlayerSecret],
                 history: mutable.ListBuffer[String] = new mutable.ListBuffer[String]
               ) {

  val games = new mutable.ListBuffer[Game]

  def initGame(): Unit = {
    mrJackPlayer.alibiCard = pickAlibiCard()
    initTurn()
  }

  def playGame(): Unit = {
    board.printBoard()
    while (findWinner().isEmpty) playTurn()
    printWinner()
  }

  // ===================================================================================================
  // = Utils
  // ===================================================================================================

  def pickAlibiCard(): AlibiCard = alibiCards.remove(0)

  def isEvenTurn: Boolean = turnTokens.length % 2 == 0

  def getTurnNumber: Int = Math.abs(turnTokens.length - Game.MAX_TURN)

  def countUnusedToken(): Int = actionTokens.count(!_.isUsed)

  def getUnusedTokens: mutable.Seq[ActionToken] = actionTokens.filter(!_.isUsed)

  def isDetectiveCurrentPlayer: Boolean = isEvenTurn && (countUnusedToken() == 4 || countUnusedToken() == 1) ||
    !isEvenTurn && (countUnusedToken() == 2 || countUnusedToken() == 3)

  def getCurrentPlayer: Player = if (isDetectiveCurrentPlayer) detectivePlayer else mrJackPlayer

  def checkIfMrJackVisible: Boolean = {
    val visibleCells = board.calculateVisibleCellsFromAllDetective
    visibleCells.exists(_.district.get.name == mrJackPlayer.alibiCard.name)
  }

  def displayMrJack(): Unit = println("MrJack is " + mrJackPlayer.alibiCard + "(" + mrJackPlayer.alibiCard.asChar + ")")

  def addMessageToHistory(message: String): Unit = {
    history += new Date + " : " + message
  }

  // ===================================================================================================
  // = Display - Getters for front
  // ===================================================================================================
  def getBoard: Board = board

  def getDetectivePlayer: DetectivePlayer = detectivePlayer

  def getMrJack: MrJackPlayer = mrJackPlayer

  def getActionTokens: Array[ActionToken] = actionTokens.toArray

  def getWinner: String = if (winner.isDefined) winner.get.printName else null

  def getHistory: Array[String] = history.toArray

  def getGames: Array[Game] = games.toArray

  // ===================================================================================================
  // = Multiplayer
  // ===================================================================================================

  def registerPlayer(pseudo: String, isMrJack: Boolean): String = {
    val uuid = randomUUID().toString
    secrets.put(uuid, PlayerSecret(pseudo, isMrJack, this))
    uuid
  }

  def registerAIPlayer(aiLevel: String,
                       isMrJack: Boolean): String = {
    val uuid = registerPlayer("AI_" + isMrJack + "_" + aiLevel, isMrJack)
    val aiPlayer = buildNewPlayer(aiLevel, isMrJack)
    if (isMrJack) mrJackPlayer.aiBrain = Option.apply(aiPlayer)
    if (!isMrJack) detectivePlayer.aiBrain = Option.apply(aiPlayer)
    aiPlayer.launchAI(this)
    uuid
  }

  def isMrJackRegistered: Boolean = {
    secrets.values().asScala.exists(_.isMrJack)
  }

  def isDetectiveRegistered: Boolean = {
    secrets.values().asScala.exists(!_.isMrJack)
  }

  def getPlayerSecret(secretId: String): PlayerSecret = secrets.get(secretId)

  def isAuthorizedToPlay(secretId: String): Boolean = {
    if (!secrets.containsKey(secretId)) return false
    val secret = secrets.get(secretId)
    secret.isMrJack && !isDetectiveCurrentPlayer || !secret.isMrJack && isDetectiveCurrentPlayer
  }

  // ===================================================================================================
  // = One Turn
  // ===================================================================================================

  def initTurn(): Unit = {
    actionTokens.foreach(_.isUsed = false)
    board.getDistricts.foreach(_.isAlreadyRotated = false)
    if (isEvenTurn) detectivePlayer.launchTokenAction(this) else mrJackPlayer.swapActionsToken(this)
  }

  def playTurn(): Unit = {
    println("⏳ Playing Turn " + (Game.MAX_TURN - turnTokens.length + 1) + "")
    initTurn()
    1 to 4 foreach { _ => playActionWithKeyboard() }
    witnessCall(mrJackPlayer.alibiCard.name)
  }

  def handleEndOfTurn() {
    if (countUnusedToken() != 0) return
    witnessCall(mrJackPlayer.alibiCard.name)
    winner = findWinner()
    if (winner.nonEmpty) {
      addMessageToHistory("Congrats we have a winner ! Winner is ... " + winner.get.printName)
      return
    }
    initTurn()
    addMessageToHistory("This is now Turn : " + getTurnNumber)
  }

  def playActionWithKeyboard(): Unit = {
    playAction(askActionFromUserKeyboard)
  }

  /*
  def playAIIfNecessary(): Unit = {
    if (winner.isEmpty) return
    if (isDetectiveCurrentPlayer && detectivePlayer.isAI) detectivePlayer.aiBrain.getNextMove(this)
    if (!isDetectiveCurrentPlayer && mrJackPlayer.isAI) mrJackPlayer.aiBrain.getNextMove(this)
  }
  */

  def playAction(action: ActionDetails, shouldHandleEndOfTurn: Boolean = true): Unit = {
    games += Game.clone(game = this)
    addActionPlayedToHistory(action)
    action.action.playAction(action.actionInput, game = this)
    action.actionToken.isUsed = true
    if (shouldHandleEndOfTurn) handleEndOfTurn()
  }

  def addActionPlayedToHistory(action: ActionDetails): Unit = {
    addMessageToHistory(getCurrentPlayer.printName + " plays: " + action.action.getActionName.replace("$", ""))
  }

  def askActionFromUserKeyboard: ActionDetails = {
    var action: String = ""
    var actionDetails: ActionDetails = null
    do {
      board.printBoard()
      displayToken()
      println(getCurrentPlayer.printName + " what do you want to play ?")
      action = scala.io.StdIn.readLine()
      actionDetails = ActionService.getActionDetails(this, action)
      if (!actionDetails.isValid) println("Action is Not valid !!")
    } while (!actionDetails.isValid)
    actionDetails
  }

  def displayToken(): Unit = {
    println("Possible actions are : ")
    actionTokens.filter(!_.isUsed).map(_.getCurrentAction)
      .zipWithIndex
      .foreach(t => println(" - " + t._2 + " : " + t._1.getClass.getSimpleName))
  }

  def witnessCall(mrJackName: AlibiName): Unit = {
    val visibleCells = board.calculateVisibleCellsFromAllDetective
    // println("Visible cells: " + visibleCells.map(c => c.forPrinting(true)).mkString("Array(", ", ", ")"))
    val isMrJackVisible = visibleCells.exists(_.district.get.name == mrJackName)
    addMessageToHistory("Witness call: MrJack is " + (if (isMrJackVisible) "" else "not") + " visible")
    if (isMrJackVisible) {
      detectivePlayer.turnTokens += turnTokens.remove(0)
      val nonVisibleDistricts = board.getNonVisibleDistrictsFromVisibleCells(visibleCells)
      val notMrJack = nonVisibleDistricts.filter(_.isRecto).map(_.name.toString).toSet
      nonVisibleDistricts.foreach(_.reverseDistrict())
      addMessageToHistory("They are not MrJack : " + notMrJack.mkString("(", ", ", ")"))
    } else {
      mrJackPlayer.turnTokens += turnTokens.remove(0)
      val notMrJack = visibleCells.filter(_.district.get.isRecto).map(_.district.get.name.toString).toSet
      visibleCells.foreach(_.district.get.reverseDistrict())
      addMessageToHistory("They are not MrJack : " + notMrJack.mkString("(", ", ", ")"))
    }
  }

  // ===================================================================================================
  // = End
  // ===================================================================================================

  def findWinner(): Option[Player] = {
    if (timeEnded() && !checkIfMrJackVisible) return Option.apply(mrJackPlayer)
    if (haveBothObjective && checkIfMrJackVisible) return Option.apply(detectivePlayer)
    if (haveBothObjective) {
      addMessageToHistory("Both have reach their objectives, we are in Chase Mode ! MrJack must stay unseen until Turn 8 to win.")
      return Option.empty
    }
    if (mrJackHasReachObjectives) return Option.apply(mrJackPlayer)
    if (detectiveHasReachObjectives) return Option.apply(detectivePlayer)
    Option.empty
  }

  def timeEnded(): Boolean = turnTokens.isEmpty

  def hasSomeoneReachObjective: Boolean = detectiveHasReachObjectives || mrJackHasReachObjectives

  def haveBothObjective: Boolean = detectiveHasReachObjectives && mrJackHasReachObjectives

  def detectiveHasReachObjectives: Boolean = detectivePlayer.hasReachObjective(this)

  def mrJackHasReachObjectives: Boolean = mrJackPlayer.hasReachObjective(this)

  def printWinner(): Unit = {
    println("\uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86")
    println("And the Winner is .... " + findWinner().get.printName + " : Congrats ! ")
    println("\uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86")
  }
}

object Game {
  val MAX_TURN: Int = 8

  def buildNewGame: Game = {
    new Game(
      alibiCards = initAlibiCards(),
      turnTokens = initTurnTokens(),
      actionTokens = initActionsToken()
    )
  }

  def initActionsToken(): ListBuffer[ActionToken] = ListBuffer(
    new JokerRotateToken,
    new SherlockAlibiToken,
    new SwapRotateToken,
    new WatsonTobbyToken)

  def initTurnTokens(): ListBuffer[TurnToken] = ListBuffer.fill(MAX_TURN)(new TurnToken)

  def initAlibiCards(): ListBuffer[AlibiCard] = AlibiCard.initAlibiCards

  def clone(game: Game): Game = {
    game.copy(
      board = game.board.clone(),
      mrJackPlayer = game.mrJackPlayer.copyPlayer,
      detectivePlayer = game.detectivePlayer.copyPlayer,
      alibiCards = game.alibiCards.map(_.copy()),
      turnTokens = game.turnTokens.map(_.clone()),
      actionTokens = game.actionTokens.map(_.copyToken),
      winner = if (game.winner.isEmpty) Option.empty else Option.apply(game.winner.get.copyPlayer),
      history = game.history.map(identity)
    )
  }
}