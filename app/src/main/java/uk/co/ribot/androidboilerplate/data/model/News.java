package uk.co.ribot.androidboilerplate.data.model;

import java.io.Serializable;

public class News implements Serializable {
  String id;
  String content_type;
  String language_id;
  String title;
  String news_image;
  String description;
  String date_posted;
  String updated_at;
  String category_id;

  public String getCategory_id() {
    return category_id;
  }

  public void setCategory_id(String category_id) {
    this.category_id = category_id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getContent_type() {
    return content_type;
  }

  public void setContent_type(String content_type) {
    this.content_type = content_type;
  }

  public String getLanguage_id() {
    return language_id;
  }

  public void setLanguage_id(String language_id) {
    this.language_id = language_id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getNews_image() {
    return news_image;
  }

  public void setNews_image(String news_image) {
    this.news_image = news_image;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDate_posted() {
    return date_posted;
  }

  public void setDate_posted(String date_posted) {
    this.date_posted = date_posted;
  }

  public String getUpdated_at() {
    return updated_at;
  }

  public void setUpdated_at(String updated_at) {
    this.updated_at = updated_at;
  }

}