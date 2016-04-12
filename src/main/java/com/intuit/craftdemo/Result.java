package com.intuit.craftdemo;

/**
 * Created by karthi on 4/12/16.
 */
public class Result {
    public String title;
    public Object description;
    public String license;
    public String tag_list;
    public String download_url;
    public String stream_url;
    public String last_modified;
    public int duration;
    public int id;

    public static class Builder {
        public Builder(String title) {
            this.title = title;
        }

        public Builder description(Object description) {
            this.description = description;
            return this;
        }
        public Builder license(String license) {
            this.license = license;
            return this;
        }
        public Builder tag_list(String tag_list) {
            this.tag_list = tag_list;
            return this;
        }
        public Builder download_url(String download_url) {
            this.download_url = download_url;
            return this;
        }
        public Builder stream_url(String stream_url) {
            this.stream_url = stream_url;
            return this;
        }
        public Builder last_modified(String last_modified) {
            this.last_modified = last_modified;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Result build() {
            return new Result(this);
        }

        String title;
        Object description;
        String license;
        String tag_list;
        String download_url;
        String stream_url;
        String last_modified;
        int duration;
        int id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Title: ").append(title).append("\n");
        sb.append("License: ").append(license).append("\n");
        sb.append("Tag List: ").append(tag_list).append("\n");
        sb.append("Download URL: ").append(download_url).append("\n");
        sb.append("Stream URL: ").append(stream_url).append("\n");
        sb.append("Last Modified: ").append(last_modified).append("\n");
        sb.append("Duration: ").append(duration).append("\n");
        sb.append("ID: ").append(id).append("\n");
        if ( description != null )
            sb.append("Description: ").append(description).append("\n");
        else
            sb.append("Description: null\n");

        return sb.toString();
    }

    private Result(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.license = builder.license;
        this.tag_list = builder.tag_list;
        this.download_url = builder.download_url;
        this.stream_url = builder.stream_url;
        this.last_modified = builder.last_modified;
        this.duration = builder.duration;
        this.id = builder.id;
    }
}
