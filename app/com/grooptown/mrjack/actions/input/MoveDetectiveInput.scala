package com.grooptown.mrjack.actions.input

import com.grooptown.mrjack.board.DetectiveName.DetectiveName

case class MoveDetectiveInput(detectiveName: DetectiveName, moveCount: Int) extends ActionInput
