package uk.co.ribot.androidboilerplate.ui.feeds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.thefinestartist.finestwebview.FinestWebView;
import com.thefinestartist.ytpa.YouTubePlayerActivity;
import com.thefinestartist.ytpa.enums.Orientation;
import com.thoughtw.retail.animation.GuillotineAnimation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import nl.changer.polypicker.Config;
import nl.changer.polypicker.ImagePickerActivity;
import uk.co.ribot.androidboilerplate.BoilerplateApplication;
import uk.co.ribot.androidboilerplate.R;
import uk.co.ribot.androidboilerplate.data.SyncService;
import uk.co.ribot.androidboilerplate.data.model.News;
import uk.co.ribot.androidboilerplate.data.model.Ribot;
import uk.co.ribot.androidboilerplate.ui.base.BaseActivity;
import uk.co.ribot.androidboilerplate.util.DialogFactory;
import uk.co.ribot.androidboilerplate.util.ToggleToolbarViewListener;
import uk.co.ribot.androidboilerplate.views.ExpandablePanel;
import uk.co.ribot.androidboilerplate.views.FontTextView;

public class FeedsActivity extends BaseActivity implements FeedsMvpView, View.OnClickListener, ToggleToolbarViewListener {

  private static final String EXTRA_TRIGGER_SYNC_FLAG =
      "uk.co.ribot.androidboilerplate.ui.main.MainActivity.EXTRA_TRIGGER_SYNC_FLAG";
  @Bind(R.id.toolBarMenu)
  Toolbar toolBarMenu;
  @Bind(R.id.epaper)
  ImageView ivEpaper;
  @Inject
  FeedsPresenter mMainPresenter;
  List<Fragment> fragments;
  @Bind(R.id.vertical_viewpager)
  uk.co.ribot.androidboilerplate.views.VerticalViewPager viewPager;
  @Bind(R.id.progress)
  ProgressBar progressBar;
  @Bind(R.id.content_hamburger)
  View contentHamburger;
  @Bind(R.id.root)
  FrameLayout root;
  @Bind(R.id.tittle_fragment)
  FontTextView mTittleFragment;
  public ExpandablePanel expandablePanel;
  public LinearLayout moreItemValueLayout;
  private static final long RIPPLE_DURATION = 250;
  private GuillotineAnimation guillotineAnimation;
  private ImageView homeIcon;
  private FontTextView homeTittle;
  private ImageView electronicsIcon;
  private FontTextView electronicsTittle;
  private ImageView furnitureIcon;
  private FontTextView furnitureTittle;
  private ArrayList<News> listAllContent;
  private FeedsAdapter feedsAdapter;
  private FontTextView tvMoreItem;
  //  private LinearLayout ajab_gajab_group, desh_group, videsh_group, rashifal_group, health_tips_group, election_keeda_group;
  private ImageView ivMoreItem;
  private static final int INTENT_REQUEST_GET_IMAGES = 13;

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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_cards);
    ButterKnife.bind(this);
    ivEpaper.setOnClickListener(this);
    if (getIntent().getBooleanExtra(EXTRA_TRIGGER_SYNC_FLAG, true)) {
      startService(SyncService.getStartIntent(this));
    }
    setUpToolBar();
    fragments = new ArrayList<>();
    feedsAdapter = new FeedsAdapter(getSupportFragmentManager(), fragments);
    viewPager.setAdapter(feedsAdapter);
    viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
    viewPager.setOffscreenPageLimit(3);
    View guillotineMenu = LayoutInflater.from(this).inflate(R.layout.drawer_items_menu, null);
    expandablePanel = (ExpandablePanel) guillotineMenu.findViewById(R.id.expadable_panel);
    moreItemValueLayout = (LinearLayout) guillotineMenu.findViewById(R.id.more_root);
    expandablePanel.setCollapsedHeight(moreItemValueLayout.getHeight());
    expandablePanel.setOnExpandListener(new ExpandablePanel.OnExpandListener() {
      public void onCollapse(View handle, View content) {
        if (tvMoreItem == null) {
          LinearLayout btn = (LinearLayout) handle;
          tvMoreItem = (FontTextView) btn.findViewById(R.id.more_text);
          ivMoreItem = (ImageView) btn.findViewById(R.id.more_icon);
        }
        tvMoreItem.setText("MORE");
        ivMoreItem.setImageResource(R.drawable.ic_action_more);
      }

      public void onExpand(View handle, View content) {
        if (tvMoreItem == null) {
          LinearLayout btn = (LinearLayout) handle;
          tvMoreItem = (FontTextView) btn.findViewById(R.id.more_text);
          ivMoreItem = (ImageView) btn.findViewById(R.id.more_icon);
        }
        tvMoreItem.setText("LESS");
        ivMoreItem.setImageResource(R.drawable.ic_action_less);
      }
    });
    setUpOnClicks(guillotineMenu);
    setUpGuillotineAnimation(guillotineMenu);
    root.addView(guillotineMenu);
    listAllContent = BoilerplateApplication.preference.getCartItems();
    if (listAllContent != null)
      setAllContent();
    progressBar.setVisibility(View.GONE);
    viewPager.setVisibility(View.VISIBLE);
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

  /*****
   * MVP View methods implementation
   *****/

  @Override
  public void showRibots(List<Ribot> ribots) {
//    mRibotsAdapter.setRibots(ribots);
//    mRibotsAdapter.notifyDataSetChanged();
  }

  @Override
  public void showError() {
    DialogFactory.createGenericErrorDialog(this, getString(R.string.error_loading_ribots))
        .show();
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
        if (listAllContent != null)
          setSelectedCategory("6");
        break;
      case R.id.contactus_group:
        showContactUSDialog();
        break;
      case R.id.upload_news_group:
        getImages();
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
          for (Uri uri : uris) {
            Log.i("images", " uri: " + uri);

          }

        }
      }
    }
  }

  private void showContactUSDialog() {
    final AlertDialog.Builder builder =
        new AlertDialog.Builder(this);
    View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_contactus, null);
    builder.setView(dialogView);
    final AlertDialog dialog = builder.show();
    dialogView.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dialog.dismiss();
      }
    });
  }

  private void startYoutubeVideo() {
    Intent intent = new Intent(FeedsActivity.this, YouTubePlayerActivity.class);
// Youtube video ID (Required, You can use YouTubeUrlParser to parse Video Id from url)
    intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, "iS1g8G_njx8");

