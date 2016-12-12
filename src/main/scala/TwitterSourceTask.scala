package twitterKafkaConnect

import java.time._
import java.util
import java.util.Collections
import java.util.concurrent.LinkedBlockingQueue

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.collection.mutable

import com.google.common.collect.Lists
import com.twitter.hbc.ClientBuilder
import com.twitter.hbc.core.Constants
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint
import com.twitter.hbc.core.processor.StringDelimitedProcessor
import com.twitter.hbc.httpclient.BasicClient
import com.twitter.hbc.httpclient.auth.OAuth1
import org.apache.kafka.connect.data.Schema
import org.apache.kafka.connect.source.{SourceRecord, SourceTask}
import org.json4s._
import org.json4s.jackson.Serialization.read

class TwitterSourceTask extends SourceTask {
  private lazy val tweetQueue: LinkedBlockingQueue[String] = new LinkedBlockingQueue[String](1000)
  private var twitterClient: BasicClient = _
  private var topic: String = _

  def start(validProps: java.util.Map[String, String]): Unit = {
    this.topic = validProps.get(TwitterConfig.topicConfig)
    val endpoint = new StatusesFilterEndpoint();
    endpoint.trackTerms(Lists.newArrayList(validProps.get(TwitterConfig.trackTermsConfig)));

    val auth = new OAuth1(
      validProps.get(TwitterConfig.consumerKeyConfig),
      validProps.get(TwitterConfig.consumerSecretConfig),
      validProps.get(TwitterConfig.tokenConfig),
      validProps.get(TwitterConfig.tokenSecretConfig)
    )

    this.twitterClient = new ClientBuilder()
      .hosts(Constants.STREAM_HOST)
      .endpoint(endpoint)
      .authentication(auth)
      .processor(new StringDelimitedProcessor(this.tweetQueue))
      .build();

    this.twitterClient.connect()
  }

  override def poll(): java.util.List[SourceRecord] = {
    val tweets = new java.util.ArrayList[String]()
    val sourcePartitionKey = Collections.singletonMap("Partition", 0)

    this.tweetQueue.drainTo(tweets)

    tweets.map(t => (t, TweetParser.getTimeStampMs(t)))
      .filter(t => t._2 > 0) //twitter client sends stall warnings, this is quick way to remove those and just select tweets.
      .map(t =>
        new SourceRecord(
          sourcePartitionKey,
          Collections.singletonMap("Offset", Instant.now().toEpochMilli()),
          this.topic,
          0,
          Schema.STRING_SCHEMA,
          "dummyKey",
          Schema.STRING_SCHEMA,
          t._1,
          t._2
        ))
      .toList
  }

  def stop(): Unit = {
    this.twitterClient.stop()
  }

  override def version(): String = "0.1"

  private object TweetParser {

    implicit val formats = DefaultFormats
    def getTimeStampMs(s: String): Long = {
      try org.json4s.jackson.JsonMethods.parse(s).extract[Tweet].timestamp_ms.toLong catch { case e: Exception => 0 }
    }

    case class Tweet(timestamp_ms: String)
  }
}
