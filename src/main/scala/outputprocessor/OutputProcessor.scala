package org.pipe.pipeline
package outputprocessor

import config.Config

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.pipeline.multipleclasses.Main.getClass

import java.util.logging.Logger
import scala.annotation.unused

class OutputProcessor(@unused spark: SparkSession) {

  val log = Logger.getLogger(getClass.getName)

  def process(config: Config): Unit = {
    config.file_write.foreach { fileWriteIterator =>
      val df = spark.table(fileWriteIterator.table_name)
      log.info("---File Read Start - " + fileWriteIterator.table_name + "---")
      df.write.mode("overwrite").option("header", "true").csv(fileWriteIterator.file_output)
      log.info("---File Read End - " + fileWriteIterator.table_name + "---")
    }
  }
}
