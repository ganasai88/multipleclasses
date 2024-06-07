package org.pipeline.multipleclasses

import org.apache.spark.sql.execution.streaming.StreamMetadata.format
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.json4s._
import org.json4s.native.JsonMethods._

class TransformProcessor(spark: SparkSession) {
  def process(jsonString: String, tableDFs: Map[String, DataFrame]): Map[String, DataFrame] = {
    val json = parse(jsonString)

    val transformations = (json \ "transform").extract[List[Map[String, String]]]
    val resultDFs = transformations.map { transform =>
      val tableName = transform("table_name")
      val sql = transform("sql")

      // Execute the SQL query using the table name
      val df = spark.sql(sql)

      // Optionally, display or process the results
      println(s"Results for transformation on table: $tableName")
      df.show()

      // Return the resulting DataFrame with the table name
      (tableName, df)
    }.toMap

    resultDFs
  }
}
