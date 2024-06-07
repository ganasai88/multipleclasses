package org.pipeline.multipleclasses

import org.apache.spark.sql.execution.streaming.StreamMetadata.format
import org.apache.spark.sql.{DataFrame, SaveMode, SparkSession}
import org.json4s._
import org.json4s.native.JsonMethods._

class OutputProcessor(spark: SparkSession) {
  def process(jsonString: String, resultDFs: Map[String, DataFrame]): Unit = {
    val json = parse(jsonString)

    val fileWrites = (json \ "file_write").extract[List[Map[String, String]]]
    fileWrites.foreach { fileWrite =>
      val tableName = fileWrite("table_name")
      val outputFile = fileWrite("file_output")

      // Check if the resultDFs map contains the table name
      if (resultDFs.contains(tableName)) {
        // Write the DataFrame to the specified output file path
        resultDFs(tableName)
          .write
          .mode(SaveMode.Overwrite)
          .option("header", "true")
          .csv(outputFile)

        println(s"Output saved to file: $outputFile")
      } else {
        println(s"Table $tableName not found in the result DataFrame map.")
      }
    }
  }
}