// Youtube player style (DEFAULT as default)
    intent.putExtra(YouTubePlayerActivity.EXTRA_PLAYER_STYLE, YouTubePlayer.PlayerStyle.DEFAULT);

// Screen Orientation Setting (AUTO for default)
// AUTO, AUTO_START_WITH_LANDSCAPE, ONLY_LANDSCAPE, ONLY_PORTRAIT
    intent.putExtra(YouTubePlayerActivity.EXTRA_ORIENTATION, Orientation.AUTO);

// Show audio interface when user adjust volume (true for default)
    intent.putExtra(YouTubePlayerActivity.EXTRA_SHOW_AUDIO_UI, true);

// If the video is not playable, use Youtube app or Internet Browser to play it
// (true for default)
    intent.putExtra(YouTubePlayerActivity.EXTRA_HANDLE_ERROR, true);

// Animation when closing youtubeplayeractivity (none for default)
    intent.putExtra(YouTubePlayerActivity.EXTRA_ANIM_ENTER, R.anim.fade_in);
    intent.putExtra(YouTubePlayerActivity.EXTRA_ANIM_EXIT, R.anim.fade_out);

    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);
  }

  private void updateItemSelection(ImageView activeImage, FontTextView activeTittle, int icon, String toolBarTittle) {
    resetViewsItems();
    activeImage.setImageDrawable(ResourcesCompat.getDrawable(getResources(), icon, null));
    activeTittle.setTextColor(Color.parseColor("#30d1d5"));
    mTittleFragment.setText(toolBarTittle);
    guillotineAnimation.close();
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
    electronicsTittle = (FontTextView) view.findViewById(R.id.elec_tittle);
    furnitureTittle = (FontTextView) view.findViewById(R.id.furniture_tittle);
    homeTittle = (FontTextView) view.findViewById(R.id.home_tittle);
  }

  @Override
  public void showRibotsEmpty() {
    Toast.makeText(this, R.string.empty_ribots, Toast.LENGTH_LONG).show();
  }


  private void setAllContent() {
    removeFragmentsFromPager();
    for (int i = 0; i < listAllContent.size(); i++) {
      News news = listAllContent.get(i);
      fragments.add(ContentFragment.newInstance(news, i));
    }
    feedsAdapter.notifyDataSetChanged();
  }

  private void setJobs() {
    removeFragmentsFromPager();
    for (int i = 0; i < listAllContent.size(); i++) {
      News news = listAllContent.get(i);
      if (news.getContent_type().equals("2")) {
        fragments.add(ContentFragment.newInstance(news, i));
      }
    }
    feedsAdapter.notifyDataSetChanged();
  }

  private void setSelectedCategory(String categoryType) {
    removeFragmentsFromPager();
    for (int i = 0; i < listAllContent.size(); i++) {
      News news = listAllContent.get(i);

      if (news.getCategory_id() != null && news.getCategory_id().equals(categoryType)) {
        fragments.add(ContentFragment.newInstance(news, i));
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
      News news = listAllContent.get(i);
      if (news.getContent_type().equals("3")) {
        fragments.add(ContentFragment.newInstance(news, i));
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
