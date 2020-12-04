import com.grooptown.mrjack.board.DetectiveName.{SHERLOCK, TOBBY}
import com.grooptown.mrjack.board.DetectiveToken
import com.grooptown.mrjack.board.Orientation.{EAST, NORTH, SOUTH, WEST}
import com.grooptown.mrjack.game.Game
import com.grooptown.mrjack.players.AlibiName
import org.junit.Test


class WitnessCallTesting {
  @Test def witnessCallTest(): Unit = {
    val game: Game = new Game
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
    val game: Game = new Game
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
}

