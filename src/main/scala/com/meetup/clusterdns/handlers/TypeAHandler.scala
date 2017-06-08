package com.meetup.clusterdns.handlers

import com.github.mkroli.dns4s.Message
import com.github.mkroli.dns4s.dsl._
import com.meetup.clusterdns.{DefaultResolver, HostResolver, HostTransformer}

class TypeAHandler(
    resolver: HostResolver = DefaultResolver,
    transformer: Option[HostTransformer] = sys.env.get("TRANS_DOMAIN").map(new HostTransformer(_))
) extends QueryHandler {

  override def handle: PartialFunction[Any, Message] = {

    case Query(q) ~ Questions(QName(host) ~ TypeA() :: Nil) =>
      Response(q) ~ resolver.resolve(
        // If a transformer exists use it.
        transformer.fold(host)(_.transform(host))
      // If there's no resolve response, error otherwise
      // return with original host name.
      ).fold[MessageModifier](ServerFailure) { aRecord =>
          Answers(RRName(host) ~ aRecord)
        }

    case Query(q) ~ Questions(QName(host) ~ TypeAAAA() :: Nil) =>
      Response(q) ~ resolver.resolve(
        // If a transformer exists use it.
        transformer.fold(host)(_.transform(host))
      // If there's no resolve response, error otherwise
      // return with original host name.
      ).fold[MessageModifier](ServerFailure) { aRecord =>
          Answers(RRName(host) ~ aRecord)
        }

  }

}
