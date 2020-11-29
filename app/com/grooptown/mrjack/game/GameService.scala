package com.grooptown.mrjack.game

import scala.collection.mutable
import java.util.UUID.randomUUID

object GameService {
  val games: mutable.Map[String, Game] = new mutable.HashMap[String, Game]

  def addGame() : String = {
    val uuid = randomUUID().toString
    games.put(uuid, new Game)
    uuid
  }
}
