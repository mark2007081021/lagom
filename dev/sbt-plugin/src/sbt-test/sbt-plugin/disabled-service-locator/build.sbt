import com.lightbend.lagom.sbt.InternalKeys.interactionMode

lazy val root = (project in file(".")).enablePlugins(LagomJava)

scalaVersion := Option(System.getProperty("scala.version")).getOrElse("2.11.7")

interactionMode in ThisBuild := com.lightbend.lagom.sbt.NonBlockingInteractionMode

lagomServiceLocatorEnabled := false

InputKey[Unit]("isServiceLocatorDown") := {
  try {
    DevModeBuild.isServiceLocatorReachable()
    throw new RuntimeException("Service locator is running")
  }
  catch {
    case e: Exception => println("Service locator is not running")
  }
}

InputKey[Unit]("verifyReloads") := {
  val expected = Def.spaceDelimited().parsed.head.toInt
  val actual = IO.readLines(target.value / "reload.log").count(_.nonEmpty)
  if (expected == actual) {
    println(s"Expected and got $expected reloads")
  } else {
    throw new RuntimeException(s"Expected $expected reloads but got $actual")
  }
}