package org.louiscala.learning.luhn.tlasica

import org.louiscala.learning.luhn.tlasica.LuhnAlgorithm._
import org.scalatest.WordSpec
import org.scalatest.Matchers

class LuhnSpec extends WordSpec with Matchers {

  "isNumberValid() should return true on valid number" in {
    isNumberValid("79927398713") shouldBe true
  }

  "isNumberValid() should return false on invalid numbers" in {
    isNumberValid("79927398710") shouldBe false
    isNumberValid("79927398711") shouldBe false
    isNumberValid("79927398718") shouldBe false
  }

  "generateCheckDigit() should generate correct" in {
    generateCheckDigit("7992739871") shouldBe 3
  }

}
