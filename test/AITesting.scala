import com.grooptown.mrjack.actions.ActionService
import com.grooptown.mrjack.ai.level1.SmartAI
import com.grooptown.mrjack.board.DetectiveName.{SHERLOCK, TOBBY, WATSON}
import com.grooptown.mrjack.board.DetectiveToken
import com.grooptown.mrjack.board.Orientation.{EAST, NORTH, SOUTH, WEST}
import com.grooptown.mrjack.game.Game
import com.grooptown.mrjack.players.{AlibiCard, AlibiName}
import org.junit.Test

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date


class AITesting {

  @Test def aiTest(): Unit = {
    val game: Game = Game.buildNewGame()
    game.initGame()
    game.board.cells(1)(1).district.get.orientation = NORTH
    game.board.cells(1)(1).district.get.isRecto = false
    game.board.cells(1)(2).district.get.orientation = WEST
    game.board.cells(1)(2).district.get.isRecto = false
    game.board.cells(1)(3).district.get.orientation = NORTH
    game.board.cells(1)(3).district.get.isRecto = false
    game.board.cells(2)(1).district.get.orientation = EAST
    game.board.cells(2)(1).district.get.isRecto = false
    game.board.cells(2)(2).district.get.orientation = EAST
    game.board.cells(2)(2).district.get.isRecto = false
    game.board.cells(2)(2).district.get.isCross = false
    game.board.cells(2)(3).district.get.orientation = SOUTH
    game.board.cells(2)(3).district.get.isRecto = true
    game.board.cells(3)(1).district.get.isCross = true
    game.board.cells(3)(1).district.get.isRecto = false
    game.board.cells(3)(2).district.get.orientation = SOUTH
    game.board.cells(3)(2).district.get.isRecto = false
    game.board.cells(3)(3).district.get.orientation = NORTH
    game.board.cells(3)(3).district.get.isRecto = true

    game.board.cells(1)(0).detectives.get.remove(0)
    game.board.cells(1)(4).detectives.get += DetectiveToken(SHERLOCK)

    game.board.cells(1)(4).detectives.get.remove(0)
    game.board.cells(2)(0).detectives.get += DetectiveToken(WATSON)

    game.board.cells(4)(2).detectives.get.remove(0)
    game.board.cells(0)(2).detectives.get += DetectiveToken(TOBBY)

    game.board.printBoard()
    val mrJack = game.board.cells(3)(3).district.get.name
    game.mrJackPlayer.alibiCard = new AlibiCard(mrJack, 1)
    println("MrJack " + AlibiName.toChar(mrJack))
    game.actionTokens.head.isUsed = false
    game.actionTokens.head.isRecto = true
    game.actionTokens(1).isUsed = true
    game.actionTokens(1).isRecto = true
    game.actionTokens(2).isUsed = true
    game.actionTokens(2).isRecto = false
    game.actionTokens(3).isUsed = false
    game.actionTokens(3).isRecto = true
    game.displayToken()
    val aiPlayer = SmartAI(true)
    println("Turn of " + game.getCurrentPlayer.printName)
    val move = aiPlayer.getNextMove(game)
    println(move)
    assert(move.startsWith("0"))

  }


