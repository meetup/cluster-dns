package com.meetup.clusterdns

import com.github.mkroli.dns4s.dsl._
import org.scalatest.{FunSpec, Matchers}

class HostResolverTest extends FunSpec with Matchers {

  describe("DefaultResolver") {
    it("should resolve using system") {
      val result = DefaultResolver.resolve("localhost")
      // Turn into message.
      result.nonEmpty shouldBe true
      result.foreach { answer =>
        val response = Response ~ Answers(answer)
        response.answer match {
          case Seq(ARecord(record)) =>
            record.address.getHostAddress shouldBe "127.0.0.1"
          case x =>
            fail(s"Unrecognized result: $x")
        }
      }
    }

    it("should return failure for bad resolves") {
      val result = DefaultResolver.resolve("not.valid")
      result shouldBe None
    }
  }

}
