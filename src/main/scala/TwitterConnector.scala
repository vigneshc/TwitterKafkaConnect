package twitterKafkaConnect

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable

import org.apache.kafka.common.config.ConfigDef
import org.apache.kafka.connect.connector._
import org.apache.kafka.connect.source.SourceConnector

class TwitterConnector extends SourceConnector {

  private var props: Map[String, String] = _

  override def config(): ConfigDef = TwitterConfig.config

  override def start(props: java.util.Map[String, String]): Unit = {
    this.props = TwitterConfig.config.parse(props).map(x => (x._1, x._2.toString())).toMap
  }

  override def stop(): Unit = {
  }

  override def taskConfigs(maxTasks: Int): java.util.List[java.util.Map[String, String]] = {
    // always return single config, twitter stream api only allows one connection.
    List(this.props.asJava).asJava
  }

  override def taskClass(): Class[_ <: Task] = classOf[TwitterSourceTask]

  override def version(): String = "0.1"
}

object TwitterConfig {
  val consumerKeyConfig = "consumerKey"
  val consumerSecretConfig = "consumerSecret"
  val tokenConfig = "token"
  val tokenSecretConfig = "secret"
  val topicConfig = "kafkaTopic"
  val trackTermsConfig = "trackKeywords"

  val config = new ConfigDef()
    .define(consumerKeyConfig, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "consumer key")
    .define(consumerSecretConfig, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "consumer secret")
    .define(tokenConfig, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "token")
    .define(tokenSecretConfig, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "secret")
    .define(trackTermsConfig, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "comma separated list of track terms")
    .define(topicConfig, ConfigDef.Type.STRING, ConfigDef.Importance.HIGH, "kafkatopic")
}
