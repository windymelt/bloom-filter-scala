package dev.capslock.bloom

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class BloomSpec extends AnyFunSpec, Matchers, ScalaCheckPropertyChecks {
  describe("BloomFilter") {
    it("should add/check elements") {
      forAll { (ss: Set[String], nss: Set[String]) =>
        whenever(ss.size > 20 && nss.size > 20) {
          val bf = BloomFilter(4096, 1000)
          val bf2 = ss.foldLeft(bf) { (acc, s) =>
            acc.add(s)
          }
          ss.foreach { s =>
            bf2.contains(s) should be(true)
          }
          val nss2 = nss -- ss
          nss2.foreach { s =>
            bf2.contains(s) should be(false)
          }
        }
      }
    }

    it("should have a false positive rate") {
      val bf = BloomFilter(4096, 1000)
      note(s"False positive rate: ${bf.falsePositiveRate}")
      bf.falsePositiveRate >= 0.0 shouldBe true
      bf.falsePositiveRate < 0.001 shouldBe true
    }
  }
}
