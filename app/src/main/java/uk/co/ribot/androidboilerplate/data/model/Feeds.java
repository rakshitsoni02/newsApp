package uk.co.ribot.androidboilerplate.data.model;


import java.io.Serializable;
import java.util.List;

public class Feeds implements Serializable {
  Data data;

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public Feeds() {
  }

  public class Data implements Serializable {
    public List<News> news;
    String image_path;

    public String getImage_path() {
      return image_path;
    }

    public void setImage_path(String image_path) {
      this.image_path = image_path;
    }

    public List<News> getNews() {
      return news;
    }

    public void setNews(List<News> news) {
      this.news = news;
    }
  }


}
