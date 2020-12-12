package com.grooptown.mrjack.game

case class TurnToken() {
  override def clone(): TurnToken = new TurnToken
}