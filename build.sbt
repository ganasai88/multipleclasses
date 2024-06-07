ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12" // Ensure compatibility with Spark 3.5.0

lazy val root = (project in file("."))
  .settings(
    name := "pipeline",
    idePackagePrefix := Some("org.pipe.pipeline")
  )

val sparkVersion = "3.5.0"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.json4s" %% "json4s-native" % "3.6.12",
)
