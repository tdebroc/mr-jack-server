package com.grooptown.mrjack.players

object AlibiName extends Enumeration {
  type AlibiName = Value
  val MADAME,
      SERGENT_GOODLEY,
      JEREMY_BART,
      WILLIAM_GULL,
      MISS_STEALTHY,
      JOHN_SMITH,
      LESTRADE,
      JOHN_PIZER,
      JOSEPH_LANG = Value

  def toChar(alibi : AlibiName): Char = {
    (AlibiName.values.toList.indexOf(alibi) + 65).toChar
  }
}

