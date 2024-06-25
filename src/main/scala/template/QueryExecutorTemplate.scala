package org.pipe.pipeline
package template

import config.ConfigAndJsonParser
import inputprocessor.InputProcessor
import outputprocessor.OutputProcessor
import transforms.TransformProcessor

import org.apache.spark.sql.SparkSession
import org.pipeline.multipleclasses.Main.getClass

import java.util.logging.Logger

class QueryExecutorTemplate {

  val log = Logger.getLogger(getClass.getName)

  def process(configFilePath: String):Unit = {


    val configFilePath = "C:\\Users\\ganas\\NewScala\\multipleclasses\\src\\main\\scala\\config.json"

    val config = ConfigAndJsonParser.parseConfigFile(configFilePath)

    config.file_input.foreach { fileInputIterator =>
      val sourceType = fileInputIterator.source_type
      // Initialize Spark session based on source type
      val spark = sourceType match {
        case "S3Bucket" =>
          SparkSession.builder()
            .appName("Spark Pipeline Example")
            .master("local[*]") // Adjust as needed
            .config("spark.jars.packages", "org.apache.hadoop:hadoop-aws:3.3.2,com.amazonaws:aws-java-sdk-pom:1.12.365")
            .config("spark.hadoop.fs.s3a.aws.credentials.provider", "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
            .config("spark.hadoop.fs.s3a.access.key", "")
            .config("spark.hadoop.fs.s3a.secret.key", "")
            .getOrCreate()

        case "local" =>
          SparkSession.builder()
            .appName("Spark Pipeline Example")
            .master("local[*]") // Adjust as needed
            .getOrCreate()

        case _ =>
          throw new IllegalArgumentException(s"Unsupported source type: $sourceType")
      }

      log.info("****************** Processing Input File Start *************************")
      val inputProcessor = new InputProcessor(spark)
      val tableDFs = inputProcessor.process(config)
      log.info("****************** Processing Input File End *************************")

      log.info("****************** Processing Query Start *************************")
      val transformProcessor = new TransformProcessor(spark)
      val resultDFs = transformProcessor.process(config)
      log.info("****************** Processing Query End *************************")

      log.info("****************** Output Dataframe to File Start *************************")
      val outputProcessor = new OutputProcessor(spark)
      outputProcessor.process(config)
      log.info("****************** Output Dataframe to File End *************************")

      spark.stop()
    }
  }
}
