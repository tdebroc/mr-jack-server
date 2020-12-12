import com.grooptown.mrjack.actions.ActionDetails
import com.grooptown.mrjack.actions.behavior.{AlibiCardAction, MoveTobbyAction, RotateAction}
import com.grooptown.mrjack.actions.input.{MoveDetectiveInput, RotateInput}
import com.grooptown.mrjack.actions.tokens.{JokerRotateToken, SherlockAlibiToken, SwapRotateToken, WatsonTobbyToken}
import com.grooptown.mrjack.board.DetectiveName.{SHERLOCK, TOBBY}
import com.grooptown.mrjack.board.DetectiveToken
import com.grooptown.mrjack.board.Orientation.{EAST, NORTH, SOUTH, WEST}
import com.grooptown.mrjack.game.Game
import com.grooptown.mrjack.players.AlibiName
import org.junit.Test


class WitnessCallTesting {
  @Test def witnessCallTest(): Unit = {
    val game: Game = Game.buildNewGame
    game.board.cells(1)(1).district.get.orientation = WEST
    game.board.cells(1)(2).district.get.orientation = WEST
    game.board.cells(1)(3).district.get.orientation = WEST
    game.board.cells(2)(1).district.get.orientation = NORTH
    // game.board.cells(2)(2).district.get.orientation = EAST
    game.board.cells(2)(3).district.get.orientation = EAST
    game.board.cells(3)(1).district.get.orientation = NORTH
    game.board.cells(3)(2).district.get.orientation = SOUTH
    game.board.cells(3)(3).district.get.orientation = WEST

    game.board.cells(4)(2).detectives.get.remove(0)
    game.board.cells(3)(0).detectives.get += DetectiveToken(TOBBY)

    game.board.printBoard()
    val mrJack = game.board.cells(3)(1).district.get.name

    println("MrJack " + AlibiName.toChar(mrJack))
    game.witnessCall(mrJack)
    game.board.printBoard()
  }


  @Test def witnessCallTest2(): Unit = {
    val game: Game = Game.buildNewGame
    game.board.cells(1)(1).district.get.orientation = WEST
    game.board.cells(1)(2).district.get.orientation = SOUTH
    game.board.cells(1)(3).district.get.orientation = EAST
    game.board.cells(2)(1).district.get.orientation = EAST
    // game.board.cells(2)(2).district.get.orientation = EAST
    game.board.cells(2)(3).district.get.orientation = SOUTH
    game.board.cells(3)(1).district.get.orientation = EAST
    game.board.cells(3)(2).district.get.orientation = SOUTH
    game.board.cells(3)(3).district.get.orientation = EAST

    game.board.cells(1)(0).detectives.get.remove(0)
    game.board.cells(0)(3).detectives.get += DetectiveToken(SHERLOCK)

    game.board.cells(4)(2).detectives.get.remove(0)
    game.board.cells(4)(1).detectives.get += DetectiveToken(TOBBY)

    game.board.printBoard()
    val mrJack = game.board.cells(3)(1).district.get.name
    println("MrJack " + AlibiName.toChar(mrJack))
    game.witnessCall(mrJack)
    game.board.printBoard()
  }

  @Test def playTurn1(): Unit = {
    val game: Game = Game.buildNewGame
    game.board.printBoard()
    println("MrJack is " + game.mrJackPlayer.alibiCard + " : " + game.mrJackPlayer.alibiCard.asChar)
    game.initTurn()
    val action1 = ActionDetails(isValid = true, null, AlibiCardAction, null, new SherlockAlibiToken)
    action1.action.playAction(action1.actionInput, game)
    val action2 = ActionDetails(isValid = true, null, MoveTobbyAction, MoveDetectiveInput(TOBBY, 1), new WatsonTobbyToken)
    game.board.printBoard()
    action2.action.playAction(action2.actionInput, game)
    game.board.printBoard()
    val action3 = ActionDetails(isValid = true, null, RotateAction, RotateInput(0, NORTH), new JokerRotateToken)
    action3.action.playAction(action3.actionInput, game)
    game.board.printBoard()
    val action4 = ActionDetails(isValid = true, null, RotateAction, RotateInput(1, NORTH), new SwapRotateToken)
    action4.action.playAction(action4.actionInput, game)
    game.board.printBoard()
    game.witnessCall(game.mrJackPlayer.alibiCard.name)
    game.board.printBoard()
    // game.playTurn()

    val game2 = game.copy()
    print(game2.history)

  }

}

