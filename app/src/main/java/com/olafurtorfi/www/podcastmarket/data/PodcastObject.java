package com.olafurtorfi.www.podcastmarket.data;

/**
 * Created by olitorfi on 14/03/2017.
 */

public class PodcastObject {
    String title;
    String author;
    String description;
    String url;

    public PodcastObject(String title_, String author_, String description_, String url_) {
        title = title_;
        author = author_;
        description = description_;
        url = url_;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}
