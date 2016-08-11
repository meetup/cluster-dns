package com.meetup.clusterdns

class HostTransformer(clusterZone: String) {
  def transform(host: String): String = {
    if (host.endsWith(clusterZone)) {
      // Let's remove the clusterZone plus the ending period.
      host.substring(0, host.length - (clusterZone.length + 1))
    } else {
      host
    }
  }
}
