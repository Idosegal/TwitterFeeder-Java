package il.ac.colman.cs.util;

import com.amazonaws.services.s3.AmazonS3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
  Connection conn;

  public DataStorage() {
    this.conn = getRemoteConnection();
  }

  private static Connection getRemoteConnection() {
    if (System.getProperty("RDS_HOSTNAME") != null) {
      try {

        Class.forName("com.mysql.cj.jdbc.Driver");
        String dbName = System.getProperty("RDS_DB_NAME");
        String userName = System.getProperty("RDS_USERNAME");
        String password = System.getProperty("RDS_PASSWORD");
        String hostname = System.getProperty("RDS_HOSTNAME");
        String port = System.getProperty("RDS_PORT");
        String jdbcUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?user=" + userName + "&password=" + password;
        Connection con = DriverManager.getConnection(jdbcUrl);
        System.out.println("The connection with the database succeeded");

        return con;
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  public void createTable() {
    Statement statement = null;
    try {

      statement = this.conn.createStatement();
      String sql = "CREATE TABLE  IF NOT EXISTS Ido_DB" +
              "(PRIMARYID INT NOT NULL AUTO_INCREMENT PRIMARY KEY," +
              "LINK  TEXT," +
              "TWEETID LONG NOT NULL," +
              "TITLE   TEXT," +
              "CONTENT  TEXT," +
              "TIMESTAMP DATETIME," +
              "SCREENSHOT TEXT," +
              "TRACK TEXT)";
      statement.executeUpdate(sql);
      statement.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(0);
    }
    System.out.println("The table created");
  }

  /**
   * Search for a link
   *
   * @param query The query to search
   */
  public List<ExtractedLink> search(String query) {
    /*
    Search for query in the database and return the results
     */
    String sql_query = "SELECT * FROM Ido_DB WHERE CONTENT LIKE ?";
    try {

      PreparedStatement p = conn.prepareStatement(sql_query);
      p.setString(1,"%" + query + "%");
      ResultSet resultSet;
      resultSet = p.executeQuery();
      ArrayList<ExtractedLink> tweets = new ArrayList<ExtractedLink>();

      while(resultSet.next())
      {
        String description;
        String content = resultSet.getString("CONTENT");
        if(content.length() >= 100)
            description = content.substring(0,99);

        else
            description = content;

        ExtractedLink link = new ExtractedLink(resultSet.getString("LINK"),content,resultSet.getString("TITLE")
                ,description, resultSet.getString("SCREENSHOT"));
        tweets.add(link);
      }
      return tweets;

    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public  void DeleteTable(){
        String delete = "DROP TABLE Ido_DB";
    Statement statement = null;
    System.out.println("start DELETE");
    try {
      statement = this.conn.createStatement();
      statement.executeUpdate(delete);

    } catch (SQLException e) {
      e.printStackTrace();
    }
    System.out.println("DELETED");
    System.exit(0);
  }

  public void insertTable(ExtractedLink info, Long tweetID, String track) {

    try {
      DeleteFromDB();
      conn.setAutoCommit(false);
      System.out.println("Open the database");
      String insert_sql_statement = "INSERT INTO Ido_DB" +
              "(LINK,TWEETID,TITLE,CONTENT,TIMESTAMP,SCREENSHOT,TRACK) VALUES" +
              "(?,?,?,?,?,?,?)";
      PreparedStatement preparedStatement = conn.prepareStatement(insert_sql_statement);
      preparedStatement.setString(1,info.getUrl());
      preparedStatement.setLong(2,tweetID);
      preparedStatement.setString(3,info.getTitle());
      preparedStatement.setString(4,info.getContent());
      preparedStatement.setTimestamp(5,new Timestamp((System.currentTimeMillis())));
      preparedStatement.setString(6,info.getScreenshotURL());
      preparedStatement.setString(7,track);

      preparedStatement.executeUpdate();
      preparedStatement.close();
      conn.commit();
    } catch (Exception e) {
      System.err.println(e.getClass().getName()+ ": " +e.getMessage());
      System.exit(0);
    }
  System.out.println("Insert Successful");
  }


  private void DeleteFromDB()
  {
    int rows_number = 0;
    ResultSet set;
    try {
      Statement statement = conn.createStatement();
      String query = "SELECT COUNT(*) FROM Ido_DB";
      set = statement.executeQuery(query);
      if(set.next())
        rows_number = set.getInt("COUNT(*)");
      if(rows_number >= 1000)
      {
        String screenshot = "";
        String id = "";
        query = "SELECT * FROM Ido_DB ORDER BY PRIMARYID LIMIT 1 ";
        set = statement.executeQuery(query);
        if(set.next()){
          screenshot = ((set.getString("SCREENSHOT")).substring(1));
          id = set.getString("PRIMARYID");
          AmazonS3 clientS3 = AWScred.getS3Client();
          String bucket_name = System.getProperty("BUCKET");
          clientS3.deleteObject(bucket_name,screenshot);

          query = "DELETE FROM Ido_DB WHERE PRIMARYID ="+id;
          statement.executeUpdate(query);
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
