name:= "twitterKafkaConnect"
val kafkaGroupId = "org.apache.kafka"
val kafkaClientArtifactId = "kafka-clients"
val kafkaClientRevision = "0.10.1.0"
libraryDependencies +=  kafkaGroupId % kafkaClientArtifactId % kafkaClientRevision

val kafkaConnectArtifactId = "connect-api"
libraryDependencies +=  kafkaGroupId % kafkaConnectArtifactId % kafkaClientRevision

val twitterClientGroupId = "com.twitter"
val twitterClientArtifactId = "hbc-core"
val twitterClientRevision = "2.2.0"
val twitter4JArtifactId = "hbc-twitter4j"

libraryDependencies += twitterClientGroupId % twitterClientArtifactId % twitterClientRevision
libraryDependencies += twitterClientGroupId % twitter4JArtifactId % twitterClientRevision

val json4sJackson = "org.json4s" %% "json4s-jackson" % "3.5.0"
libraryDependencies += json4sJackson
