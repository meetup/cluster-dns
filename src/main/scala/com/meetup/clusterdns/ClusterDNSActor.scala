package com.meetup.clusterdns

import akka.actor.Actor
import com.github.mkroli.dns4s.Message
import com.github.mkroli.dns4s.dsl._
import com.meetup.clusterdns.handlers.{QueryHandler, TypeAHandler}
import com.meetup.logging.Logging

object ClusterDNSActor extends Logging {
  def receiveQuery(m: Message, handlers: List[QueryHandler] = List.empty): Message = {
    val response: Option[Message] =
      if (handlers.nonEmpty) {
        handlers.map(_.handle)
          .reduceRight(_ orElse _)
          .lift(m)
      } else None

    response.getOrElse[Message] {
      log.error(s"Couldn't handle message: $m")
      Response ~ NotImplemented
    }
  }
}

class ClusterDNSActor(
    handlers: List[QueryHandler] = List(new TypeAHandler())
) extends Actor with Logging {
  log.info(s"Actor running with handlers: $handlers")

  override def receive = {
    case m @ Query(_) => sender ! ClusterDNSActor.receiveQuery(m, handlers)
  }
}
