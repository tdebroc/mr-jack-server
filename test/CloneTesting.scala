import com.grooptown.mrjack.ai.level1.SmartAI
import com.grooptown.mrjack.board.DetectiveName.{SHERLOCK, TOBBY}
import com.grooptown.mrjack.board.DetectiveToken
import com.grooptown.mrjack.board.Orientation.{EAST, SOUTH, WEST}
import com.grooptown.mrjack.game.Game
import com.grooptown.mrjack.players.AlibiName
import org.junit.Test


class CloneTesting {

  @Test def cloneTest(): Unit = {
    val game: Game = Game.buildNewGame()
    game.initGame()
    game.board.cells(1)(1).district.get.orientation = WEST
    game.board.cells(1)(1).district.get.isRecto = false
    game.board.cells(1)(2).district.get.orientation = SOUTH
    game.board.cells(1)(2).district.get.isRecto = false
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

    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)
    game.mrJackPlayer.turnTokens += game.turnTokens.remove(0)

    game.actionTokens.head.isUsed = true
    val gameCloned = Game.clone(game)
    assert(gameCloned.actionTokens.head.isUsed)
    assert(!gameCloned.board.cells(1)(1).district.get.isRecto)
    assert(!gameCloned.board.cells(1)(2).district.get.isRecto)
    assert(gameCloned.mrJackPlayer.turnTokens.length == game.mrJackPlayer.turnTokens.length)

  }



}

