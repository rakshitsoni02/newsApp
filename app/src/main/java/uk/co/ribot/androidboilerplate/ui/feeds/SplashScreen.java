package uk.co.ribot.androidboilerplate.ui.feeds;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import uk.co.ribot.androidboilerplate.BoilerplateApplication;
import uk.co.ribot.androidboilerplate.R;
import uk.co.ribot.androidboilerplate.data.model.Feeds;
import uk.co.ribot.androidboilerplate.data.model.News;
import uk.co.ribot.androidboilerplate.util.ApiService;
import uk.co.ribot.androidboilerplate.util.NetworkUtil;

public class SplashScreen extends AppCompatActivity {

  // Splash screen timer
  private static int SPLASH_TIME_OUT = 3000;
  Response response;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    if (NetworkUtil.isNetworkConnected(this)) {
      useAsyncTaskForSplashScreenTimeOut();
    } else {
      useHandlerForSplashScreenTimeOut();
    }

  }

  private void useAsyncTaskForSplashScreenTimeOut() {
    getNewsFromApi();
  }

  private void getNewsFromApi() {

    new AsyncTask<Void, Void, Void>() {
      @Override
      protected Void doInBackground(Void... params) {
        RequestBody formBody = null;
        if (BoilerplateApplication.preference.getCartItems() == null) {
          formBody = new FormBody.Builder()
              .add("limit", "500")
                  //    .add("langId", "0")
              .build();
        } else {
          formBody = new FormBody.Builder()
              .add("updateBy", BoilerplateApplication.preference.getLastSync())
              .add("limit", "500")
                  // .add("langId", "0")
              .build();
        }
        try {
          response = new ApiService(formBody).getNews();
          if (response.code() == 200) {
            Gson gson = new Gson();
            Feeds feeds = gson.fromJson(response.body().charStream(), Feeds.class);
            ArrayList<News> list = (ArrayList<News>) feeds.getData().getNews();
            processNewsData(list);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }

      @Override
      protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent i = new Intent(SplashScreen.this, FeedsActivity.class);
        startActivity(i);
        // close this activity
        finish();
      }

    }.execute();

  }

  private void processNewsData(ArrayList<News> list) {
    BoilerplateApplication.preference.setLastSync(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date()));
    if (BoilerplateApplication.preference.getCartItems() == null) {
      BoilerplateApplication.preference.setCartItems(list);
    } else {
      int sumOfItems = BoilerplateApplication.preference.getCartItems().size() + list.size();
      if (sumOfItems > 500) {
        int rowsToRemove = sumOfItems - 500;
        BoilerplateApplication.preference.setCartItems((ArrayList<News>) BoilerplateApplication.preference.getCartItems().
            subList(0, BoilerplateApplication.preference.getCartItems().size() - (rowsToRemove + 1)));
      }
      list.addAll(BoilerplateApplication.preference.getCartItems());
      BoilerplateApplication.preference.setCartItems(list);
    }
  }

  private void useHandlerForSplashScreenTimeOut() {
    new Handler().postDelayed(new Runnable() {

			/*
       * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */

      @Override
      public void run() {
        // This method will be executed once the timer is over
        // Start your app main activity
        Intent i = new Intent(SplashScreen.this, FeedsActivity.class);
        startActivity(i);
        // close this activity
        finish();
      }
    }, SPLASH_TIME_OUT);
  }

}