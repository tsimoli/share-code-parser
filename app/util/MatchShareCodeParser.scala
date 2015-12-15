package util

import java.nio.ByteBuffer
import javax.xml.bind.DatatypeConverter

object MatchShareCodeParser extends App {
  /**
    * Parses CSGO share code for example CSGO-axK5i-FtSWL-K2LYa-G9DsZ-rjNYE
    * returns matchId, outcomeId and token as an option tuple
    */
  def parseCode(code: String): Option[(String, String, String)] = {
    val allowedValues = "ABCDEFGHJKLMNOPQRSTUVWXYZabcdefhijkmnopqrstuvwxyz23456789"
    val allowedValuesLength = allowedValues.length
    val codeLength = code.length

    if (codeIsInRightFormat(code)) {
      val concattedValues: BigInt = sanitazeCode(code).toCharArray.reverse.foldLeft(BigInt(0))((acc, char) => {
        acc * allowedValuesLength + allowedValues.indexOf(char)
      })
      val byteArray = DatatypeConverter.parseHexBinary(concattedValues.toString(16))
      val matchId = read64bitInt(byteArray.take(8))
      val outcomeId = read64bitInt(byteArray.drop(8).take(8))
      val token = readShort(byteArray.takeRight(2))
      Some((matchId.toString, outcomeId.toString, token.toString))
    } else {
      None
    }
  }

  private def read64bitInt(bytes: Array[Byte]) = {
    bytes.zipWithIndex.foldLeft(0L) { (acc, byteAndIndex) =>
      acc + (byteAndIndex._1 & 0xff) * (Math.pow(256L, byteAndIndex._2)).toLong
    }
  }

  private def readShort(bytes: Array[Byte]) = {
    ByteBuffer.wrap(bytes.reverse).getChar().toInt
  }

  private def sanitazeCode(inputCode: String) = {
    inputCode.substring(4, inputCode.length).replace("-", "")
  }

  private def codeIsInRightFormat(code: String): Boolean = {
    val HYPHEN: Char = '-'
    code.length == 34 && code.substring(0, 4) == "CSGO" && code.charAt(10) == HYPHEN && code.charAt(16) == HYPHEN && code.charAt(22) == HYPHEN && code.charAt(28) == HYPHEN
  }
}
