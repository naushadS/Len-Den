package in.naushad.lenden;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    Thread timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Thread intro = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);
                if (isFirstStart) {
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                    getPrefs.edit().putBoolean("firstStart", false).apply();
                    finish();
                }else{
                    timer.start();
                }
            }
        });
        intro.start();
        timer=new Thread(){
            public void run(){
                try{
                    sleep(750);
                } catch(InterruptedException e){
                    e.printStackTrace();
                } finally{
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        };
    }
}