  @Test def aiTestShouldGetCard(): Unit = {
    val game: Game = Game.buildNewGame()
    game.initGame()
    game.board.cells(1)(1).district.get.orientation = SOUTH
    game.board.cells(1)(1).district.get.isRecto = false
    game.board.cells(1)(2).district.get.orientation = WEST
    game.board.cells(1)(2).district.get.isRecto = true
    game.board.cells(1)(3).district.get.orientation = WEST
    game.board.cells(1)(3).district.get.isRecto = true
    game.board.cells(2)(1).district.get.orientation = WEST
    game.board.cells(2)(1).district.get.isRecto = false
    game.board.cells(2)(2).district.get.orientation = SOUTH
    game.board.cells(2)(2).district.get.isRecto = false
    game.board.cells(2)(2).district.get.isCross = false
    game.board.cells(2)(3).district.get.orientation = WEST
    game.board.cells(2)(3).district.get.isRecto = true
    // game.board.cells(3)(1).district.get.isCross = true
    game.board.cells(3)(1).district.get.isRecto = false
    game.board.cells(3)(2).district.get.orientation = NORTH
    game.board.cells(3)(2).district.get.isRecto = false
    game.board.cells(3)(3).district.get.orientation = EAST
    game.board.cells(3)(3).district.get.isRecto = false

    game.board.cells(1)(0).detectives.get.remove(0)
    game.board.cells(0)(1).detectives.get += DetectiveToken(SHERLOCK)

    game.board.cells(1)(4).detectives.get.remove(0)
    game.board.cells(3)(4).detectives.get += DetectiveToken(WATSON)

    game.board.cells(4)(2).detectives.get.remove(0)
    game.board.cells(2)(0).detectives.get += DetectiveToken(TOBBY)

    game.board.printBoard()
    val mrJack = game.board.cells(3)(3).district.get.name
    game.mrJackPlayer.alibiCard = new AlibiCard(mrJack, 1)
    println("MrJack " + AlibiName.toChar(mrJack))
    // JokerRotate
    game.actionTokens.head.isUsed = true
    game.actionTokens.head.isRecto = true
    // SherlockAlibi
    game.actionTokens(1).isUsed = false
    game.actionTokens(1).isRecto = false
    // SwapRotate
    game.actionTokens(2).isUsed = true
    game.actionTokens(2).isRecto = false
    // WatsonTobby
    game.actionTokens(3).isUsed = false
    game.actionTokens(3).isRecto = false
    game.displayToken()
    val aiPlayer = SmartAI(true)
    println("Turn of " + game.getCurrentPlayer.printName)
    val move = aiPlayer.getNextMove(game)
    println(move)
    assert(move.startsWith("0"))

  }


  @Test def aiTestShouldNotTakeSherlock(): Unit = {
    val game: Game = Game.buildNewGame()
    game.initGame()
    game.board.cells(1)(1).district.get.orientation = SOUTH
    game.board.cells(1)(1).district.get.isRecto = false
    game.board.cells(1)(2).district.get.orientation = SOUTH
    game.board.cells(1)(2).district.get.isRecto = true
    game.board.cells(1)(3).district.get.orientation = EAST
    game.board.cells(1)(3).district.get.isRecto = true

    game.board.cells(2)(1).district.get.orientation = WEST
    game.board.cells(2)(1).district.get.isRecto = false
    game.board.cells(2)(2).district.get.orientation = WEST
    game.board.cells(2)(2).district.get.isRecto = true
    game.board.cells(2)(3).district.get.orientation = NORTH
    game.board.cells(2)(3).district.get.isRecto = true

    game.board.cells(3)(1).district.get.orientation = EAST
    game.board.cells(3)(1).district.get.isRecto = false
    game.board.cells(3)(2).district.get.orientation = WEST
    game.board.cells(3)(2).district.get.isRecto = true
    game.board.cells(3)(3).district.get.orientation = WEST
    game.board.cells(3)(3).district.get.isRecto = true

    game.board.cells(1)(0).detectives.get.remove(0)
    game.board.cells(0)(1).detectives.get += DetectiveToken(SHERLOCK)

    game.board.cells(1)(4).detectives.get.remove(0)
    game.board.cells(1)(4).detectives.get += DetectiveToken(WATSON)

    game.board.cells(4)(2).detectives.get.remove(0)
    game.board.cells(4)(1).detectives.get += DetectiveToken(TOBBY)

    game.board.printBoard()
    val mrJack = game.board.cells(3)(3).district.get.name
    game.mrJackPlayer.alibiCard = new AlibiCard(mrJack, 1)

    println("MrJack " + AlibiName.toChar(mrJack))
    // JokerRotate
    game.actionTokens.head.isUsed = false
    game.actionTokens.head.isRecto = false
    // SherlockAlibi
    game.actionTokens(1).isUsed = false
    game.actionTokens(1).isRecto = true
    // SwapRotate
    game.actionTokens(2).isUsed = false
    game.actionTokens(2).isRecto = false
    // WatsonTobby
    game.actionTokens(3).isUsed = false
    game.actionTokens(3).isRecto = true
    game.displayToken()
    // Turn 1:
    game.turnTokens.remove(0)
    val aiPlayer = SmartAI(true)
    println("Turn of " + game.getCurrentPlayer.printName)
    val startTime = getCurrentMicroSec
    val move = aiPlayer.getNextMove(game)
    println("Time taken: " + (getCurrentMicroSec - startTime))
    println(move)
    // assert(move.startsWith("0"))
  }

  def getCurrentMicroSec: Long = Instant.now().truncatedTo(ChronoUnit.MICROS).toEpochMilli

