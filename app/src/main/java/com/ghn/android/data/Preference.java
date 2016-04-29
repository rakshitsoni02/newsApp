package com.ghn.android.data;


import android.content.SharedPreferences;

        import com.google.gson.Gson;
        import com.google.gson.reflect.TypeToken;

        import java.lang.reflect.Type;
        import java.util.ArrayList;

public class Preference {
  public static final String RETAIL_SHARED_PREFERENCES_FILE = "newsapp";
  public static final String NEWS_ITEM = "news";
  public static final String LAST_SYNC = "sync";
  private SharedPreferences preferences;

  public Preference(SharedPreferences preferences) {
    this.preferences = preferences;
  }

  public ArrayList<com.ghn.android.data.model.News> getCartItems() {
    String json = preferences.getString(NEWS_ITEM, null);
    Type type = new TypeToken<ArrayList<com.ghn.android.data.model.News>>() {
    }.getType();
    return new Gson().fromJson(json, type);
  }

  public void setCartItems(ArrayList<com.ghn.android.data.model.News> list) {
    preferences.edit().putString(NEWS_ITEM, new Gson().toJson(list)).commit();
  }


  public String getLastSync() {
    String value = preferences.getString(LAST_SYNC, "");
    return value;
  }

  public void setLastSync(String value) {
    preferences.edit().putString(LAST_SYNC, value).commit();
  }


}
