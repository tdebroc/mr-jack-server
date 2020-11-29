package com.grooptown.mrjack.actions.input

import com.grooptown.mrjack.board.Orientation.Orientation

case class RotateInput(districtId: Int, orientation: Orientation) extends ActionInput
