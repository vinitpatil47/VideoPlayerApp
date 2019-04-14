package com.example.vinit.vplayer;

public class videokey
{
    private String video_key;
    private String valid;

    public videokey(){}

    public videokey(String video_key, String valid) {
        this.video_key = video_key;
        this.valid = valid;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getVideo_key() {
        return video_key;
    }

    public void setVideo_key(String video_key) {
        this.video_key = video_key;
    }
}
