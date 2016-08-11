package com.meetup.clusterdns.handlers

import com.github.mkroli.dns4s.Message

trait QueryHandler {
  def handle: PartialFunction[Any, Message]
}
