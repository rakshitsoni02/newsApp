package com.ghn.android.ui.feeds;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ghn.android.BoilerplateApplication;
import com.ghn.android.R;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadNewsActivity extends AppCompatActivity {
    @Bind(R.id.upload_image)
    ImageView ivNewsImage;
    @Bind(R.id.tittle_news_wrapper)
    TextInputLayout tilTittle;
    @Bind(R.id.desc_news_wrapper)
    TextInputLayout tilDescription;
    @Bind(R.id.phone_number_wrapper)
    TextInputLayout tilPhoneNumber;
    @Bind(R.id.btn_upload)
    Button btnUploadData;
    @Bind(R.id.toolBarMenu)
    Toolbar toolBarMenu;
    String tittle = "", description = "", phoneNumber = "";
    Uri uri;
    MaterialDialog materialDialog;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_news);
        ButterKnife.bind(this);
        setUpToolBar();
        setPhoneNumber();
    }

    private void setUpToolBar() {
        if (toolBarMenu != null) {
            setSupportActionBar(toolBarMenu);
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @OnClick(R.id.upload_image)
    public void openImageSelector() {
        getImages();
    }

    @OnClick(R.id.btn_upload)
    public void procesUploadingNews() {
        if (com.ghn.android.util.NetworkUtil.isNetworkConnected(this)) {
            if (validateData()) {
                hitCreateNewsApi();
            }
        } else
            BoilerplateApplication.showToast(UploadNewsActivity.this, "Please check Network Connection");
    }

    private Boolean validateData() {
        Boolean valid = true;
        hideKeyboard();
        tittle = tilTittle.getEditText().getText().toString();
        description = tilDescription.getEditText().getText().toString();
        phoneNumber = tilPhoneNumber.getEditText().getText().toString();
        if (tittle.equals("")) {
            valid = false;
            tilTittle.setError("Not a valid news tittle!");
        } else {
            tilTittle.setErrorEnabled(false);
        }
        if (phoneNumber.equals("")) {
            valid = false;
            tilPhoneNumber.setError("Not a valid phone number!");
        } else {
            tilPhoneNumber.setErrorEnabled(false);
        }
        if (description.equals("")) {
            valid = false;
            tilDescription.setError("Not a valid news description!");
        } else {
            tilDescription.setErrorEnabled(false);
        }
        if (uri == null) {
            valid = false;
        }
        return valid;
    }

    private void getImages() {
        Intent intent = new Intent(this, ImagePickerActivity.class);
        Config config = new Config.Builder()
                .setTabBackgroundColor(R.color.primary)    // set tab background color. Default white.
                .setTabSelectionIndicatorColor(R.color.white)
                .setCameraButtonColor(R.color.finestWhite)
                .setSelectionLimit(1)    // set photo selection limit. Default unlimited selection.
                .build();
        ImagePickerActivity.setConfig(config);
        startActivityForResult(intent, INTENT_REQUEST_GET_IMAGES);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {
                Parcelable[] parcelableUris = intent.getParcelableArrayExtra(ImagePickerActivity.EXTRA_IMAGE_URIS);

                if (parcelableUris == null) {
                    return;
                }

                // Java doesn't allow array casting, this is a little hack
                Uri[] uris = new Uri[parcelableUris.length];
                System.arraycopy(parcelableUris, 0, uris, 0, parcelableUris.length);

                if (uris != null) {
                    uri = uris[0];
                    ivNewsImage.setImageURI(uri);
                }
            }
        }
    }

    private void hitCreateNewsApi() {
        showProgressDialog();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://216.158.225.126/~androidapp/index.php/api/uploadNews")
                .post(createNews())
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                materialDialog.dismiss();
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("response=", response.body().string());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BoilerplateApplication.showToast(UploadNewsActivity.this, "News added successfully!");
                        materialDialog.dismiss();
                    }
                });
                finish();
            }
        });
    }

    private RequestBody createNews() {
        File file = new File(uri.getPath());
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", tittle)
                .addFormDataPart("description", description)
                .addFormDataPart("phone", phoneNumber)
                .addFormDataPart("lang_id", "1")
                .addFormDataPart("news_image", file.getName(),
                        RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();
        return requestBody;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPhoneNumber() {
        if (checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            tilPhoneNumber.getEditText().setText(manager.getLine1Number());
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void showProgressDialog() {
        materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.wait)
                .content(R.string.loading)
                .progress(true, 0)
                .cancelable(false)
                .show();
    }
}
