package uk.co.ribot.androidboilerplate.ui.feeds;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.thoughtw.retail.animation.GuillotineAnimation;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
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
  private LinearLayout ajab_gajab_group, desh_group, videsh_group, rashifal_group, health_tips_group, election_keeda_group;
  private ImageView ivMoreItem;

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
    }
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
