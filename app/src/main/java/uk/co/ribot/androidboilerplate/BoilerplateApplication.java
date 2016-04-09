package uk.co.ribot.androidboilerplate;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import com.thefinestartist.Base;

import timber.log.Timber;
import uk.co.ribot.androidboilerplate.data.Preference;
import uk.co.ribot.androidboilerplate.injection.component.ApplicationComponent;
import uk.co.ribot.androidboilerplate.injection.component.DaggerApplicationComponent;
import uk.co.ribot.androidboilerplate.injection.module.ApplicationModule;

public class BoilerplateApplication extends Application {
  private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
  ApplicationComponent mApplicationComponent;
  public static Typeface canaroExtraBold;
  public static Preference preference;

  @Override
  public void onCreate() {
    super.onCreate();
    Base.initialize(this);
    if (BuildConfig.DEBUG) {
      Timber.plant(new Timber.DebugTree());
//            Fabric.with(this, new Crashlytics());
    }
     initTypefaceAndPreference();

  }

  private void initTypefaceAndPreference() {
    canaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);
    preference = new Preference(getSharedPreferences(Preference.RETAIL_SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE));
  }

  public static BoilerplateApplication get(Context context) {
    return (BoilerplateApplication) context.getApplicationContext();
  }

  public ApplicationComponent getComponent() {
    if (mApplicationComponent == null) {
      mApplicationComponent = DaggerApplicationComponent.builder()
          .applicationModule(new ApplicationModule(this))
          .build();
    }
    return mApplicationComponent;
  }

  // Needed to replace the component with a test specific one
  public void setComponent(ApplicationComponent applicationComponent) {
    mApplicationComponent = applicationComponent;
  }
}
