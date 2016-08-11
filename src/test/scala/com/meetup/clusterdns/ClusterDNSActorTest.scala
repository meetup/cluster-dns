package com.meetup.clusterdns

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{DefaultTimeout, TestActorRef, TestKit}
import com.github.mkroli.dns4s.Message
import com.github.mkroli.dns4s.dsl._
import com.meetup.clusterdns.handlers.QueryHandler
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterAll, FunSpec, FunSpecLike, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class ClusterDNSActorTest extends TestKit(ActorSystem("ClusterDNSActorTest"))
    with DefaultTimeout with FunSpecLike with Matchers with MockitoSugar
    with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  it("should return NotImplemented when no matching PF") {
    ClusterDNSActor.receiveQuery(Query) match {
      case Response(_) ~ NotImplemented() =>
      case other =>
        fail(s"Got unexpected result: $other")
    }
  }

  it("should combine Handler PFs") {
    val input1 = mock[Message]
    val message1 = mock[Message]
    val handler1 = new QueryHandler {
      override def handle: PartialFunction[Any, Message] = {
        case x if x == input1 => message1
      }
    }

    val input2 = mock[Message]
    val message2 = mock[Message]
    val handler2 = new QueryHandler {
      override def handle: PartialFunction[Any, Message] = {
        case x if x == input2 => message2
      }
    }

    val handlers = List(handler1, handler2)

    // Sanity check
    message1 == message2 shouldBe false
    ClusterDNSActor.receiveQuery(input1, handlers) shouldBe message1
    ClusterDNSActor.receiveQuery(input2, handlers) shouldBe message2
  }

  it("should match query messages") {
    val actorRef = TestActorRef(new ClusterDNSActor(List.empty))
    val inputMessage = Query ~ Questions(QName("google.de"))

    val response = actorRef ? inputMessage

    Try(Await.result(response, 5.seconds)) match {
      case Success(Response(_) ~ NotImplemented()) =>
      // Got the expected message
      case Success(message) =>
        fail(s"Unexpected response: $message")

      case Failure(e) =>
        fail(s"Failed to get future: $e", e)
    }
  }

  it("should match complex query messages") {
    val actorRef = TestActorRef(new ClusterDNSActor(List.empty))
    val inputMessage = Query ~ Questions(QName("example.com") ~ TypeA ~ ClassIN)

    val response = actorRef ? inputMessage

    Try(Await.result(response, 5.seconds)) match {
      case Success(Response(_) ~ NotImplemented()) =>
      // Got the expected message
      case Success(message) =>
        fail(s"Unexpected response: $message")

      case Failure(e) =>
        fail(s"Failed to get future: $e", e)
    }
  }

}
