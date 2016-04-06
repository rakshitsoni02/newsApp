package uk.co.ribot.androidboilerplate.ui.feeds;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FeedsAdapter extends FragmentStatePagerAdapter {
  private List<Fragment> fragments = new ArrayList<>();

  public FeedsAdapter(FragmentManager fm, List<Fragment> fragments) {
    super(fm);
    this.fragments = fragments;
  }

  @Override
  public Fragment getItem(int position) {
    return fragments.get(position);
  }

  @Override
  public int getCount() {
    return fragments.size();
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return ((ContentFragment) fragments.get(position)).getTitle();
  }

  @Override
  public int getItemPosition(Object item) {
    ContentFragment fragment = (ContentFragment) item;
    String title = fragment.getTitle();
    int position = fragments.indexOf(title);
    if (position >= 0) {
      return position;
    } else {
      return POSITION_NONE;
    }
  }

}

