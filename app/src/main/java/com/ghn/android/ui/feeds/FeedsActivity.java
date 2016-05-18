package com.ghn.android.ui.feeds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.ghn.android.R;
import com.ghn.android.data.model.Feeds;
import com.ghn.android.data.model.News;
import com.ghn.android.views.DepthPageTransformer;
import com.ghn.android.views.VerticalViewPager;
import com.google.gson.Gson;
import com.thefinestartist.finestwebview.FinestWebView;
import com.thoughtw.retail.animation.GuillotineAnimation;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FeedsActivity extends AppCompatActivity implements View.OnClickListener, com.ghn.android.util.ToggleToolbarViewListener {

    private static final String EXTRA_TRIGGER_SYNC_FLAG =
            "uk.co.ribot.androidboilerplate.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";
    private static final long RIPPLE_DURATION = 250;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/jpg");
    public com.ghn.android.views.ExpandablePanel expandablePanel;
    public LinearLayout moreItemValueLayout;
    @Bind(R.id.toolBarMenu)
    Toolbar toolBarMenu;
    @Bind(R.id.epaper)
    ImageView ivEpaper;
    @Bind(R.id.refresh)
    ImageView ivrefresh;
    List<Fragment> fragments;
    @Bind(R.id.vertical_viewpager)
    VerticalViewPager viewPager;
    @Bind(R.id.progress)
    ProgressBar progressBar;
    @Bind(R.id.content_hamburger)
    View contentHamburger;
    @Bind(R.id.root)
    FrameLayout root;
    @Bind(R.id.tittle_fragment)
    com.ghn.android.views.FontTextView mTittleFragment;
    private GuillotineAnimation guillotineAnimation;
    private ImageView homeIcon;
    private com.ghn.android.views.FontTextView homeTittle;
    private ImageView electronicsIcon;
    private com.ghn.android.views.FontTextView electronicsTittle;
    private ImageView furnitureIcon;
    private com.ghn.android.views.FontTextView furnitureTittle;
    private ArrayList<com.ghn.android.data.model.News> listAllContent;
    private com.ghn.android.ui.feeds.FeedsAdapter feedsAdapter;
    private com.ghn.android.views.FontTextView tvMoreItem;
    //  private LinearLayout ajab_gajab_group, desh_group, videsh_group, rashifal_group, health_tips_group, election_keeda_group;
    private ImageView ivMoreItem;
    Response response;

    /**
     * Return an Intent to start this Activity.
     * triggerDataSyncOnCreate allows disabling the background sync service onCreate. Should
     * only be set to false during testing.
     */
    public static Intent getStartIntent(Context context, boolean triggerDataSyncOnCreate) {
        Intent intent = new Intent(context, FeedsActivity.class);
        intent.putExtra(EXTRA_TRIGGER_SYNC_FLAG, triggerDataSyncOnCreate);
        return intent;
    }

    @OnClick(R.id.refresh)
    public void refreshContent() {
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
                removeFragmentsFromPager();
                listAllContent = com.ghn.android.BoilerplateApplication.preference.getCartItems();
                if (listAllContent != null)
                    setAllContent();
                updateItemSelection(homeTittle, getResources().getString(R.string.activity));

            }
        }.execute();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_cards);
        ButterKnife.bind(this);
        ivEpaper.setOnClickListener(this);
        setUpToolBar();
        fragments = new ArrayList<>();
        feedsAdapter = new com.ghn.android.ui.feeds.FeedsAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(feedsAdapter);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.drawer_items_menu, null);
        expandablePanel = (com.ghn.android.views.ExpandablePanel) guillotineMenu.findViewById(R.id.expadable_panel);
        moreItemValueLayout = (LinearLayout) guillotineMenu.findViewById(R.id.more_root);
        expandablePanel.setCollapsedHeight(moreItemValueLayout.getHeight());
        expandablePanel.setOnExpandListener(new com.ghn.android.views.ExpandablePanel.OnExpandListener() {
            public void onCollapse(View handle, View content) {
                if (tvMoreItem == null) {
                    LinearLayout btn = (LinearLayout) handle;
                    tvMoreItem = (com.ghn.android.views.FontTextView) btn.findViewById(R.id.more_text);
                    ivMoreItem = (ImageView) btn.findViewById(R.id.more_icon);
                }
                tvMoreItem.setText("MORE");
                ivMoreItem.setImageResource(R.drawable.ic_action_more);
            }

            public void onExpand(View handle, View content) {
                if (tvMoreItem == null) {
                    LinearLayout btn = (LinearLayout) handle;
                    tvMoreItem = (com.ghn.android.views.FontTextView) btn.findViewById(R.id.more_text);
                    ivMoreItem = (ImageView) btn.findViewById(R.id.more_icon);
                }
                tvMoreItem.setText("LESS");
                ivMoreItem.setImageResource(R.drawable.ic_action_less);
            }
        });
        setUpOnClicks(guillotineMenu);
        setUpGuillotineAnimation(guillotineMenu);
        root.addView(guillotineMenu);
        listAllContent = com.ghn.android.BoilerplateApplication.preference.getCartItems();
        if (listAllContent != null)
            setAllContent();
        progressBar.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        try {
            viewPager.setOffscreenPageLimit(4);
        } catch (Exception e) {
            //ignore
        }
    }

    private void setUpGuillotineAnimation(View guillotineMenu) {
        initItemOfGuillotine(guillotineMenu);
        guillotineAnimation = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setStartDelay(RIPPLE_DURATION)
                .setActionBarViewForAnimation(toolBarMenu)
                .setClosedOnStart(true)
                .build();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

//    mMainPresenter.detachView();
    }


    private void setUpOnClicks(View guillotineMenu) {
        guillotineMenu.findViewById(R.id.activity_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.electronic_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.furniture_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.ajab_gajab_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.desh_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.videsh_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.rashifal_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.health_tips_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.election_keeda_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.contactus_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.upload_news_group).setOnClickListener(this);
        guillotineMenu.findViewById(R.id.epaper_group).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_group:
                updateItemSelection(homeIcon, homeTittle, R.drawable.ic_action_house_active, getResources().getString(R.string.activity));
                if (listAllContent != null)
                    setAllContent();
                break;
            case R.id.electronic_group:
                updateItemSelection(electronicsIcon, electronicsTittle, R.drawable.ic_action_plug_active, getResources().getString(R.string.electronics));
                if (listAllContent != null)
                    setAds();
                break;
            case R.id.furniture_group:
                updateItemSelection(furnitureIcon, furnitureTittle, R.drawable.ic_action_furniture_active, getResources().getString(R.string.furniture));
                if (listAllContent != null)
                    setJobs();
                break;
            case R.id.ajab_gajab_group:
                updateItemSelection(furnitureIcon, furnitureTittle, R.drawable.ic_action_furniture_active, getResources().getString(R.string.ajab_gajab));
                if (listAllContent != null)
                    setSelectedCategory("1");
                break;
            case R.id.desh_group:
                updateItemSelection(furnitureIcon, furnitureTittle, R.drawable.ic_action_furniture_active, getResources().getString(R.string.desh));
                if (listAllContent != null)
                    setSelectedCategory("2");
                break;
            case R.id.videsh_group:
                updateItemSelection(furnitureIcon, furnitureTittle, R.drawable.ic_action_furniture_active, getResources().getString(R.string.videsh));
                if (listAllContent != null)
                    setSelectedCategory("3");
                break;
            case R.id.rashifal_group:
                updateItemSelection(furnitureIcon, furnitureTittle, R.drawable.ic_action_furniture_active, getResources().getString(R.string.rashifal));
                if (listAllContent != null)
                    setSelectedCategory("4");
                break;
            case R.id.health_tips_group:
                updateItemSelection(furnitureIcon, furnitureTittle, R.drawable.ic_action_furniture_active, getResources().getString(R.string.health_tips));
                if (listAllContent != null)
                    setSelectedCategory("5");
                break;
            case R.id.epaper:
                new FinestWebView.Builder(getApplicationContext()).webViewBuiltInZoomControls(true).webViewDisplayZoomControls(true).webViewSupportZoom(true).show("http://globalherald.in/national-herald/");
                break;
            case R.id.election_keeda_group:
                updateItemSelection(furnitureIcon, furnitureTittle, R.drawable.ic_action_furniture_active, getResources().getString(R.string.election_keeda));
                if (listAllContent != null)
                    setSelectedCategory("6");
                break;
            case R.id.contactus_group:
                showContactUSDialog();
                break;
            case R.id.upload_news_group:
                startActivity(new Intent(this, UploadNewsActivity.class));
                break;
            case R.id.epaper_group:
                new FinestWebView.Builder(getApplicationContext()).webViewBuiltInZoomControls(true).webViewDisplayZoomControls(true).webViewSupportZoom(true).show("http://globalherald.in/national-herald/");
                break;
        }
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
                    hitCreateNewsApi(uris[0]);
                }
            }
        }
    }

    private void hitCreateNewsApi(Uri uri) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://216.158.225.126/~androidapp/index.php/api/uploadNews")
                .post(createNews(uri))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("response=", response.body().string());
            }
        });
    }

    private RequestBody createNews(Uri uri) {
        File file = new File(uri.getPath());
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("title", "Square Logo")
                .addFormDataPart("description", "Square Logo")
                .addFormDataPart("phone", "7828286616")
                .addFormDataPart("lang_id", "1")
                .addFormDataPart("news_image", file.getName(),
                        RequestBody.create(MEDIA_TYPE_PNG, file))
                .build();
        return requestBody;
    }

    private void showContactUSDialog() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_contactus, null);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.show();
        dialogView.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }


    private void updateItemSelection(ImageView activeImage, com.ghn.android.views.FontTextView activeTittle, int icon, String toolBarTittle) {
        resetViewsItems();
        //   activeImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), icon, null));
        activeTittle.setTextColor(Color.parseColor("#30d1d5"));
        mTittleFragment.setText(toolBarTittle);
        guillotineAnimation.close();
    }

    private void updateItemSelection(com.ghn.android.views.FontTextView activeTittle, String toolBarTittle) {
        resetViewsItems();
        activeTittle.setTextColor(Color.parseColor("#30d1d5"));
        mTittleFragment.setText(toolBarTittle);
    }

    private void resetViewsItems() {
        homeIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_home, null));
        electronicsIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_ads, null));
        furnitureIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_jobs, null));
        homeTittle.setTextColor(Color.parseColor("#ffffff"));
        electronicsTittle.setTextColor(Color.parseColor("#ffffff"));
        furnitureTittle.setTextColor(Color.parseColor("#ffffff"));
    }

    private void initItemOfGuillotine(View view) {
        electronicsIcon = (ImageView) view.findViewById(R.id.elec_icon);
        furnitureIcon = (ImageView) view.findViewById(R.id.furniture_icon);
        homeIcon = (ImageView) view.findViewById(R.id.home_icon);
        electronicsTittle = (com.ghn.android.views.FontTextView) view.findViewById(R.id.elec_tittle);
        furnitureTittle = (com.ghn.android.views.FontTextView) view.findViewById(R.id.furniture_tittle);
        homeTittle = (com.ghn.android.views.FontTextView) view.findViewById(R.id.home_tittle);
    }


    private void setAllContent() {
        removeFragmentsFromPager();
        for (int i = 0; i < listAllContent.size(); i++) {
            com.ghn.android.data.model.News news = listAllContent.get(i);
            fragments.add(ContentFragment.newInstance(news, i));
        }
        feedsAdapter.notifyDataSetChanged();
    }

    private void setJobs() {
        removeFragmentsFromPager();
        for (int i = 0; i < listAllContent.size(); i++) {
            com.ghn.android.data.model.News news = listAllContent.get(i);
            if (news.getContent_type().equals("2")) {
                fragments.add(com.ghn.android.ui.feeds.ContentFragment.newInstance(news, i));
            }
        }
        feedsAdapter.notifyDataSetChanged();
    }

    private void setSelectedCategory(String categoryType) {
        removeFragmentsFromPager();
        for (int i = 0; i < listAllContent.size(); i++) {
            com.ghn.android.data.model.News news = listAllContent.get(i);

            if (news.getCategory_id() != null && news.getCategory_id().equals(categoryType)) {
                fragments.add(com.ghn.android.ui.feeds.ContentFragment.newInstance(news, i));
            }
        }
        feedsAdapter.notifyDataSetChanged();
    }

    @Override
    public void toggleVisibilityOfToolbar() {
        if (toolBarMenu.getVisibility() == View.VISIBLE) {
            toolBarMenu.setVisibility(View.GONE);
        } else {
            toolBarMenu.setVisibility(View.VISIBLE);
        }
    }

    private void setAds() {
        removeFragmentsFromPager();
        for (int i = 0; i < listAllContent.size(); i++) {
            com.ghn.android.data.model.News news = listAllContent.get(i);
            if (news.getContent_type().equals("3")) {
                fragments.add(com.ghn.android.ui.feeds.ContentFragment.newInstance(news, i));
            }
        }
        feedsAdapter.notifyDataSetChanged();
    }

    private void removeFragmentsFromPager() {
        fragments.clear();
    }

    private void setUpToolBar() {
        if (toolBarMenu != null) {
            setSupportActionBar(toolBarMenu);
            getSupportActionBar().setTitle(null);
        }
    }
}
