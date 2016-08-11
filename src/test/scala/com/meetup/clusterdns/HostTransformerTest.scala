package com.meetup.clusterdns

import org.scalatest.{FunSpec, Matchers}

class HostTransformerTest extends FunSpec with Matchers {

  it("should strip full domain for matched ones.") {
    val domain = "test.meetup.com"
    val input = "yada.test.meetup.com"
    val expected = "yada"

    new HostTransformer(domain).transform(input) shouldBe expected
  }

  it("should leave other domains alone.") {
    val domain = "test.meetup.com"
    val input = "yada.test.other.com"

    new HostTransformer(domain).transform(input) shouldBe input
  }
}
