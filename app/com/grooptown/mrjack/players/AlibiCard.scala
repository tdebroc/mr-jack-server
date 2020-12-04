package com.grooptown.mrjack.players

import com.grooptown.mrjack.players.AlibiName._

import scala.collection.mutable.ListBuffer

case class AlibiCard(name: AlibiName, hourGlassCount: Int) {
  val asChar: Char = toChar(this.name)

  def getName: String = name.toString

  def getHourGlass: Int = hourGlassCount
}

object AlibiCard {
  def initAlibiCards: ListBuffer[AlibiCard] = {
    util.Random.shuffle(ListBuffer(
      AlibiCard(MADAME, 2), AlibiCard(SERGENT_GOODLEY, 0), AlibiCard(JEREMY_BART, 1),
      AlibiCard(WILLIAM_GULL, 1), AlibiCard(MISS_STEALTHY, 1), AlibiCard(JOHN_SMITH, 1),
      AlibiCard(LESTRADE, 0), AlibiCard(JOHN_PIZER, 1), AlibiCard(JOSEPH_LANG, 1)
    ))
  }
}

