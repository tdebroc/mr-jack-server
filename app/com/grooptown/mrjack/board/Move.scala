package com.grooptown.mrjack.board

import com.grooptown.mrjack.board.Orientation.Orientation
import com.grooptown.mrjack.game.Position

case class Move(
                 direction: Position,
                 orientationToEnterCell: Orientation,
                 orientationToExitCell: Orientation,
               )
