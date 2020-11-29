package com.grooptown.mrjack.actions.behavior

import com.grooptown.mrjack.board.DetectiveName.{DetectiveName, SHERLOCK}

object MoveSherlockAction extends MoveDetectiveAction {
  override def getDetective: DetectiveName = SHERLOCK
}
