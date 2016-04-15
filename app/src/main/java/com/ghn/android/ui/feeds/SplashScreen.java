package com.ghn.android.ui.feeds;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ghn.android.BoilerplateApplication;
import com.ghn.android.R;
import com.ghn.android.data.model.Feeds;
import com.ghn.android.data.model.News;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashScreen extends AppCompatActivity {

  private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
  private static final String TAG = "AppActivity";
  // Splash screen timer
  private static int SPLASH_TIME_OUT = 3000;
  Response response;
  private String firstTimeRun = "";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);
    //if (checkPlayServices()) Log.e(TAG, "Working play");
    if (com.ghn.android.util.NetworkUtil.isNetworkConnected(this)) {
      useAsyncTaskForSplashScreenTimeOut();
    } else {
      BoilerplateApplication.showToast(SplashScreen.this, "Please check Network Connection");
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
        if (com.ghn.android.BoilerplateApplication.preference.getCartItems() == null) {
          formBody = new FormBody.Builder()
              .add("limit", "500")
                  //    .add("langId", "0")
              .build();
          InstanceID instanceID = InstanceID.getInstance(SplashScreen.this);
          try {
            firstTimeRun = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
          } catch (IOException e) {
            e.printStackTrace();
          }
        } else {
          formBody = new FormBody.Builder()
              .add("updateBy", com.ghn.android.BoilerplateApplication.preference.getLastSync())
              .add("limit", "500")
                  // .add("langId", "0")
              .build();
        }
        try {
          response = new com.ghn.android.util.ApiService(formBody).getNews();
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
        if (!firstTimeRun.equals("")) {
          try {
            sendTokenToServer();
          } catch (IOException e) {
            e.printStackTrace();
          }
        } else
          navigateToMainScreen();
      }

    }.execute();

  }

  private void sendTokenToServer() throws IOException {
    RequestBody formBody = new FormBody.Builder()
        .add("token", firstTimeRun)
        .build();
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url("http://www.amantran.xyz/api/addToken")
        .post(formBody)
        .build();
    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        e.printStackTrace();
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
//        Log.e("response token", response.body().toString());
        navigateToMainScreen();
      }
    });
  }

  private void navigateToMainScreen() {
    Intent i = new Intent(SplashScreen.this, com.ghn.android.ui.feeds.FeedsActivity.class);
    startActivity(i);
    // close this activity
    finish();
  }

  private void processNewsData(ArrayList<News> list) {
    com.ghn.android.BoilerplateApplication.preference.setLastSync(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(new Date()));
    if (com.ghn.android.BoilerplateApplication.preference.getCartItems() == null) {
      com.ghn.android.BoilerplateApplication.preference.setCartItems(list);
    } else {
      removeAlreadyExistingItemsInData(list);
      int sumOfItems = com.ghn.android.BoilerplateApplication.preference.getCartItems().size() + list.size();
      if (sumOfItems > 500) {
        int rowsToRemove = sumOfItems - 500;
        com.ghn.android.BoilerplateApplication.preference.setCartItems((ArrayList<com.ghn.android.data.model.News>) com.ghn.android.BoilerplateApplication.preference.getCartItems().
            subList(0, com.ghn.android.BoilerplateApplication.preference.getCartItems().size() - (rowsToRemove + 1)));
      }
      list.addAll(com.ghn.android.BoilerplateApplication.preference.getCartItems());
      com.ghn.android.BoilerplateApplication.preference.setCartItems(list);
    }
  }

  private void removeAlreadyExistingItemsInData(ArrayList<News> list) {
    ArrayList<News> storeListInPreference = com.ghn.android.BoilerplateApplication.preference.getCartItems();
    if (list.size() != 0)
      for (News newsNew : list) {
        for (Iterator<News> it = storeListInPreference.iterator(); it.hasNext(); ) {
          News s = it.next();
          if (s.getId().equals(newsNew.getId())) {
            it.remove();
          }
        }
      }
    com.ghn.android.BoilerplateApplication.preference.setCartItems(storeListInPreference);
  }

  private boolean checkPlayServices() {
    GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
    int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
    if (resultCode != ConnectionResult.SUCCESS) {
      if (apiAvailability.isUserResolvableError(resultCode)) {
        apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
            .show();
      } else {
        Log.i(TAG, "This device is not supported.");
        //  finish();
      }
      return false;
    }
    return true;
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
        Intent i = new Intent(SplashScreen.this, com.ghn.android.ui.feeds.FeedsActivity.class);
        startActivity(i);
        // close this activity
        finish();
      }
    }, SPLASH_TIME_OUT);
  }

}