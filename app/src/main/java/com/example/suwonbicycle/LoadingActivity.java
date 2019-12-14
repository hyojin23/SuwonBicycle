package com.example.suwonbicycle;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;

import com.airbnb.lottie.LottieAnimationView;

public class LoadingActivity  extends AppCompatActivity {
    private static final String TAG = " LoadingActivity";
    private static int progress;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"처음 로딩화면 실행");
        setContentView(R.layout.activity_bike_loading);
        startLoading();
        setupLottie();

        // 로딩 프로그레스바 스레드
//        progress = 0;
//        progressBar = (ProgressBar) findViewById(R.id.progressbar);
//        progressBar.setMax(200);
//
//        new Thread(new Runnable() {
//            public void run() {
//                while (progressStatus < 200) {
//                    progressStatus = doSomeWork();
//                    handler.post(new Runnable() {
//                        public void run() {
//                            progressBar.setProgress(progressStatus);
//                        }
//                    });
//                }
//                handler.post(new Runnable() {
//                    public void run() {
//                        // ---0 - VISIBLE; 4 - INVISIBLE; 8 - GONE---
//                        progressBar.setVisibility(View.GONE);
//                        finish();
//                    }
//                });
//            }
//
//            private int doSomeWork() {
//                try {
//                    // ---simulate doing some work---
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return ++progress;
//            }
//        }).start();
    }

    public void setupLottie() {
        LottieAnimationView lottie = findViewById(R.id.loading_bike_lottie);
        lottie.setAnimation("bike_animation.json");
        lottie.loop(true);   // or
        lottie.setRepeatCount(2);
        lottie.playAnimation();
    }
    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);

}
    }
