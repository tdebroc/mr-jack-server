package com.grooptown.mrjack.board

import com.grooptown.mrjack.board.Board.{BOARD_WITH, detectivePositions}
import com.grooptown.mrjack.board.Cell.CELL_WIDTH
import com.grooptown.mrjack.board.DetectiveName.DetectiveName
import com.grooptown.mrjack.board.Orientation.{EAST, NORTH, SOUTH, WEST}
import com.grooptown.mrjack.game.Position
import com.grooptown.mrjack.players.AlibiName
import com.grooptown.mrjack.players.AlibiName.{AlibiName, LESTRADE}

import scala.Array.ofDim
import scala.Option._
import scala.collection.mutable.ListBuffer
import scala.util.Random
import scala.util.control.Breaks.{break, breakable}

case class Board(cells: Array[Array[Cell]]) {

  def printBoard(): Unit = {
    val chars = ofDim[Char](CELL_WIDTH * BOARD_WITH, CELL_WIDTH * BOARD_WITH)
    for {
      boardLine <- cells.indices
      boardCol <- cells.indices
    } {
      val cell = cells(boardLine)(boardCol)
      val cellChars = cell.forPrinting(boardLine == 0 || boardLine == cells.length - 1)
      for {
        i <- cellChars.indices
        j <- cellChars.indices
      } {
        chars(boardLine * CELL_WIDTH + i)(boardCol * CELL_WIDTH + j) = cellChars(i)(j)
      }
    }
    chars.foreach(line => {
      line.foreach(c => print(c))
      println()
    })
  }

  def getDetectiveCells: Array[Cell] = detectivePositions.map(pos => cells(pos.line)(pos.col))

  def getDistricts: ListBuffer[District] = {
    val districts = new ListBuffer[District]
    for {boardLine <- 1 to 3; boardCol <- 1 to 3}
      districts += cells(boardLine)(boardCol).district.get
    districts
  }

  def innocentAlibi(name: AlibiName): Unit = {
    val district = getDistricts.filter(_.name == name).head
    district.reverseDistrict()
  }

  def moveDetective(detectiveName: DetectiveName, moveCount: Int): Unit = {
    val tokenToFind = DetectiveToken(detectiveName)
    val detectiveCells = getDetectiveCells
    val initialCell = detectiveCells.filter(cell => cell.detectives.get.contains(tokenToFind)).head
    val cellIndex = detectiveCells.indexOf(initialCell)
    val nextCellIndex = (cellIndex + moveCount) % detectiveCells.length
    detectiveCells(cellIndex).detectives.get -= tokenToFind
    detectiveCells(nextCellIndex).detectives.get += tokenToFind
  }

  def calculateVisibleCellsFromAllDetective: Array[Cell] = {
    detectivePositions
      .filter(pos => cells(pos.line)(pos.col).detectives.get.nonEmpty)
      .flatMap(getVisibleCellsFromOneDetectiveNotBlockedByWall)
  }

  def getNonVisibleDistrictsFromVisibleCells(visibleCells: Array[Cell]): ListBuffer[District] = {
    val visibleAlibis: Array[AlibiName] = visibleCells.filter(_.district.get.isRecto).map(_.district.get.name)
    this.getDistricts.filter(district => visibleAlibis.count(alibi => alibi == district.name) == 0)
  }

  def getVisibleCellsFromOneDetectiveNotBlockedByWall(pos: Position): ListBuffer[Cell] = {
    getVisibleCellsFromOneDetective(pos)
  }

  def getVisibleCellsFromOneDetective(pos: Position, blockedByWall: Boolean = true): ListBuffer[Cell] = {
    val movement = positionToMovement(pos)
    val visibleCells = new ListBuffer[Cell]
    var checked = 0
    var currentPos: Position = pos
    breakable {
      do {
        val nextPos = getNextPosition(currentPos, movement.direction)
        val nextCell = cells(nextPos.line)(nextPos.col)
        if (!nextCell.district.get.isCross
          && nextCell.district.get.orientation == movement.orientationToEnterCell
          && blockedByWall) {
          break
        }
        visibleCells += nextCell
        checked = checked + 1
        if (!nextCell.district.get.isCross
          && nextCell.district.get.orientation == movement.orientationToExitCell
          && blockedByWall) {
          break
        }
        currentPos = nextPos
      } while (checked < 3)
    }
    visibleCells
  }

  def getNextPosition(pos: Position, mov: Position): Position = Position(pos.line + mov.line, pos.col + mov.col)

  def positionToMovement(position: Position): Move = {
    position match {
      case Position(0, _) => Move(Position(1, 0), NORTH, SOUTH)
      case Position(_, 4) => Move(Position(0, -1), EAST, WEST)
      case Position(4, _) => Move(Position(-1, 0), SOUTH, NORTH)
      case Position(_, 0) => Move(Position(0, 1), WEST, EAST)
    }
  }

  def getCells: Array[Array[Cell]] = cells

  override def clone(): Board = {
    val cells = ofDim[Cell](BOARD_WITH, BOARD_WITH)
    for {
      line <- cells.indices
      col <- cells.indices
    } {
      val cell = this.cells(line)(col)
      cells(line)(col) = cell.copy(
        if (cell.detectives.isEmpty) empty else apply(cell.detectives.get.map(_.copy())),
        if (cell.district.isEmpty) empty else apply(cell.district.get.copy()),
      )
    }
    Board(cells)
  }
}

object Board {

  val BOARD_WITH = 5

  def districtIdToPosition(districtId: Int): Position = {
    Position(1 + districtId / 3, 1 + districtId % 3)
  }

  def buildBoard(): Board = {
    val cells = initCells()
    new Board(
      cells = cells
    )
  }

  def initCells(): Array[Array[Cell]] = {
    val cells = ofDim[Cell](BOARD_WITH, BOARD_WITH)
    for {
      line <- cells.indices
      col <- cells.indices
    } {
      cells(line)(col) = Cell(Option.apply(ListBuffer()), empty)
    }

    val alibis = Random.shuffle(AlibiName.values.toList).toIterator

    cells(1)(0) = Cell(Option.apply(ListBuffer(DetectiveToken(DetectiveName.SHERLOCK))), empty)
    cells(1)(4) = Cell(Option.apply(ListBuffer(DetectiveToken(DetectiveName.WATSON))), empty)
    cells(4)(2) = Cell(Option.apply(ListBuffer(DetectiveToken(DetectiveName.TOBBY))), empty)
    List(1, 2, 3).foreach(line => {
      List(1, 2, 3).foreach(col => {
        if (line == 2 && col == 2) {
          cells(line)(col) = getNewDistrict(LESTRADE)
        } else {
          var alibi = alibis.next()
          if (alibi.equals(LESTRADE)) {
            alibi = alibis.next()
          }
          cells(line)(col) = getNewDistrict(alibi)
        }
      })
    })
    cells(1)(1).district.get.orientation = WEST
    cells(1)(3).district.get.orientation = EAST
    cells(3)(2).district.get.orientation = SOUTH
    cells
  }


  def getNewDistrict(value: AlibiName.Value): Cell = {
    Cell(empty, Option.apply(District(value, Orientation.getRandomOrientation)))
  }

  val detectivePositions: Array[Position] = Array(
    Position(0, 1), Position(0, 2), Position(0, 3), // TOP
    Position(1, 4), Position(2, 4), Position(3, 4), // RIGHT
    Position(4, 3), Position(4, 2), Position(4, 1), // BOTTOM
    Position(3, 0), Position(2, 0), Position(1, 0) // LEFT
  )
}