package com.grooptown.mrjack.board

import scala.util.Random

object Orientation extends Enumeration {
  type Orientation = Value
  val NORTH, EAST, SOUTH, WEST = Value

  def getRandomOrientation: Orientation = {
    Random
      .shuffle(Orientation.values.toList)
      .head
  }
}
