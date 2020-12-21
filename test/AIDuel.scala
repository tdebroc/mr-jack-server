import com.grooptown.mrjack.game.Game
import com.grooptown.mrjack.players.Player


class AIDuel {
  var mrJackWinner = 0
  var detectiveWinner = 0
  def aiDuel(): Unit = {
    for (_ <- 1 to 10) {
      val winner = launchBattle()
      if (winner.toString.contains("DetectivePlayer")) detectiveWinner = detectiveWinner + 1 else mrJackWinner = mrJackWinner + 1
    }
    println("MrJack won : " + mrJackWinner)
    println("Detective won : " + detectiveWinner)
  }

  def launchBattle(): Player = {
    val game = Game.buildNewGame()
    game.initGame()
    game.registerAIPlayer("0", isMrJack = false)
    game.registerAIPlayer("1", isMrJack = true)
    while (game.winner.isEmpty) {
      game.board.printBoard()
      Thread.sleep(1000)
    }
    println("Winner is " + game.winner.get)
    game.winner.get
  }
}

