package org.pipe.pipeline
package transforms

import config.Config

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.pipeline.multipleclasses.Main.getClass

import java.util.logging.Logger

class TransformProcessor(spark: SparkSession) {

  val log = Logger.getLogger(getClass.getName)

  def process(config: Config): Unit = {
    config.transform.map { transform =>
      val df = spark.sql(transform.sql)
      log.info("---Query Execution Start - " + transform.table_name + "---")
      df.createOrReplaceTempView(transform.table_name)
      log.info("---Query Execution End - " + transform.table_name + "---")
    }
  }
}
