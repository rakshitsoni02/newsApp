package com.ghn.android.ui.feeds;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ghn.android.R;
import com.ghn.android.data.model.News;
import com.ghn.android.views.FontTextView;
import com.google.android.youtube.player.YouTubePlayer;
import com.squareup.picasso.Picasso;
import com.thefinestartist.finestwebview.FinestWebView;
import com.thefinestartist.ytpa.YouTubePlayerActivity;
import com.thefinestartist.ytpa.enums.Orientation;
import com.thefinestartist.ytpa.utils.YouTubeUrlParser;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContentFragment extends Fragment {
  @Bind(R.id.tittle_content)
  TextView tittle_content;
  @Bind(R.id.video_youtube)
  FontTextView watchVideo;
  @Bind(R.id.content_desc)
  TextView content_desc;
  @Bind(R.id.image_content)
  ImageView imageNews;
  @Bind(R.id.card_view)
  CardView cardView;
  Toolbar toolBarMenu;
  @Bind(R.id.menu_option)
  ImageView fullScreen;
  @Bind(R.id.share_option)
  TextView shareIntent;
  @Bind(R.id.read_more)
  FontTextView tvReadMore;
  String youtubeUrl = "";

  public ContentFragment() {
  }

  public static Fragment newInstance(News news, int position) {
    Bundle args = new Bundle();
    args.putString("title", news.getId());
    args.putInt("position", position);
    args.putSerializable("news", news);
    com.ghn.android.ui.feeds.ContentFragment fragment = new com.ghn.android.ui.feeds.ContentFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @OnClick(R.id.video_youtube)
  public void openYoutube() {
    startYoutubeVideo();
  }

  private void startYoutubeVideo() {
    Intent intent = new Intent(getActivity(), YouTubePlayerActivity.class);
    String vidoeId = YouTubeUrlParser.getVideoId(youtubeUrl);
// Youtube video ID (Required, You can use YouTubeUrlParser to parse Video Id from url)
    intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, vidoeId);

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

  public static Bitmap getScreenShot(View view) {
    View screenView = view.getRootView();
    screenView.setDrawingCacheEnabled(true);
    Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
    screenView.setDrawingCacheEnabled(false);
    return bitmap;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.item_card_stack, container, false);
    ButterKnife.bind(this, view);
    content_desc.setMovementMethod(new ScrollingMovementMethod());
    final News news = (News) getArguments().getSerializable("news");
    tittle_content.setText(news.getTitle());
    content_desc.setText(news.getDescription());
    if (!news.getNews_image().equals("")) {
      Picasso.with(this.getActivity())
          .load(news.getNews_image())
          .placeholder(R.drawable.placeholder_normal)
          .error(R.drawable.placeholder_normal) // optional
              //   .resize(0, 250)
          .into(imageNews);
    }
    youtubeUrl = news.getYoutube_url();
    if (youtubeUrl != null && !youtubeUrl.equals("")) {
      watchVideo.setVisibility(View.VISIBLE);
    }
    toolBarMenu = (Toolbar) getActivity().findViewById(R.id.toolBarMenu);
    fullScreen.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (toolBarMenu != null)
          if (toolBarMenu.getVisibility() == View.VISIBLE) {
            toolBarMenu.animate().translationY(-toolBarMenu.getHeight()).setInterpolator(new AccelerateInterpolator(1)).start();
            toolBarMenu.setVisibility(View.GONE);
          } else {
            toolBarMenu.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            toolBarMenu.setVisibility(View.VISIBLE);
          }
      }
    });
    tvReadMore.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (news.getReadmore_url() != null && !news.getReadmore_url().equals("")) {
          new FinestWebView.Builder(getActivity().getApplicationContext()).webViewBuiltInZoomControls(true).webViewDisplayZoomControls(true).webViewSupportZoom(true).show(news.getReadmore_url());
        } else {
          new FinestWebView.Builder(getActivity().getApplicationContext()).webViewBuiltInZoomControls(true).webViewDisplayZoomControls(true).webViewSupportZoom(true).show("http://globalherald.in/");
        }
      }
    });
    shareIntent.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        shareBitmap(getScreenShot(view), news.getId());
      }
    });
    return view;
  }

  private void shareBitmap(Bitmap bitmap, String fileName) {
    try {
      File file = new File(getContext().getCacheDir(), fileName + ".png");
      FileOutputStream fOut = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
      fOut.flush();
      fOut.close();
      file.setReadable(true, false);
      final Intent intent = new Intent(Intent.ACTION_SEND);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
      intent.setType("image/png");
      startActivity(intent);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public String getTitle() {
    return getArguments().getString("title");
  }


}