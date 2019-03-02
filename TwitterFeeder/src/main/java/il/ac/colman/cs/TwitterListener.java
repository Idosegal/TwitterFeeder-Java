package il.ac.colman.cs;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.sqs.AmazonSQS;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.colman.cs.util.AWScred;
import il.ac.colman.cs.util.Monitoring;
import il.ac.colman.cs.util.MyJson;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterListener {
  public static void main(String[] args) {
    // Create our twitter configuration
    ConfigurationBuilder cb = new ConfigurationBuilder();
    cb.setDebugEnabled(true)
            .setOAuthConsumerKey(System.getProperty("config.twitter.consumer.key"))
            .setOAuthConsumerSecret(System.getProperty("config.twitter.consumer.secret"))
            .setOAuthAccessToken(System.getProperty("config.twitter.access.token"))
            .setOAuthAccessTokenSecret(System.getProperty("config.twitter.access.secret"));

    // Create our Twitter stream

    /*
      This is where we should start fetching the tweets using the Streaming API
      See Example 9 on this page: http://twitter4j.org/en/code-examples.html#streaming
    */

    StatusListener listener = new StatusListener() {

      AmazonCloudWatch amazonCloudWatch = AWScred.getCloudWatchClient();
      AmazonSQS client = AWScred.getSQSclient();

      public void onStatus(Status status) {
        if (status.getURLEntities() != null && status.getLang().equals("en")) {
          for (final URLEntity map : status.getURLEntities()) {
            // Send message to a Queue
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                long id = status.getId();
              String output = objectMapper.writeValueAsString(new
                      MyJson(map.getExpandedURL(),id,System.getProperty("config.twitter.track")));
              client.sendMessage(System.getProperty("config.sqs.url"), output);
              Monitoring.CloudWatchTraffic(amazonCloudWatch, 1.00, "TwitterFeeder"
                      , System.getProperty("config.twitter.track"));
            } catch (JsonProcessingException e) {
              e.printStackTrace();
            }
          }
        }
      }

      public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}

      public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}

      public void onScrubGeo(long userId, long upToStatusId) {}

      public void onStallWarning(StallWarning warning) {}

      public void onException(Exception ex) {
        ex.printStackTrace();
      }
    };

    TwitterStreamFactory tf = new TwitterStreamFactory(cb.build());
    TwitterStream twitterStream = tf.getInstance();
    // TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
    twitterStream.addListener(listener);
    // sample() method internally creates a thread which manipulates TwitterStream and calls these adequate listener methods continuously.

    twitterStream.sample();
    FilterQuery fq = new FilterQuery();
    fq.track(System.getProperty("config.twitter.track"));
    twitterStream.filter(fq);
  }
}