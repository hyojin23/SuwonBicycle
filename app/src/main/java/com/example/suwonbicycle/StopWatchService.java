package com.example.suwonbicycle;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/** 스탑워치 액티비티를 서비스에서 실행되게 하는 서비스 클래스 **/

public class StopWatchService extends Service {
    private static final String TAG = "StopWatchService";

    private static Thread timeThread = null;
    public static Boolean isRunning = true; // 스탑워치의 스레드가 동작하고 있는지 확인하는 값
    IBinder mBinder = new myBinder();
    public static Boolean isStartClicked= false;  // 스탑워치의 시작 버튼이 눌렸는지 확인하는 값
    public static Boolean isInitilized= false; // 스탑워치의 초기화 버튼이 눌렸는지 확인하는 값
    public static Boolean isRecord= false; // 스탑워치의 기록 버튼이 눌렸는지 확인하는 값

    class myBinder extends Binder {

        StopWatchService getService() { // 서비스 객체를 리턴
            return StopWatchService.this;

        }
    }

    //public String getBroastData() {
        //return result;
    //}

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "서비스 onBind 에서 시작");
       // timeThread = new Thread(new timeThread());
        timeThread.start();
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "서비스 onCreate 에서 시작");
        timeThread = new Thread(new timeThread());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "서비스 onStartCommand 에서 시작");
        // timeThread = new Thread(new timeThread());
        // timeThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    // 연결된 액티비티를 벗어나거나 액티비티에서 unbindService()를 호출할 경우 onDestroy가 호출된다.
    @Override
    public void onDestroy() {
        Log.d(TAG, "서비스 종료");
        super.onDestroy();
        //timeThread.interrupt();
    }


//// 서비스 클래스를 구현하려면, Service 를 상속받는다
//    //MediaPlayer mp; // 음악 재생을 위한 객체
//    private Thread timeThread = null;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//
//        // Service 객체와 (화면단 Activity 사이에서)
//        // 통신(데이터를 주고받을) 때 사용하는 메서드
//        // 데이터를 전달할 필요가 없으면 return null;
//        return null;
//    }
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        // 서비스에서 가장 먼저 호출됨(최초에 한번만)
//        Log.d("test", "서비스의 onCreate");
//        //mp = MediaPlayer.create(this, R.raw.chacha);
//       // mp.setLooping(false); // 반복재생
//    }
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // 서비스가 호출될 때마다 실행
//        Log.d("test", "서비스의 onStartCommand");
//        timeThread = new Thread(new StopWatchService.timeThread());
//        timeThread.start();
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        // 서비스가 종료될 때 실행
//      //  mp.stop(); // 음악 종료
//        timeThread.interrupt();
//        Log.d("test", "서비스의 onDestroy");
//    }
//


//    // 스탑워치 핸들러
//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            int mSec = msg.arg1 % 100;
//            int sec = (msg.arg1 / 100) % 60;
//            int min = (msg.arg1 / 100) / 60;
//            int hour = (msg.arg1 / 100) / 360;
//            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
//            Log.d("StopWatchService", "핸들러 동작");
//            //@SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);
//            //result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);
//            result = "ddd";
//            Log.d("StopWatchService", "핸들러 동작2");
//
//            //mTimeTextView.setText(result);
//        }
//
//
//    };

    //    private void sendMessage() {
//        Log.d("messageService", "Broadcasting message");
//        Intent intent = new Intent("custom-event-name");
//        intent.putExtra("message", result);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//    }
//
    // 스탑워치 스레드
    public static class timeThread implements Runnable {
        Handler handler;

        public static void interrupt() {
           timeThread.interrupt();
        }

//        timeThread(Handler handler){
//            this.handler = handler;
//        } // end constructor

        @Override
        public void run() {
            int i = 0;

            while (true) {

                // Log.d(TAG, "isRunning==false가 되어 스레드가 일시정지 상태");

                // 일시정지를 누르면 isRunning = false가 되어 멈춤
                while (isRunning) {
                    Message msg = new Message();
                    msg.arg1 = i++;
                    // 증가하는 숫자 값을 RecordTimeActivity에 보냄
                    RecordTimeActivity.handler.sendMessage(msg);
                    Log.d("StopWatchService", "스레드 동작");
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
//                        runOnUiThread(new Runnable(){
//                            @Override
//                            public void run() {
//                                Log.d("스레드", "종료");
//                                mTimeTextView.setText("");
//                                mTimeTextView.setText("00:00:00:00");
//                            }
//                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            } // while (true) 끝나는점
        }
    }
}



