package com.grooptown.mrjack.actions.input

import com.grooptown.mrjack.board.DetectiveName.DetectiveName

case class JokerInput(detectiveName: DetectiveName, doNothing: Boolean) extends ActionInput