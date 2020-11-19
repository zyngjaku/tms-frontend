package io.github.zyngjaku.tmsfrontend;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash_activity);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent newIntent = new Intent(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(newIntent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
