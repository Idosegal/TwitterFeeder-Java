package il.ac.colman.cs.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Region;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.UUID;

public class ScreenshotGenerator {

  public static String takeScreenshot(String url){
    UUID uuid = UUID.randomUUID();
    /*
    Run our screenshot generator program using wkhtmltoimage, the application runnuing on ubuntu instance,
    So the command isn't running on windows.
     */
    try {
      String tempFile = new File(uuid.toString() + ".png").toString();
      //String[] cmd = {"xvfb-run", "--server-args=\"-screen 0 1024x768x24\"", "node screenshot.js",url, tempFile};
      String cmd = "xvfb-run --server-args=\"-screen 0 1024x768x24\" wkhtmltoimage --format png --crop-w 1024 --crop-h 768 --quiet --quality 60 " + url +" "+ tempFile;
      AmazonCloudWatch cloudWatch = AWScred.getCloudWatchClient();
      Long startTime = System.nanoTime();
      Process process = Runtime.getRuntime().exec(new String[] {"bash","-c",cmd});
      process.waitFor();
      System.out.println(process.exitValue());
      Long endTime = (System.nanoTime() - startTime) / 1000000;
      Monitoring.CloudWatchTraffic(cloudWatch, endTime.doubleValue(), "ScreenShoot", "ProcessTime");
      return tempFile;
    } catch (IOException e) {e.printStackTrace();
    } catch (InterruptedException e){e.printStackTrace();}
    return null;
  }
}
