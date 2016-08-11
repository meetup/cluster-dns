package com.meetup.clusterdns

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.io.IO
import akka.util.Timeout
import akka.pattern.ask
import com.github.mkroli.dns4s.akka.Dns
import com.github.mkroli.dns4s.dsl._
import org.scalatest.{FunSpec, Matchers}
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}


class ClusterDNSComponentTest extends FunSpec with Matchers with BeforeAndAfterAll {
  implicit val system = ActorSystem("DnsRequester")
  implicit val timeout = Timeout(5.seconds)

  override def beforeAll {
    ClusterDNS.main(Array.empty)
  }

  it("should pass") {

    val response = IO(Dns) ? Dns.DnsPacket(Query ~ Questions(QName("localhost")), new InetSocketAddress("127.0.0.1", 32053))

    Try(Await.result(response, 5.seconds)) match {
      case Success(message) =>
        message match {
          case Response(Answers(answers)) =>
            answers.collect {
              case ARecord(arecord) =>
                arecord.address.getHostAddress shouldBe "127.0.0.1"
            }
          case x => fail(s"got $x")
        }
      case Failure(e) =>
        fail(s"Got failure: $e")
    }

  }
  override def afterAll{
    ClusterDNS.shutdown()
  }


}
