package in.naushad.lenden;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Naushad on 22/01/2017.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addSlide(AppIntroFragment.newInstance("Welcome to Len-Den","Ledger made Simple and Easy",R.drawable.logo_full_res, ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary)));
        if(Build.VERSION.SDK_INT >=  23) {
            addSlide(AppIntroFragment.newInstance("This app won't work without Storage Permission", "To store the app data onto your internal storage\n \n Enable it in next screen", R.drawable.ic_add_white_24dp, ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
        }addSlide(AppIntroFragment.newInstance("You are all set.\nEnjoy Len-Den","GET STARTED",R.drawable.done_full_res, ContextCompat.getColor(getApplicationContext(),R.color.colorPrimary)));

        if(Build.VERSION.SDK_INT >=  23) {
            askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
        showSkipButton(false);
        setVibrate(true);
        setGoBackLock(true);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        startActivity(new Intent(IntroActivity.this,MainActivity.class));
        finish();
    }
}
