package uk.co.ribot.androidboilerplate.ui.feeds;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.Bind;
import butterknife.ButterKnife;
import uk.co.ribot.androidboilerplate.R;
import uk.co.ribot.androidboilerplate.data.model.News;

public class ContentFragment extends Fragment {
  @Bind(R.id.tittle_content)
  TextView tittle_content;
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

  public ContentFragment() {
  }

  public static Fragment newInstance(News news, int position) {
    Bundle args = new Bundle();
    args.putString("title", news.getId());
    args.putInt("position", position);
    args.putSerializable("news", news);
    ContentFragment fragment = new ContentFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.item_card_stack, container, false);
    ButterKnife.bind(this, view);
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
      final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
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

  public static Bitmap getScreenShot(View view) {
    View screenView = view.getRootView();
    screenView.setDrawingCacheEnabled(true);
    Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
    screenView.setDrawingCacheEnabled(false);
    return bitmap;
  }


}