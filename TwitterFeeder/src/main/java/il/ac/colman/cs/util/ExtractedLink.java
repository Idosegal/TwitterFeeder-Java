package il.ac.colman.cs.util;

public class ExtractedLink {
  private final String url;
  private final String content;
  private final String title;
  private final String description;
  private final String screenshotURL;

  public ExtractedLink(String url, String content, String title, String description, String screenshotURL) {
    this.url = url;
    this.content = content;
    this.title = title;
    this.description = description;
    this.screenshotURL = screenshotURL;
  }

  public String getUrl() {
    return url;
  }

  public String getContent() {
    return content;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public String getScreenshotURL() {
    return screenshotURL;
  }

  @Override
  public String toString() {
    return "ExtractedLink:\n" +
            "url=" + url + '\n' +
            "content=" + content + '\n' +
            "title=" + title + '\n' +
            "description=" + description + '\n' +
            "screenshotURL=" + screenshotURL + '\n';
  }
}
