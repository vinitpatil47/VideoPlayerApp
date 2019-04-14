package com.example.vinit.vplayer;

public class Video
{
    private String title;
    private String description;
    private String user_uid;
    private int views;
    private String thumb_url;
    private String video_url;
    private String valid;

    public Video(){}

    public Video(String title, String description, String user_uid, int views, String thumb_url, String video_url, String valid) {
        this.title = title;
        this.description = description;
        this.user_uid = user_uid;
        this.views = views;
        this.thumb_url = thumb_url;
        this.video_url = video_url;
        this.valid = valid;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }
}
