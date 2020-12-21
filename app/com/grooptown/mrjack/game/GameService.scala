package com.grooptown.mrjack.game

import com.grooptown.mrjack.board.Board

import java.util
import java.util.Date

object GameService {
  val games: util.Map[String, Game] = new util.HashMap[String, Game]

  def addGame(): String = {
    val uuid = getNewGameId
    games.put(uuid, Game.buildNewGame(uuid))
    uuid
  }

  def cloneGame(gameId: String): String = {
    val uuid = getNewGameId
    games.put(uuid, Game.clone(games.get(gameId)))
    uuid
  }

  def getNewGameId: String = new Date().getTime.toString
}
