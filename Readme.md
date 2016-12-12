Simple Kafka connect classes in scala for loading twitter stream to kafka topic.

Kafka connect overview is here https://kafka.apache.org/documentation#connect
Listens to twitter stream api - https://dev.twitter.com/streaming/overview using twitter client https://github.com/twitter/hbc

Instructions
------------

* Create twitter api keys and copy them from https://apps.twitter.com/
* Update the file TwitterKafkaConnect.Properties with twitter keys, keyword to track and kafka topic name.
* Start kafka server if using standalone, first two steps in https://kafka.apache.org/quickstart
* Compile TwitterKafkaConnect
    * Goto the source directory
    * sbt
    * assembly
* Goto Kafka directory. 
* Run "Export CLASSPATH=/pathToTwitterConnectTarget"
* Run "bin/connect-standalone.sh config/connect-standalone.properties TwitterKafkaConnect.properties"

It will send all tweets to kafka topic as long as it is running.
