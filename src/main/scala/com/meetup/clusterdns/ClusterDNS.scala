package com.meetup.clusterdns

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.github.mkroli.dns4s.akka._
import com.meetup.logging.Logging

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Main class duh.
 */
object ClusterDNS extends App with Logging {
  implicit val system = ActorSystem("ClusterDNS")
  implicit val timeout = Timeout(5.seconds)
  val port = sys.env.get("SERVER_PORT").fold(32053)(_.toInt)
  log.info(s"Starting on port $port")

  IO(Dns) ? Dns.Bind(
    system.actorOf(Props(new ClusterDNSActor())),
    port)

  def shutdown(): Future[Terminated] = {
    system.terminate()
  }
}
