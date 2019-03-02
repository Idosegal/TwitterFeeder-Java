package il.ac.colman.cs.util;

public class MyJson {

    private String _url;
    private String _track;
    private long _tweetID;


    public MyJson() {}

    public MyJson(String url, long tweet_ID,String track) {
        this.set_url(url);
        this.set_track(track);
        this.set_tweetID(tweet_ID);
    }


    public void set_url(String url) {
        this._url = url;
    }

    public void set_track(String track) {
        this._track = track;
    }

    public void set_tweetID(long tweetID) {this._tweetID = tweetID; }

    public String get_url() {
        return this._url;
    }

    public String get_track() {
        return this._track;
    }

    public long get_tweetID() {return this._tweetID;}
}