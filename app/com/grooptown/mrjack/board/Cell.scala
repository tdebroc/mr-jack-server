package com.grooptown.mrjack.board

import com.grooptown.mrjack.board.Cell.getEmptyCell

import scala.Array.ofDim
import scala.collection.mutable.ListBuffer

case class Cell(
                 detectives: Option[ListBuffer[DetectiveToken]],
                 var district: Option[District]
               ) {

  def forPrinting(detectivesHorizontal: Boolean): Array[Array[Char]] = {
    if (district.nonEmpty) district.get.forPrinting()
    else printDetective(detectivesHorizontal)
  }

  def printDetective(detectivesHorizontal: Boolean): Array[Array[Char]] = {
    val chars = getEmptyCell
    detectives.get.zipWithIndex.foreach(t => {
      val line = if (!detectivesHorizontal) 1 else t._2
      val col = if (!detectivesHorizontal) if (detectives.get.length == 1) 1 else t._2 else 1
      chars(line)(col) = t._1.name.toString.toUpperCase.charAt(0)
    })
    chars
  }

  def getDetectives: Array[DetectiveToken] = if (detectives.isEmpty) null else detectives.get.toArray
  def getDistricts: District = if (district.isEmpty) null else district.get
}

object Cell {
  val CELL_WIDTH = 3

  def getEmptyCell: Array[Array[Char]] = {
    val chars = ofDim[Char](CELL_WIDTH, CELL_WIDTH)
    for {
      i <- chars.indices
      j <- chars.indices
    } {
      chars(i)(j) = ' '
    }
    chars
  }
}