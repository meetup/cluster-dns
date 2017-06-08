package com.meetup.clusterdns.handlers

import com.github.mkroli.dns4s.Message
import com.github.mkroli.dns4s.dsl.{ARecord, _}
import com.google.common.net.InetAddresses
import com.meetup.clusterdns.{HostResolver, HostTransformer}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

class TypeAHandlerTest extends FunSpec with Matchers with MockitoSugar {
  it("should resolve with the provided resolver for A records") {
    val host = "google.de"
    val addr = "10.0.0.1"

    val resolver = mock[HostResolver]
    val resolved = Some(ARecord(addr.toString))
    when(resolver.resolve(any[String]())).thenReturn(resolved)

    val message: Message = Query ~ Questions(QName(host) ~ TypeA)
    val result = new TypeAHandler(resolver, None).handle.lift(message)

    result.nonEmpty shouldBe true
    result.map { r =>
      r.answer.nonEmpty shouldBe true
      r.answer match {
        case List(ARecord(record)) =>
          record.address shouldBe InetAddresses.forString(addr)

        case other =>
          fail(s"Didn't recognize result: $other")
      }
    }
  }

  it("should transform host if transformer available") {

    val host = "test.namespace.meetup.com"
    val addr = "10.0.0.1"

    val transformer = new HostTransformer("meetup.com")

    val resolver = mock[HostResolver]
    val resolved = Some(ARecord(addr.toString))
    when(resolver.resolve(transformer.transform(host))).thenReturn(resolved)

    val message: Message = Query ~ Questions(QName(host) ~ TypeA)
    val result = new TypeAHandler(resolver, Some(transformer)).handle.lift(message)

    result.nonEmpty shouldBe true
    result.map { r =>
      r.answer.nonEmpty shouldBe true
      r.answer match {
        case List(ARecord(record)) =>
          record.address shouldBe InetAddresses.forString(addr)

        case other =>
          fail(s"Didn't recognize result: $other")
      }
    }
  }

  it("should resolve with the provided resolver for AAAA records") {
    val host = "google.de"
    val addr = "0000 0000 0000 0000 0000 FFFF 0A00 0001"
    val resolver = mock[HostResolver]
    val resolved = Some(AAAARecord(addr.toString))
    when(resolver.resolve(any[String]())).thenReturn(resolved)

    val message: Message = Query ~ Questions(QName(host) ~ TypeA)
    val result = new TypeAHandler(resolver, None).handle.lift(message)

    result.nonEmpty shouldBe true
    result.map { r =>
      r.answer.nonEmpty shouldBe true
      r.answer match {
        case List(AAAARecord(record)) =>
          record.address shouldBe InetAddresses.forString(addr)

        case other =>
          fail(s"Didn't recognize result: $other")
      }
    }
  }
}
