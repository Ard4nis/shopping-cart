name := "ShoppingCart"
 
version := "1.0-SNAPSHOT"
      
lazy val root = (project in file(".")).enablePlugins(PlayScala, SwaggerPlugin)

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
  "org.webjars" % "swagger-ui" % "3.25.0"
)


resolvers ++= Seq(
  "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
)

swaggerDomainNameSpaces := Seq("models")
swaggerPrettyJson := true
swaggerV3 := true