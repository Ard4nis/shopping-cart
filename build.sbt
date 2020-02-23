name := "ShoppingCart"
 
version := "1.0-SNAPSHOT"
      
lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"
val playVersion = "2.7.3"

libraryDependencies ++= Seq(
  guice,
  ehcache,
  filters,
  jdbc,
  evolutions,
  ws,
  "com.typesafe.play" % "play-cache_2.12" % playVersion,
  "ch.qos.logback.contrib" % "logback-jackson" % "0.1.5",
  "ch.qos.logback.contrib" % "logback-json-classic" % "0.1.5",
  "io.prometheus" % "simpleclient_servlet" % "0.7.0",
  "org.postgresql" % "postgresql" % "42.2.5",
  // Test dependencies
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.scalamock" %% "scalamock-scalatest-support" % "3.6.0" % Test,
  "com.miguno.akka" %% "akka-mock-scheduler" % "0.5.4" % Test
)


resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
)