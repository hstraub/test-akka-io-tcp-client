import NativePackagerKeys._

packageArchetype.akka_application

name := "TestAkkaIoTcpClient"

version := "0.1"

scalaVersion := "2.11.6"


mainClass in Compile := Some( "at.linuxhacker.testAkkaIoClient.ClientTest" )


mappings in Universal <+= (packageBin in Compile, sourceDirectory ) map {
  ( _, src) => 
  val conf = src / "main" / "resources" / "application.conf"
  conf -> "conf/application.conf"
}


resolvers ++= Seq("snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "releases" at "http://oss.sonatype.org/content/repositories/releases",
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases" )

val akkaVersion = "2.3.9"

libraryDependencies ++= Seq( 
  "com.typesafe.akka" %% "akka-kernel" % akkaVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" % "akka-slf4j_2.11" % akkaVersion
)

