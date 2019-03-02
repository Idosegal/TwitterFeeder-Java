package il.ac.colman.cs;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import il.ac.colman.cs.util.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

/*
Service that listening to the Amazon SQS, here we processing the links,
 and save the data in Amazon RDS,and the screenshot to Amazon S3.
 */
public class LinkListener {
  public static void main(String[] args) throws SQLException {
    // Connect to the database
    final DataStorage dataStorage = new DataStorage();
    dataStorage.createTable();
    //Listening to the SQS
    AmazonSQS client = AWScred.getSQSclient();
    //AmazonSQS client = AWScred.getSQSclient();
      String sqs_url = System.getProperty("config.sqs.url");
      ReceiveMessageRequest request = new ReceiveMessageRequest(sqs_url);
      request.setWaitTimeSeconds(5);
      request.setVisibilityTimeout(1);
      while(true)
      {
          // Listen to SQS for arriving links
          ReceiveMessageResult result = client.receiveMessage(request);
          try {
              Thread.sleep(1000);

              for(Message message : result.getMessages())
              {
                  ObjectMapper mapper = new ObjectMapper();
                  mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                  try {
                      System.out.println("Message from sqs:\n" + message.getBody());
                      MyJson twitterInfo = mapper.readValue(message.getBody(), MyJson.class);

                      String tweet_url = twitterInfo.get_url();
                      // Take screenshot
                      String screenshot_path = new ScreenshotGenerator().takeScreenshot(tweet_url);
                      String bucket_name = System.getProperty("BUCKET");
                      AmazonS3 s3_client = AWScred.getS3Client();
                      File ss_file = new File(screenshot_path);
                      if(ss_file.exists()){
                          s3_client.putObject(bucket_name,screenshot_path,ss_file);
                          URL screenshot_url = s3_client.getUrl(bucket_name,screenshot_path);
                          ExtractedLink info = new LinkExtractor().extractContent(tweet_url,screenshot_url.toString());
                          System.out.println(screenshot_url.getPath());
                          if(info != null)
                          {
                              dataStorage.insertTable(info,twitterInfo.get_tweetID(),twitterInfo.get_track());
                              System.out.println(info.toString());
                          }
                          ss_file.delete();
                      }
                      client.deleteMessage(sqs_url,message.getReceiptHandle());
                  }catch (IOException e) {e.printStackTrace();}
              }

          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
  }
}
