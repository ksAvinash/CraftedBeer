package it.justdevelop.craftedbeer;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;

import it.justdevelop.craftedbeer.helpers.BackendHelper;

public class SplasherActivity extends AppCompatActivity {

    Context context;
    LottieAnimationView animation_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splasher);

        context = getApplicationContext();
        animation_view = findViewById(R.id.animation_view);

        BackendHelper.fetch_beers fetch_beers = new BackendHelper.fetch_beers();
        fetch_beers.execute(getApplicationContext(), getResources().getString(R.string.web_api));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animation_view.playAnimation();
            }
        }, 2500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplasherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3500);


    }
}
