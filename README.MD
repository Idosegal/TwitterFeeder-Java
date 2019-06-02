Project owner: Ido Segal
			 
In my Twitter Feeder project, we've implemented 3 different procceses:

1) The TwitterFeeder- Listening to the twitter feeds and getting them as a stream via API calls (Twitter4j).
We extracting the links from the stream and sending them to the SQS AS A Jason with to fields: Url and Track(The filter).
To start this service we can run the the configuration file with the following: ./config.txt TwitterListener from one of our instances.

2) TwitterListener- Proccessing the tweets we recived from the SQS.
We are using Jsoup to extract the content from the links and saving the details on our database in the cloud- RDS, In addition, we taking a screenshot from each Url the we received,
and sending him to the S3- our file storage cloud service using API calls.
To start this service we can run the the configuration file with the following: ./config.txt LinkListener from one of our instances.

3) SearchResultServer- this is the service that "speaking" with the end user.
This service opening HTTP server using Jetty API. In addition, in this service we declared our protocol.
The user needs to send A HTTP GET request with the folowing: <Our.load.balancer.DNS.NAME>/result?query="yourfilter".
The server response is a Jason file that contains all the records that match the filter:

[{
"title":
"content":
"description": (100 chars limit)
"ScreenshotUrl": 
}]

To start this service we can run the the configuration file with the following: ./config.txt SearchResultsServer from one of our instances.


Our project saving the open closed design pattern, we can add services without any dependency from the other services.
Moreover, the services The services are independent of each other, for example, If our RDS database is down, the service will not be affected.
We can run each service individually.

Our services are monitoring using Amazon Cloud Watch, so we can detect any unusual activity on them.
