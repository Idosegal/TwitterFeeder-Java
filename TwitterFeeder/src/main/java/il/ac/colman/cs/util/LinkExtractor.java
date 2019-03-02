package il.ac.colman.cs.util;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Extract content from links
 */
public class LinkExtractor {
  /*
 Use JSoup to extract the text, title and description from the URL.

 Extract the page's content, without the HTML tags.
 Extract the title from title tag or meta tags, prefer the meta title tags.
 Extract the description the same as you would the title.

 For title and description tags, if there are multiple (which is usually the case)
 take the first.
  */
  public ExtractedLink extractContent(String url, String screenshotURL) {
    try {

      AmazonCloudWatch cloudWatch = AWScred.getCloudWatchClient();
      Long start_time = System.nanoTime();

      String title=null;
      String text=null;
      String description_100_chars=null;
      //Using Jsoup to extract the data from the links
      org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
      title = doc.title();
      text = doc.body().text();
      if(text.length() > 100) {
        description_100_chars = text.substring(0,99);
      }
      else
        description_100_chars = text;

      Long end_time = ((System.nanoTime() - start_time) / 1000000);
      Monitoring.CloudWatchTraffic(cloudWatch,end_time.doubleValue(),"Content URL","Processing time");
      return new ExtractedLink(url, text, title, description_100_chars, screenshotURL);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
