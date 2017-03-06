package com.olafurtorfi.www.podcastmarket.data;

/**
 * Created by olitorfi on 21/02/2017.
 */

public class EpisodeObject {
    public String title;
    public String description;
    public String podcast;
    public String author;
    public String filePath;
    public String fileUrl;

    public EpisodeObject(String title, String description, String podcast, String filePath, String fileUrl, String author) {
        this.title = title;
        this.description = description;
        this.podcast = podcast;
        this.filePath = filePath;
        this.fileUrl = fileUrl;
        this.author = author;
    }
}
