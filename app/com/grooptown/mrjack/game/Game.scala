package com.grooptown.mrjack.game

import java.util
import java.util.UUID.randomUUID

import com.grooptown.mrjack.actions.tokens._
import com.grooptown.mrjack.actions.{ActionDetails, ActionService}
import com.grooptown.mrjack.board.Board
import com.grooptown.mrjack.players.AlibiName.AlibiName
import com.grooptown.mrjack.players._

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

case class Game() {
  val MAX_TURN = 8
  val board: Board = new Board()
  val alibiCards: mutable.ListBuffer[AlibiCard] = initAlibiCards()
  val turnTokens: mutable.ListBuffer[TurnToken] = initTurnTokens()
  val actionTokens: mutable.ListBuffer[ActionToken] = initActionsToken()
  val detectivePlayer: DetectivePlayer = DetectivePlayer()
  val mrJackPlayer: MrJackPlayer = MrJackPlayer(pickAlibiCard())
  var winner: Option[Player] = Option.empty
  val secrets: util.Map[String, PlayerSecret] = new util.HashMap[String, PlayerSecret]

  def initActionsToken(): ListBuffer[ActionToken] = ListBuffer(new JokerRotateToken, new SherlockAlibiToken, new SwapRotateToken, new WatsonTobbyToken)

  def initTurnTokens(): ListBuffer[TurnToken] = ListBuffer.fill(MAX_TURN)(new TurnToken)

  def initAlibiCards(): ListBuffer[AlibiCard] = AlibiCard.initAlibiCards

  def initGame(): Unit = {
    if (isEvenTurn) detectivePlayer.launchTokenAction(this) else mrJackPlayer.swapActionsToken(this)
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

  def getTurnNumber: Int = Math.abs(turnTokens.length - MAX_TURN)

  def countUnusedToken(): Int = actionTokens.count(!_.isUsed)

  def isDetectiveCurrentPlayer: Boolean = isEvenTurn && (countUnusedToken() == 4 || countUnusedToken() == 1) ||
    !isEvenTurn && (countUnusedToken() == 2 || countUnusedToken() == 3)

  def getCurrentPlayer: Player = if (isDetectiveCurrentPlayer) detectivePlayer else mrJackPlayer

  def checkIfMrJackVisible: Boolean = {
    val visibleCells = board.calculateVisibleCellsFromAllDetective
    visibleCells.exists(_.district.get.name == mrJackPlayer.alibiCard.name)
  }

  def displayMrJack(): Unit = println("MrJack is " + mrJackPlayer.alibiCard + "(" + mrJackPlayer.alibiCard.asChar + ")")

  // ===================================================================================================
  // = Getters for front
  // ===================================================================================================
  def getBoard: Board = board

  def getDetectivePlayer: DetectivePlayer = detectivePlayer

  def getMrJack: MrJackPlayer = mrJackPlayer

  def getActionTokens: Array[ActionToken] = actionTokens.toArray

  def getWinner: String = if (winner.isDefined) winner.get.printName else null

  // ===================================================================================================
  // = Multiplayer
  // ===================================================================================================

  def registerPlayer(pseudo: String, isMrJack: Boolean): String = {
    val uuid = randomUUID().toString
    secrets.put(uuid, PlayerSecret(pseudo, isMrJack, this))
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
  }

  def playTurn(): Unit = {
    println("⏳ Playing Turn " + (MAX_TURN - turnTokens.length + 1) + "")
    initTurn()
    if (isEvenTurn) detectivePlayer.launchTokenAction(this) else mrJackPlayer.swapActionsToken(this)
    1 to 4 foreach { _ => playActionWithKeyboard() }
    witnessCall(mrJackPlayer.alibiCard.name)
  }

  def handleActionPlayed() {
    if (countUnusedToken() != 0) return
    witnessCall(mrJackPlayer.alibiCard.name)
    if (isEvenTurn) detectivePlayer.launchTokenAction(this) else mrJackPlayer.swapActionsToken(this)
    winner = findWinner()
    initTurn()
  }

  def playActionWithKeyboard(): Unit = {
    playAction(askActionFromUserKeyboard)
  }

  def playAction(action: ActionDetails): Unit = {
    action.action.playAction(action.actionInput, this)
    action.actionToken.isUsed = true
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
    println(visibleCells.map(c => c.forPrinting(true)).mkString("Array(", ", ", ")"))
    val isMrJackVisible = visibleCells.exists(_.district.get.name == mrJackName)
    if (isMrJackVisible) {
      detectivePlayer.turnTokens += turnTokens.remove(0)
      board.getNonVisibleDistrictsFromVisibleCells(visibleCells).foreach(_.isRecto = false)
    } else {
      mrJackPlayer.turnTokens += turnTokens.remove(0)
      visibleCells.foreach(_.district.get.isRecto = false)
    }
  }

  // ===================================================================================================
  // = End
  // ===================================================================================================

  def findWinner(): Option[Player] = {
    if (timeEnded() && !checkIfMrJackVisible) return Option.apply(mrJackPlayer)
    if (haveBothObjective && checkIfMrJackVisible) return Option.apply(detectivePlayer)
    if (mrJackHasReachObjectives) return Option.apply(mrJackPlayer)
    if (detectiveHasReachObjectives) return Option.apply(detectivePlayer)
    Option.empty
  }

  def timeEnded(): Boolean = turnTokens.isEmpty

  def hasSomeoneReachObjective: Boolean = detectiveHasReachObjectives || mrJackHasReachObjectives

  def haveBothObjective: Boolean = detectiveHasReachObjectives || mrJackHasReachObjectives

  def detectiveHasReachObjectives: Boolean = detectivePlayer.hasReachObjective(this)

  def mrJackHasReachObjectives: Boolean = mrJackPlayer.hasReachObjective(this)

  def printWinner(): Unit = {
    println("\uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86")
    println("And the Winner is .... " + findWinner().get.printName + " : Congrats ! ")
    println("\uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86 \uD83C\uDF86")
  }
}
