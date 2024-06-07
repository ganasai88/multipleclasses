package org.pipeline.multipleclasses

import org.apache.spark.sql.SparkSession
import org.json4s._
import org.json4s.native.JsonMethods._

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Spark Pipeline Example")
      .master("local[*]")
      .getOrCreate()

    val configFilePath = "C:\\Users\\ganas\\NewScala\\multipleclasses\\src\\main\\scala\\config.json"
    val jsonString = readConfigFile(configFilePath)

    val inputProcessor = new InputProcessor(spark)
    val tableDFs = inputProcessor.process(jsonString)

    // Create temporary views for each DataFrame
    tableDFs.foreach { case (tableName, df) =>
      df.createOrReplaceTempView(tableName)
      println(s"Temporary view created: $tableName")

      // Display data from the temporary view
      //println(s"Data from temporary view: $tableName")
      //spark.sql(s"SELECT * FROM $tableName").show()
    }

    val transformProcessor = new TransformProcessor(spark)
    val resultDFs = transformProcessor.process(jsonString, tableDFs)

    val outputProcessor = new OutputProcessor(spark)
    outputProcessor.process(jsonString, resultDFs)
    spark.stop()
  }

  private def readConfigFile(configFilePath: String): String = {
    val jsonString = scala.io.Source.fromFile(configFilePath).mkString
    implicit val formats: DefaultFormats.type = DefaultFormats
    compact(render(parse(jsonString)))
  }
}
