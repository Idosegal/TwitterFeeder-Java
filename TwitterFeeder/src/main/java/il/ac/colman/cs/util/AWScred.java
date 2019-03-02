package il.ac.colman.cs.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;


// Generate the instances of all the AWS services that I'm using.
public class AWScred {
    public static AmazonSQS getSQSclient() {
        AmazonSQS client = AmazonSQSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials() {
            public String getAWSAccessKeyId() {
                return System.getProperty("config.aws.id");
            }

            public String getAWSSecretKey() {
                return System.getProperty("config.aws.secret.key");
            }
        })).withRegion(Regions.US_EAST_1).build();
        return client;
    }

    public static AmazonS3 getS3Client() {
        AmazonS3 amazonS3 = AmazonS3Client.builder()
                .withRegion("us-east-1")
                .withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return System.getProperty("config.aws.id");
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return System.getProperty("config.aws.secret.key");
                    }
                })).withRegion(Regions.US_EAST_1).build();
        return amazonS3;
    }


    public static AmazonCloudWatch getCloudWatchClient() {
        AmazonCloudWatch amazonCloudWatch = AmazonCloudWatchClientBuilder.standard()
                .withRegion("us-east-1")
                .withCredentials(new AWSStaticCredentialsProvider(new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return System.getProperty("config.aws.id");
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return System.getProperty("config.aws.secret.key");
                    }
                })).withRegion(Regions.US_EAST_1).build();
        return amazonCloudWatch;
    }
}