  @Test def aiTestShouldNotBlock(): Unit = {
    val game: Game = Game.buildNewGame()
    game.initGame()
    game.board.cells(1)(1).district.get.orientation = EAST
    game.board.cells(1)(1).district.get.isRecto = false
    game.board.cells(1)(2).district.get.orientation = SOUTH
    game.board.cells(1)(2).district.get.isRecto = false
    game.board.cells(1)(3).district.get.orientation = EAST
    game.board.cells(1)(3).district.get.isRecto = false

    game.board.cells(2)(1).district.get.orientation = WEST
    game.board.cells(2)(1).district.get.isRecto = false
    game.board.cells(2)(2).district.get.orientation = SOUTH
    game.board.cells(2)(2).district.get.isRecto = false
    game.board.cells(2)(3).district.get.orientation = WEST
    game.board.cells(2)(3).district.get.isRecto = true

    game.board.cells(3)(1).district.get.orientation = NORTH
    game.board.cells(3)(1).district.get.isRecto = true
    game.board.cells(3)(2).district.get.orientation = WEST
    game.board.cells(3)(2).district.get.isRecto = false
    game.board.cells(3)(3).district.get.orientation = NORTH
    game.board.cells(3)(3).district.get.isRecto = true

    game.board.cells(1)(0).detectives.get.remove(0)
    game.board.cells(0)(1).detectives.get += DetectiveToken(SHERLOCK)

    game.board.cells(1)(4).detectives.get.remove(0)
    game.board.cells(1)(4).detectives.get += DetectiveToken(WATSON)

    game.board.cells(4)(2).detectives.get.remove(0)
    game.board.cells(4)(1).detectives.get += DetectiveToken(TOBBY)

    game.board.printBoard()
    val mrJack = game.board.cells(2)(3).district.get.name
    game.mrJackPlayer.alibiCard = new AlibiCard(mrJack, 1)

    println("MrJack " + AlibiName.toChar(mrJack))
    // JokerRotate
    game.actionTokens.head.isUsed = true
    game.actionTokens.head.isRecto = false
    // SherlockAlibi
    game.actionTokens(1).isUsed = true
    game.actionTokens(1).isRecto = true
    // SwapRotate
    game.actionTokens(2).isUsed = true
    game.actionTokens(2).isRecto = false
    // WatsonTobby
    game.actionTokens(3).isUsed = false
    game.actionTokens(3).isRecto = true
    game.displayToken()
    // Turn 1:
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.alibiCards += new AlibiCard(mrJack, 1)
    game.mrJackPlayer.alibiCards += new AlibiCard(mrJack, 1)
    val aiPlayer = SmartAI(false)
    println("Turn number " + game.getTurnNumber)
    println("Turn of " + game.getCurrentPlayer.printName)
    val startTime = (new Date).getTime
    val move = aiPlayer.getNextMove(game)
    println("Time taken: " + ((new Date).getTime - startTime))
    game.playAction(ActionService.getActionDetails(game, move))
    println(move)
    // assert(move.startsWith("0"))
  }


  @Test def aiTestInChaseMode(): Unit = {
    val game: Game = Game.buildNewGame()
    game.initGame()
    game.board.cells(1)(1).district.get.orientation = WEST
    game.board.cells(1)(1).district.get.isRecto = false
    game.board.cells(1)(2).district.get.orientation = WEST
    game.board.cells(1)(2).district.get.isRecto = false
    game.board.cells(1)(3).district.get.orientation = EAST
    game.board.cells(1)(3).district.get.isRecto = false

    game.board.cells(2)(1).district.get.orientation = SOUTH
    game.board.cells(2)(1).district.get.isRecto = false
    game.board.cells(2)(2).district.get.orientation = SOUTH
    game.board.cells(2)(2).district.get.isRecto = true
    game.board.cells(2)(3).district.get.orientation = SOUTH
    game.board.cells(2)(3).district.get.isCross = true
    game.board.cells(2)(3).district.get.isRecto = false

    game.board.cells(3)(1).district.get.orientation = SOUTH
    game.board.cells(3)(1).district.get.isRecto = false
    game.board.cells(3)(2).district.get.orientation = SOUTH
    game.board.cells(3)(2).district.get.isRecto = false
    game.board.cells(3)(3).district.get.orientation = WEST
    game.board.cells(3)(3).district.get.isRecto = false

    game.board.cells(1)(0).detectives.get.remove(0)
    game.board.cells(0)(3).detectives.get += DetectiveToken(SHERLOCK)

    game.board.cells(1)(4).detectives.get.remove(0)
    game.board.cells(4)(2).detectives.get += DetectiveToken(WATSON)

    game.board.cells(4)(2).detectives.get.remove(0)
    game.board.cells(0)(1).detectives.get += DetectiveToken(TOBBY)

    game.board.printBoard()
    val mrJack = game.board.cells(2)(2).district.get.name
    game.mrJackPlayer.alibiCard = new AlibiCard(mrJack, 1)

    println("MrJack " + AlibiName.toChar(mrJack))
    // JokerRotate
    game.actionTokens.head.isUsed = true
    game.actionTokens.head.isRecto = false
    // SherlockAlibi
    game.actionTokens(1).isUsed = false
    game.actionTokens(1).isRecto = true
    // SwapRotate
    game.actionTokens(2).isUsed = true
    game.actionTokens(2).isRecto = false
    // WatsonTobby
    game.actionTokens(3).isUsed = false
    game.actionTokens(3).isRecto = true
    game.displayToken()
    // Turn 1:
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.alibiCards += new AlibiCard(mrJack, 1)
    game.mrJackPlayer.alibiCards += new AlibiCard(mrJack, 1)
    val aiPlayer = SmartAI(true)
    println("Turn number " + game.getTurnNumber)
    println("Turn of " + game.getCurrentPlayer.printName)
    val move = aiPlayer.getNextMove(game)
    game.playAction(ActionService.getActionDetails(game, move))
    println(move)
    // assert(move.startsWith("0"))
  }


