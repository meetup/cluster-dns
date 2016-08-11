package com.meetup.clusterdns

import com.github.mkroli.dns4s.dsl._
import org.scalatest.{FunSpec, Matchers}

class HostResolverTest extends FunSpec with Matchers {

  describe("DefaultResolver") {
    it("should resolve using system") {
      val result = DefaultResolver.resolve("localhost")
      val response = Response ~ result
      response.answer match {
        case Seq(ARecord(record)) =>
          record.address.getHostAddress shouldBe "127.0.0.1"
        case x =>
          fail(s"Unrecognized result: $x")
      }
    }

    it("should return failure for bad resolves") {
      val result = DefaultResolver.resolve("not.val_id")
      val response = Response ~ result
      response match {
        case Response(_) ~ ServerFailure() =>
        case x =>
          fail(s"Unrecognized result: $x")
      }
    }
  }

}
