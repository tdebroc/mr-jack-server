package com.grooptown.mrjack.game

import java.util
import java.util.Date

object GameService {
  val games: util.Map[String, Game] = new util.HashMap[String, Game]

  def addGame(): String = {
    val uuid = new Date().getTime.toString
    games.put(uuid, Game.buildNewGame)
    uuid
  }
}
