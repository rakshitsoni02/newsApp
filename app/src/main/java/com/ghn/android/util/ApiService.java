package com.ghn.android.util;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ApiService {
  public static final MediaType JSON
      = MediaType.parse("application/json; charset=utf-8");
 // String ENDPOINT = "http://amantran.xyz/api/";
  String ENDPOINT = "http://216.158.225.126/~androidapp/index.php/api/";
  RequestBody formBody;

  public ApiService(RequestBody formBody) {
    this.formBody = formBody;
  }

  public Response getNews() {

    return getResponse(ENDPOINT + "getContent");
  }


  private Response getResponse(String url) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder()
        .url(url)
        .post(formBody)
        .build();
    Response httpResponse = null;
    try {
      httpResponse = client.newCall(request).execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return httpResponse;
  }
}