  @Test def aiTestDetectiveShouldSeeSomeone(): Unit = {
    val game: Game = Game.buildNewGame()
    game.initGame()
    game.board.cells(1)(1).district.get.orientation = SOUTH
    game.board.cells(1)(1).district.get.isRecto = false
    game.board.cells(1)(2).district.get.orientation = WEST
    game.board.cells(1)(2).district.get.isRecto = true
    game.board.cells(1)(3).district.get.orientation = SOUTH
    game.board.cells(1)(3).district.get.isRecto = true

    game.board.cells(2)(1).district.get.orientation = NORTH
    game.board.cells(2)(1).district.get.isRecto = false
    game.board.cells(2)(2).district.get.orientation = NORTH
    game.board.cells(2)(2).district.get.isRecto = true
    game.board.cells(2)(3).district.get.orientation = NORTH
    game.board.cells(2)(3).district.get.isCross = false
    game.board.cells(2)(3).district.get.isRecto = true

    game.board.cells(3)(1).district.get.orientation = WEST
    game.board.cells(3)(1).district.get.isRecto = false
    game.board.cells(3)(2).district.get.orientation = SOUTH
    game.board.cells(3)(2).district.get.isRecto = true
    game.board.cells(3)(3).district.get.orientation = EAST
    game.board.cells(3)(3).district.get.isRecto = true

    game.board.cells(1)(0).detectives.get.remove(0)
    game.board.cells(1)(0).detectives.get += DetectiveToken(SHERLOCK)

    game.board.cells(1)(4).detectives.get.remove(0)
    game.board.cells(1)(4).detectives.get += DetectiveToken(WATSON)

    game.board.cells(4)(2).detectives.get.remove(0)
    game.board.cells(4)(1).detectives.get += DetectiveToken(TOBBY)

    game.board.printBoard()
    val mrJack = game.board.cells(2)(2).district.get.name
    game.mrJackPlayer.alibiCard = new AlibiCard(mrJack, 1)

    println("MrJack " + AlibiName.toChar(mrJack))
    // JokerRotate
    game.actionTokens.head.isUsed = false
    game.actionTokens.head.isRecto = true
    // SherlockAlibi
    game.actionTokens(1).isUsed = false
    game.actionTokens(1).isRecto = true
    // SwapRotate
    game.actionTokens(2).isUsed = true
    game.actionTokens(2).isRecto = false
    // WatsonTobby
    game.actionTokens(3).isUsed = false
    game.actionTokens(3).isRecto = true
    game.displayToken()
    // Turn 1:
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.alibiCards += new AlibiCard(mrJack, 1)
    val aiPlayer = SmartAI(isMrJackVal = false)
    println("Turn number " + game.getTurnNumber)
    println("Turn of " + game.getCurrentPlayer.printName)
    val move = aiPlayer.getNextMove(game)
    game.playAction(ActionService.getActionDetails(game, move))
    println(move)
    // Shouldn't take Joker !
    assert(move.startsWith("0"))
  }
}