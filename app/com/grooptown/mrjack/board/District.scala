package com.grooptown.mrjack.board

import com.grooptown.mrjack.board.Cell.getEmptyCell
import com.grooptown.mrjack.board.Orientation.{EAST, NORTH, Orientation, SOUTH, WEST}
import com.grooptown.mrjack.players.AlibiName
import com.grooptown.mrjack.players.AlibiName.AlibiName

case class District(name: AlibiName,
               var orientation: Orientation) {
  var isRecto: Boolean = true
  var isCross: Boolean = false
  var isAlreadyRotated: Boolean = false

  def getOrientationAsString: String = orientation.toString
  def getAlibiNameAsString: String = name.toString

  def forPrinting(): Array[Array[Char]] = {
    val chars = getEmptyCell
    chars(0)(0) = '█'
    chars(0)(2) = '█'
    chars(2)(0) = '█'
    chars(2)(2) = '█'
    chars(1)(1) = if (isRecto) AlibiName.toChar(name) else ' '
    if (!isCross) {
      orientation match {
        case NORTH => chars(0)(1) = '█'
        case EAST => chars(1)(2) = '█'
        case SOUTH => chars(2)(1) = '█'
        case WEST => chars(1)(0) = '█'
      }
    }
    chars
  }

  def reverseDistrict(): Unit = {
    isRecto = false
    if (name.equals(AlibiName.JOSEPH_LANG)) {
      isCross = true
    }
  }
}
object District {
  val districtIdToName = Map(
    0 -> "Top-Left",
    1 -> "Top-Middle",
    2 -> "Top-Right",
    3 -> "Middle-Left",
    4 -> "Center",
    5 -> "Middle-Right",
    6 -> "Bottom-Left",
    7 -> "Bottom-Middle",
    8 -> "Bottom-Left"
  )
}