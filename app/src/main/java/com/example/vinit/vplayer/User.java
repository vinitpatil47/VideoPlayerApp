package com.example.vinit.vplayer;

public class User
{
    private String first_name;
    private String last_name;
    private String email_id;
    private String profile_url;

    public User(){}

    public User(String first_name, String last_name, String email_id, String profile_url) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email_id = email_id;
        this.profile_url = profile_url;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail_id() {
        return email_id;
    }

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public void setProfile_url(String profile_url) {
        this.profile_url = profile_url;
    }
}
