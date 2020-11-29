package com.grooptown.mrjack.game

import play.api.libs.json.Json

case class Position(line: Int, col: Int)

object Position {
  implicit val residentWrites = Json.writes[Position]
}
