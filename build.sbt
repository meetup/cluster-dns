resolvers += "bintray" at "http://jcenter.bintray.com"

enablePlugins(CommonSettingsPlugin)
enablePlugins(DockerPackagePlugin)
enablePlugins(CoverallsWrapper)

name := "cluster-dns"

libraryDependencies ++= Seq(
  "com.meetup" %% "scala-logger" % "10.0.0",
  "com.github.mkroli" %% "dns4s-akka" % "0.9",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.9-RC2" % "test",
  "org.mockito" % "mockito-core" % "1.9.5" % "test"
)
