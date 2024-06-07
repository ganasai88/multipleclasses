package org.pipeline.multipleclasses

import org.apache.spark.sql.execution.streaming.StreamMetadata.format
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.json4s._
import org.json4s.native.JsonMethods._

class InputProcessor(spark: SparkSession) {
  def process(jsonString: String): Map[String, DataFrame] = {
    val json = parse(jsonString)

    val fileInputs = (json \ "file_input").extract[List[Map[String, String]]]
    val tableDFs: Map[String, DataFrame] = fileInputs.map { input =>
      val sourceType = input("source_type")
      val tableName = input("table_name")
      val path = input("path")

      val df = sourceType match {
        case "local" => spark.read.option("header", "true").csv(path)
        // Add more cases for other source types if needed
        case _ => throw new IllegalArgumentException("Invalid source type")
      }

      (tableName, df)
    }.toMap

    tableDFs
  }
}
