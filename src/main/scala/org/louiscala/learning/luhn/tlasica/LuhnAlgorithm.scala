package org.louiscala.learning.luhn.tlasica

object LuhnAlgorithm {

  private[this] def digitValue( dwi: (Int, Int) ) : Int = dwi match {
    case (n:Int, p:Int) if p%2 == 0 => n
    case (n:Int, _) => (2*n)/10 + (2*n)%10
  }

  private[this] def sum(num:String) : Int = {
    val digits = num map (_.toString.toInt)
    val withIndex = digits.reverse.zipWithIndex
    val values = withIndex map digitValue
    values.sum
  }

  def isNumberValid(num: String): Boolean = {
    sum(num) % 10 == 0
  }

  def generateCheckDigit(num: String): Int = {
    10 - (sum(num+"0") % 10)
  }

  // should pass "79927398713"
  // should not pass "7992739871{0,1,2,4..9}"

}
