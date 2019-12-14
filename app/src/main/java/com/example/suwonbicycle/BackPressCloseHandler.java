package com.example.suwonbicycle;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {

    private long backKeyPressedTime = 0; // 뒤로가기 버튼을 눌렀을 때 현재 시간을 저장하는 변수
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed() {
        // 현재 시간 > back키를 눌렀을 때 시간 + 2초일때
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            // back키를 누른 시간이 현재시간이 된다.
            backKeyPressedTime = System.currentTimeMillis();
            // 뒤로가기 버튼을 한번 더 누르면 종료된다는 토스트를 띄운다.
            showGuide();
            return;
        }
        // 현재 시간 <= back키를 눌렀을 때 시간 + 2초일때
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            // 액티비티 종료
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}



