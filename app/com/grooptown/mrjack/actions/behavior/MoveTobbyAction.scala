package com.grooptown.mrjack.actions.behavior

import com.grooptown.mrjack.board.DetectiveName.{DetectiveName, SHERLOCK, TOBBY}

object MoveTobbyAction extends MoveDetectiveAction {
  override def getDetective: DetectiveName = TOBBY
}
