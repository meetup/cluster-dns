# cluster-dns
[![Build Status](https://travis-ci.org/meetup/cluster-dns.svg?branch=master)](https://travis-ci.org/meetup/cluster-dns)
[![Coverage Status](https://coveralls.io/repos/github/meetup/cluster-dns/badge.svg?branch=master)](https://coveralls.io/github/meetup/cluster-dns?branch=master)

Expose internal cluster dns masked as a different authoritative domain.  We had to do this because we currently can't configure authoritative domain for KubeDNS on GCE.

## usage
Has two environment variables worth mentioning.

* `SERVER_PORT` - Optional, listens to `32053` by default.
* `TRANS_DOMAIN` - Parent domain you'd like to mask as.

## example

Let's say your cluster has a service `svc1.my-namespace.svc.cluster.local` bound to `10.0.0.1`,  set the `TRANS_DOMAIN` to `my.domain.com`.  This server will resolve `svc1.my-namespace.my.domain.com` to `10.0.0.1` for you.
