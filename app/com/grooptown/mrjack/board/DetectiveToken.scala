package com.grooptown.mrjack.board

import com.grooptown.mrjack.board.DetectiveName.DetectiveName

case class DetectiveToken(name : DetectiveName) {
  def getName: String = name.toString
}
