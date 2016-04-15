package com.ghn.android.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ghn.android.BoilerplateApplication;


public class FontTextView extends TextView {
  public FontTextView(Context context) {
    this(context, null);
  }

  public FontTextView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    setTypeface(BoilerplateApplication.canaroExtraBold);
  }
}
