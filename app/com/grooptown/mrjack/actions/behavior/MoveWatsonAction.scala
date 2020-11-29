package com.grooptown.mrjack.actions.behavior

import com.grooptown.mrjack.board.DetectiveName.{DetectiveName, WATSON}

object MoveWatsonAction extends MoveDetectiveAction {
  override def getDetective: DetectiveName = WATSON
}
