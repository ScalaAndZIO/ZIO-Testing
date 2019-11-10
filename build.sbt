name := "ZioTesting"

version := "0.1"

scalaVersion := "2.12.8"

val zioVersion = "1.0.0-RC14"

libraryDependencies += "dev.zio" %% "zio" % "1.0.0-RC16"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-test"     % zioVersion % "test",
  "dev.zio" %% "zio-test-sbt" % zioVersion % "test"
)
testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))