package ttqrpos.com.ttposmobile;

import android.app.Application;
import android.content.ContextWrapper;

import com.activeandroid.ActiveAndroid;
import com.pixplicity.easyprefs.library.Prefs;

public class PrefsApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        ActiveAndroid.initialize(this);
    }
}