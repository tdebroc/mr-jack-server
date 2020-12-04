package com.grooptown.mrjack.game

import java.util

import java.util.UUID.randomUUID

object GameService {
  val games: util.Map[String, Game] = new util.HashMap[String, Game]

  def addGame() : String = {
    val uuid = randomUUID().toString
    games.put(uuid, new Game)
    uuid
  }
}
