package com.inspiration.inspirationrewards.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.inspiration.inspirationrewards.R;
import com.inspiration.inspirationrewards.utils.StoredData;

public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent ;
                if (StoredData.getBoolean(SplashScreenActivity.this,"loginStaus")){
                    intent = new Intent(SplashScreenActivity.this, ProfileActivity.class);
                    intent.putExtra("defaultLogin", true);
                }else {
                    intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }).start();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // your code
            emptyMethod();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void emptyMethod() {
    }
}