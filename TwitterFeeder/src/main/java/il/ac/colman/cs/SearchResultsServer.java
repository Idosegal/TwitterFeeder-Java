package il.ac.colman.cs;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import il.ac.colman.cs.util.AWScred;
import il.ac.colman.cs.util.DataStorage;
import il.ac.colman.cs.util.ExtractedLink;
import il.ac.colman.cs.util.Monitoring;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/*

Create a HTTP Server using Jetty ans define our protocol:

Client request is: <AWSloadbalancerDNSname>/results?query="yourquery"

Server response is Jason:
[{
"title":
"content":
"description": (100 chars limit)
"ScreenshotUrl":
}]
 */
public class  SearchResultsServer extends AbstractHandler {
  public static void main(String[] args) throws Exception {
    // Connect to the database
    DataStorage dataStorage = new DataStorage();

    // Start the http server on port 8080
    Server server = new Server(8080);

    server.setHandler(new SearchResultsServer());

    server.start();
    server.join();
  }

  private DataStorage storage;

  SearchResultsServer() throws SQLException {
    storage = new DataStorage();
  }

  public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
    // Set the content type to JSON
    httpServletResponse.setContentType("application/json;charset=UTF-8");
    // Set the status to 200 OK
    httpServletResponse.setStatus(HttpServletResponse.SC_OK);

    AmazonCloudWatch amazonCloudWatch = AWScred.getCloudWatchClient(); // Cloud Watch
    Long start_time = System.nanoTime();

    // Build data from request
    List<ExtractedLink> results = storage.search(httpServletRequest.getParameter("query"));

    Long end_time = ((System.nanoTime() - start_time) / 1000000);
    Monitoring.CloudWatchTraffic(amazonCloudWatch,end_time.doubleValue(),"Search Result server","Serach Time");
    // Notify that this request was handled
    request.setHandled(true);

    // Convert data to JSON string and write to output
    ObjectMapper mapper = new ObjectMapper();
    mapper.enable(SerializationFeature.INDENT_OUTPUT);
    mapper.writeValue(httpServletResponse.getWriter(), results);
  }
}
