package com.ghn.android.data.model;


import java.io.Serializable;
import java.util.List;

public class Feeds implements Serializable {
  Data data;

  public Feeds() {
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public class Data implements Serializable {
    public List<com.ghn.android.data.model.News> news;
    String image_path;

    public String getImage_path() {
      return image_path;
    }

    public void setImage_path(String image_path) {
      this.image_path = image_path;
    }

    public List<com.ghn.android.data.model.News> getNews() {
      return news;
    }

    public void setNews(List<com.ghn.android.data.model.News> news) {
      this.news = news;
    }
  }


}
