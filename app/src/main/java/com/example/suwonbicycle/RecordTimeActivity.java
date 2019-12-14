package com.example.suwonbicycle;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class RecordTimeActivity extends AppCompatActivity {

    public static final String TAG = "RecordTimeActivity";
//public static Handler handler;

    // 로티
    private  static LottieAnimationView lottie;

    StopWatchService myService; // 서비스 클래스의 객체
    boolean isService = false; // 서비스 실행 확인

    // 리스트, 어댑터, 리사이클러뷰 객체 생성
    private ArrayList<RecordTimeDictionary> mArrayList;
    private RecordTimeAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecordTimeDictionary dict;
    private Context mcontext;
    Toolbar toolbar;


    // 스탑워치 기능구현을 위한 버튼, 스레드 객체
    private Button mStartBtn, mCleanBtn, mPauseBtn;
    private static TextView mTimeTextView;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    // private Boolean isStartClicked= false;

    ServiceConnection conn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            // 서비스와 연결되었을 때 호출되는 메서드
            Log.d(TAG, "conn 서비스와 연결");
            StopWatchService.myBinder binder =
                    (StopWatchService.myBinder) service;
            myService = binder.getService();
            isService = true; // 실행 여부를 판단
        }

        public void onServiceDisconnected(ComponentName name) {
            // 서비스와 연결이 끊기거나 종료되었을 때
            Log.d(TAG, "conn 서비스와 연결 해제");
            isService = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_time); // 레이아웃 화면 표시


        // 툴바 기능
        toolbar = (Toolbar) findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);//액션바와 같게 만들어줌
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표를 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바의 기본 제목을 보여주지 않음


        // 레이아웃의 리사이클러뷰 id와 리사이클러뷰 객체를 연결
        mRecyclerView = (RecyclerView) findViewById(R.id.record_recyclerview);
        // 리사이클러뷰에 레이아웃 매니저 생성
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //  RecordTimeActivity에서 RecyclerView의 데이터에 접근시 사용
        mArrayList = new ArrayList<>();
        // gson활용시 필요한 메소드
        // loadData();
        loadDataFromJson(); // JSON으로 저장된 데이터를 불러옴

        // RecyclerView를 위해 CustomAdapter를 사용
        mAdapter = new RecordTimeAdapter(this, mArrayList);
        // 리사이클러뷰 객체와 어댑터 연결
        mRecyclerView.setAdapter(mAdapter);
        // JSON으로 저장된 데이터가 mArrayList에 추가되었으므로 어댑터에서 갱신하여 리사이클러뷰에 보여줌
        mAdapter.notifyDataSetChanged();

        // 메인화면에서 주행기록을 받아와서 리사이클러뷰 리스트에 저장
//        Intent intent = getIntent();
//        String record = intent.getStringExtra("record");
//        //RecordTimeDictionary의 객체 생성 후 매개변수 대입
//        if (savedInstanceState == null) {
//            dict = new RecordTimeDictionary("주행시간기록12", record);
//            mArrayList.add(0, dict);
//            Log.d("조건문", "통과");
//        } else { dict = savedInstanceState.getParcelable("save");
//            mArrayList.add(0, dict); //첫 줄에 삽입
//            Log.d("조건문", "null값 아님");
//        }

        //         상태표시줄 색상 변경
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().setStatusBarColor(Color.parseColor("#4ea1d3"));
//        }

        // 뷰 객체 연결 및 상태표시
        mStartBtn = (Button) findViewById(R.id.start_btn);
        mCleanBtn = (Button) findViewById(R.id.clean_btn);
        mPauseBtn = (Button) findViewById(R.id.pause_btn);
        mTimeTextView = (TextView) findViewById(R.id.text_view_total_time_number);
        mCleanBtn.setVisibility(View.INVISIBLE);
        mPauseBtn.setVisibility(View.INVISIBLE);

        // 전에 스타트 버튼을 눌렀으면 계속/초기화 버튼이 보이게, 안눌렀으면 시작 버튼이 보이게 함
//        Log.d(TAG,"검사");
//         StopWatchService.isStartClicked = loadBoolData();
//        if (StopWatchService.isStartClicked) {
//            Log.d(TAG,"isStartClicked 값이 true");
//            mStartBtn.setVisibility(View.GONE);
//            mCleanBtn.setVisibility(View.VISIBLE);
//            mPauseBtn.setVisibility(View.VISIBLE);
//            mPauseBtn.setText("계속");
//            mPauseBtn.setTextColor(Color.parseColor("#43A047"));
//            mPauseBtn.setTypeface(null, Typeface.BOLD);
//            mCleanBtn.setText("초기화");
//            mCleanBtn.setTextColor(Color.parseColor("#03A9F4"));
//            mCleanBtn.setTypeface(null, Typeface.BOLD);
//        } else {
//            Log.d(TAG,"isStartClicked 값이 false");
//            mStartBtn.setVisibility(View.VISIBLE);
//            mCleanBtn.setVisibility(View.GONE);
//            mPauseBtn.setVisibility(View.GONE);
//        }


//        if (!StopWatchService.isRunning) {
//
//        }



        // 일시정지를 누르고 뒤로가기를 눌러 액티비티를 종료했다가 다시 액티비티를 실행하는 경우
        if (!StopWatchService.isRunning) {
            Log.d(TAG, "스탑워치를 멈추고 뒤로갔다가 돌아오는 경우");
            Log.d(TAG, "StopWatchService.isRunning 은 false");
            mStartBtn.setVisibility(View.GONE);
            mCleanBtn.setVisibility(View.VISIBLE);
            mPauseBtn.setVisibility(View.VISIBLE);
            mPauseBtn.setText("계속");
            mPauseBtn.setTextColor(Color.parseColor("#43A047"));
            mPauseBtn.setTypeface(null, Typeface.BOLD);
            mCleanBtn.setText("초기화");
            mCleanBtn.setTextColor(Color.parseColor("#03A9F4"));
            mCleanBtn.setTypeface(null, Typeface.BOLD);
            loadTimeData();
        }
        // 초기화 버튼을 누르고 뒤로가기를 눌러 액티비티를 다시 실행하는 경우
        if (!StopWatchService.isRunning && StopWatchService.isInitilized ) {
            Log.d(TAG, "초기화 버튼을 누른 뒤 뒤로가기 눌렀다가 돌아오는 경우");
            mStartBtn.setVisibility(View.VISIBLE);
            mCleanBtn.setVisibility(View.GONE);
            mPauseBtn.setVisibility(View.GONE);
        }
        // 처음에 시작버튼을 눌러 스탑워치 동작시킨뒤 뒤로가기를 눌러 액티비티를 종료했다가 다시 액티비티를 실행하는 경우
        if (StopWatchService.isStartClicked && StopWatchService.isRunning) {
            Log.d(TAG, "스탑워치가 동작중에 뒤로갔다가 돌아오는 경우");
            Log.d(TAG, "StopWatchService.isRunning 은 true");
            Log.d(TAG, "StopWatchService.isStartClicked 은 true");
            mStartBtn.setVisibility(View.GONE);
            mCleanBtn.setVisibility(View.VISIBLE);
            mPauseBtn.setVisibility(View.VISIBLE);
            mPauseBtn.setText("일시정지");
            mPauseBtn.setTextColor(Color.parseColor("#F44336"));
            mPauseBtn.setTypeface(null, Typeface.BOLD);
            mCleanBtn.setText("기록");
            mCleanBtn.setTypeface(null, Typeface.BOLD);
            mCleanBtn.setTextColor(Color.parseColor("#424242"));

        }



//TODO : 시작버튼
        // 시작 버튼 클릭시
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // StopWatchService.isStartClicked = !StopWatchService.isStartClicked;
                // saveBoolData();
                //if (StopWatchService.isStartClicked) {
                Log.d(TAG, "시작버튼 누름");
                StopWatchService.isRunning = true;
                Log.d(TAG, "StopWatchService.isRunning = true");
//                // 스탑워치 서비스 실행
//                Intent intent = new Intent(RecordTimeActivity.this, StopWatchService.class);
//                startService(intent);

                v.setVisibility(View.GONE);
                mCleanBtn.setVisibility(View.VISIBLE);
                mPauseBtn.setVisibility(View.VISIBLE);
                Intent intent = new Intent(
                        getApplicationContext(),// Activiey Context
                        StopWatchService.class); // 이동할 서비스 객체
                bindService(intent, conn, Context.BIND_AUTO_CREATE); // 서비스 실행

                // String data= ms.getBroastData();//서비스에서 가져온 데이터
                // String data=  ms.result;

                //ms = new StopWatchService();
                // String data= ms.result;
                //mTimeTextView.setText(data);
                //timeThread = new Thread(new timeThread(handler));
                //timeThread thread = new timeThread(handler);

                //thread.run();
                //AThread thread = new AThread(handler);
                //thread.setDaemon(true);
                //thread.start();


                mPauseBtn.setText("일시정지");
                mPauseBtn.setTextColor(Color.parseColor("#F44336"));
                mPauseBtn.setTypeface(null, Typeface.BOLD);
                mCleanBtn.setText("기록");
                mCleanBtn.setTypeface(null, Typeface.BOLD);
                mCleanBtn.setTextColor(Color.parseColor("#424242"));
                StopWatchService.isStartClicked = true;
                //if (StopWatchService.isStartClicked) 가 끝나는 부분 }

            }
        });
        // 초기화/기록 버튼 클릭시
        mCleanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // isRunning==true인 경우, 즉 스탑워치가 작동중일때 (버튼이 기록으로 적혀있을때)
                if (StopWatchService.isRunning) {
                    Log.d(TAG, "기록버튼 누름");
                    Toast.makeText(getApplicationContext(), "주행시간이 기록됩니다. 길게 누르면 수정, 삭제가 가능합니다.", Toast.LENGTH_LONG).show();
                    //int count = 0;
                    //count++;
                    StopWatchService.isRecord = true;
                    Log.d(TAG, "StopWatchService.isRecord = true");
                    String title = "주행시간"; //+count;
                    String time = mTimeTextView.getText().toString();

                    // Hashset을 이용하여 하나의 key에 여러 value를 저장하는 방법
//                    SharedPreferences pref = getSharedPreferences("key", MODE_PRIVATE);
//                    SharedPreferences.Editor edit = pref.edit();
//                    HashSet<String> hash = new HashSet<String>();
//                    hash.add(mTimeTextView.getText().toString());
//                    edit.putString("title", "주행시간");
//                    edit.putStringSet("timeSet", hash);
//                    edit.apply();
//                    pref.getStringSet("timeSet", null);
//                    pref.getString("title", "default");
//                    String title = pref.getString("title", "default");
//                    Set<String> time = pref.getStringSet("timeSet", null);

                    //loadData();
                    // RecordDictionary 생성자를 사용하여 ArrayList에 삽입할 데이터를 만듬

                    dict = new RecordTimeDictionary(title, time);

                    // 데이터를  mArrayList 첫번째줄에 삽입
                    mArrayList.add(0, dict);

                    mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영

                    // 인텐트로  RecordTimeActivity 실행후 시간 데이터 전달
//                    Intent intent = new Intent( MainActivity.this, RecordTimeActivity.class);
//                    intent.putExtra("record", mTimeTextView.getText().toString());
//                    startActivityForResult(intent, 30);
                }
                // isRunning==false일 경우, 즉 스탑워치가 멈춰있을때 (버튼이 초기화로 적혀있을때)
                if (!StopWatchService.isRunning) {
                    Log.d(TAG, "초기화버튼 누름");
                    // 스탑워치 서비스 종료
//                Intent intent = new Intent(RecordTimeActivity.this, StopWatchService.class);
//                stopService(intent);
                    v.setVisibility(View.GONE);
                    mStartBtn.setVisibility(View.VISIBLE);
                    mPauseBtn.setVisibility(View.GONE);
                    if (isService) {
                        Log.d(TAG, "서비스가 실행중이라면 unbindService(conn)");
                        unbindService(conn);
                    }
                    //stopService(getIntent());
                   // isRunning = true;
                   // mArrayList.clear();
                   // mAdapter.notifyDataSetChanged();
                    StopWatchService.timeThread.interrupt();
                    Log.d(TAG, "StopWatchService.timeThread.interrupt()");
                    StopWatchService.isRunning = false; // 스레드가 동작함을 나타냄
                    Log.d(TAG, "StopWatchService.isRunning = false");
                    StopWatchService.isInitilized = true; // 초기화 버튼이 눌렀음을 나타냄
                    Log.d(TAG, " StopWatchService.isInitilized = true");
                    mTimeTextView.setText("00:00:00:00");
                    // 현재 이 핸들러가 바인딩 된 쓰레드의 메세지 큐에 있는  메세지를 삭제(스레드가 종료될 때 시간이 시간 텍스트뷰에 이전 시간이 남아있지 않게 함)
                    handler.removeMessages(0);
                }
            }
        });

        // 일시정지/계속 버튼 클릭시
        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "일시정지/계속 버튼 누름");
//                 if(StopWatchService.isRunning) {
//                     unbindService(conn);
//                 } else {
//                     Intent intent = new Intent(
//                             getApplicationContext(),// Activiey Context
//                             StopWatchService.class); // 이동할 서비스 객체
//                     bindService(intent, conn, Context.BIND_AUTO_CREATE); // 서비스 실행
//                 }

                //isRunning의 값을 true에서 false로 바꿔서 스레드의 동작을 일시정지 시킨다.
                // isRunning = !isRunning;

                // StopWatchService 클래스의 isRunning의 값을 true에서 false로 바꿔서 스레드의 동작을 일시정지 시킨다.
                StopWatchService.isRunning = !StopWatchService.isRunning;

                // isRunning이 true일때 즉 스탑워치가 작동중일때
                if (StopWatchService.isRunning) {
                    Log.d(TAG, "StopWatchService.isRunning이 true");
                    mPauseBtn.setText("일시정지");
                    mPauseBtn.setTextColor(Color.parseColor("#F44336"));
                    mPauseBtn.setTypeface(null, Typeface.BOLD);
                    mCleanBtn.setText("기록");
                    mCleanBtn.setTypeface(null, Typeface.BOLD);
                    mCleanBtn.setTextColor(Color.parseColor("#424242"));

                    // isRunning이 false일때 즉 스탑워치가 작동중이 아닐때
                } else {
                    Log.d(TAG, "StopWatchService.isRunning이 false");
                    StopWatchService.isInitilized = false;
                    mPauseBtn.setText("계속");
                    mPauseBtn.setTextColor(Color.parseColor("#43A047"));
                    mPauseBtn.setTypeface(null, Typeface.BOLD);
                    mCleanBtn.setText("초기화");
                    mCleanBtn.setTextColor(Color.parseColor("#03A9F4"));
                    mCleanBtn.setTypeface(null, Typeface.BOLD);
                }
            }
        });
        // mArrayList.add(dict); //마지막 줄에 삽입
        //mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영


//
//        // 리사이클러뷰 아이템을 클릭할 경우
//        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                 RecordTimeDictionary dict = mArrayList.get(position);
//                // 클릭시 토스트 생성
//                // Toast.makeText(getApplicationContext(), dict.getTime()+' '+dict.getTitle(), Toast.LENGTH_LONG).show();
//
//                // 제목, 시간, 내용 데이터를 ReadPostActivity에 전달하고 ReadPostActivity를 실행시킴, ReadPostActivity에서 제목과 내용을 볼 수 있게 함
//                Intent intent = new Intent(getBaseContext(), ReadPostActivity.class);
//
//                intent.putExtra("time", dict.getTime());
//                intent.putExtra( "title", dict.getTitle());
//                intent.putExtra( "content", dict.getContent());
//                startActivity(intent);
//
//            }
//
//            // 롱클릭시 메소드
//            @Override
//            public void onLongClick(View view, int position) {
//            }
//        }));


//
//        // 글쓰기 버튼 클릭시
//        Button buttonInsert = (Button) findViewById(R.id.button_main_insert);
//        buttonInsert.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //  WriteActivity를 실행
//                Intent intent = new Intent(CommunityActivity.this, WriteActivity.class);
//                startActivityForResult(intent, 10);
//
//
//
//
////                AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity.this);
////                View view = LayoutInflater.from(CommunityActivity.this)
////                        .inflate(R.layout.activity_write, null, false);
////                builder.setView(view);
////                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
////                final EditText editTextID = (EditText) view.findViewById(R.id.edittext_dialog_id);
////                final EditText editTextEnglish = (EditText) view.findViewById(R.id.edittext_dialog_endlish);
////                final EditText editTextKorean = (EditText) view.findViewById(R.id.edittext_dialog_korean);
////
////                ButtonSubmit.setText("삽입");
////
////
////                final AlertDialog dialog = builder.create();
////                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
////                    public void onClick(View v) {
////                        String strtitle = editTextID.getText().toString();
////
////
////                       CommunityDictionary dict = new CommunityDictionary(strtitle);
//
////                        mArrayList.add(0, dict); //첫 줄에 삽입
////                        //mArrayList.add(dict); //마지막 줄에 삽입
////                        mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영
//
//                //dialog.dismiss();
//            }
//        });

        // dialog.show();


        //mArrayList.add(0, dict); //첫 줄에 삽입
        //mArrayList.add(dict); //마지막 줄에 삽입
        // mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영

    } // onCreate 끝나는 부분

//TODO 툴바에 메뉴 버튼 추가
    //추가된 소스, ToolBar에 menu.xml을 인플레이트함
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //추가된 소스, ToolBar에 추가된 항목의 select 이벤트를 처리하는 함수(툴바 오른쪽 ... 표시메뉴)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
                case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
                    finish(); // 액티비티 종료
                    return true;
                }
            //case R.id.bike_image:
                // User chose the "Settings" item, show the app settings UI...
                //Toast.makeText(getApplicationContext(), "환경설정 버튼 클릭됨", Toast.LENGTH_LONG).show();
                //return true;

            case R.id.menu_search: // 메뉴 오른쪽 버튼 클릭 후 주행기록 삭제 눌렀을 때
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                mArrayList.clear();   // 리스트의 기록 삭제
                mAdapter.notifyDataSetChanged();  // 어댑터에 연결된 리사이클러뷰를 갱신하여 변경된 내용을 반영(기록이 삭제된 것을 보여줌)
            default:
                return super.onOptionsItemSelected(item);

        }
    }





    //    private static final String SETTINGS_PLAYER_JSON = "settings_item_json";
//    @Override
//    protected void onStop() {
//        super.onStop();
//        setStringArrayPref(context, SETTINGS_PLAYER_JSON, mArrayList);
//        ArrayList<String> mmArrayList;
//        mmArrayList = getStringArrayPref(mcontext, SETTINGS_PLAYER_JSON);
//        if (mmArrayList != null) {
//            for (String value : mmArrayList) {
//                Log.d("리스트", "Get json : " + value);
//            }
//        }

//        Set<String> set = new HashSet<String>();
//        HashSet<String> hash = new HashSet<String>();
//        Set<String> st1 = new TreeSet<String>();
//        String a = "ㅇㅇ";
//        st1.add("aa");
//        st1.add("ss");
//        st1.add("dd");
//        set.addAll(st1);
//
//    }
//TODO 핸들러
    // 스탑워치 핸들러
    @SuppressLint("HandlerLeak")
    static Handler handler = new Handler() {
        // @Override
        public void handleMessage(Message msg) {
            int mSec = msg.arg1 % 100;
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 / 100) / 60;
            int hour = (msg.arg1 / 100) / 3600;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
            Log.d(TAG, "핸들러가 동작하고 있습니다.");
            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);
            result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);


            mTimeTextView.setText(result);
        }


    };


    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop에서 데이터 저장");
        // gson 활용시 필요한 메소드
        //saveData();
        // json으로 데이터 저장
        saveDataToJson();
        saveTimeData();
//        if(!StopWatchService.isRunning) {
//            outState.putString("timeData", mTimeTextView.getText().toString());
//        }


    }

    @Override
    public void onDestroy(){
        super.onDestroy();
//        unbindService(conn);
    }

    // 스탑워치 일시정지를 누르고 뒤로 갈 경우 액티비티 마지막에 표시된 시간을 저장
    private void saveTimeData() {
        Log.d(TAG, "스탑워치에서 일시정지된 시간을 저장");
        // SharedPreferences 객체 생성
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        // Editor 객체 생성
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("timeData", mTimeTextView.getText().toString());
        editor.apply();
    }

    // 스탑워치 일시정지를 누르고 뒤로 갈 경우 액티비티 마지막에 표시된 시간을 불러옴
    private void loadTimeData() {
        Log.d(TAG, "스탑워치에서 일시정지된 시간을 불러옴");
        // SharedPreferences 객체 생성
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        // 스탑워치 일시정지를 누르고 뒤로 갈 경우 액티비티 마지막에 표시된 시간을 불러옴
        String time = sharedPreferences.getString("timeData","");
        // 텍스트뷰에 불러온 시간 입력
        mTimeTextView.setText(time);
    }

    // 스타트 버튼이 눌렸는지 체크하는 Boolean값을 저장
    private void saveBoolData() {
        Log.d(TAG, "Boolean값을 저장");
        // SharedPreferences 객체 생성
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        // Editor 객체 생성
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // 스타트 버튼이 눌렸는지 체크하는 Boolean 값을 저장 (버튼이 눌리면 isStartClicked == true)
        Log.d(TAG, "스타트 버튼이 눌려 isStartClicked이 true가 됨을 저장");
        editor.putBoolean("start", StopWatchService.isStartClicked);
        editor.apply();
    }

    // 스타트 버튼이 눌렸는지 체크하는 Boolean값을 불러옴
    private Boolean loadBoolData() {
        Log.d(TAG, "저장된 Boolean값을 불러옴");
        // SharedPreferences 객체 생성
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        // 스타트 버튼이 눌렸는지 체크하는 Boolean값을 불러옴
        Boolean isStartClicked = sharedPreferences.getBoolean("start", false);
        return  isStartClicked;
    }



    // json으로 데이터 저장(스탑워치의 AraayList를 json형식으로 저장)
    private void saveDataToJson() {
        // SharedPreferences 객체 생성
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        // Editor 객체 생성
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // JSONObject 객체 생성
        JSONObject obj = new JSONObject();
        try
        {
            JSONArray jArray = new JSONArray();//배열이 필요할때
            for (int i = 0; i < mArrayList.size(); i++)//배열
            {
                JSONObject object = new JSONObject();//배열 내에 들어갈 json
                object.put("title", mArrayList.get(i).getTitle());  // mArrayList에 기록된 제목("주행시간")을 불러와서 object에 저장
                object.put("time", mArrayList.get(i).getTime());  // mArrayList에 기록된 시간("00:00:00:00")을 불러와서 object에 저장
                jArray.put(object); // jArray에 object 저장
            }
            obj.put("item", jArray);//오브젝트에 배열을 넣음
            String some = obj.toString(); // 오브젝트를 String 형식으로 바꿈
            // editor에 some 넣고 적용
            editor.putString("data", some);
            editor.apply();

        } catch(JSONException e) {
            e.printStackTrace();
        }


    }
    // json으로 데이터 불러오기
    private void loadDataFromJson() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("data", null);

        if (json != null) {
            Log.d(TAG, "제이손 not null");
            try {
                // object 객체 생성
                JSONObject object = new JSONObject(json);
                // 키값 "item"을 넣어 object에서JSONArray를 꺼내온다.
                JSONArray jsonArray = object.getJSONArray("item");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d(TAG, "제이손 반복문");
                    // JSONArray에서 각 인덱스의 JSONObject를 불러와 jsonObject에 대입
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // 각 object의 String 변수 지정
                    String title = jsonObject.getString("title");
                    String time = jsonObject.getString("time");
                    // dict 객체생성시 생성자에 대입
                    RecordTimeDictionary dict = new RecordTimeDictionary(title, time);
                    // mArrayList 마지막줄에 추가
                    mArrayList.add(dict);

                }
            } catch (JSONException e) {
                Log.d("제이손", "캐치");
                e.printStackTrace();
            }

//        } else {
//            Log.d("제이손", "null");
//            String title = "주행시간"; //+count;
//            String time = mTimeTextView.getText().toString();
//            RecordTimeDictionary dict = new RecordTimeDictionary(title, time);
//            mArrayList.add(0, dict);

        }
    }

    // gson활용시 필요한 메소드

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mArrayList);
        editor.putString("time list", json);
        editor.apply();
    }
    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("time list", null);
        Type type = new TypeToken<ArrayList<RecordTimeDictionary>>() {}.getType();
        mArrayList = gson.fromJson(json, type);

        if(mArrayList == null) {
            Log.d("어레이리스트", "널값");
            mArrayList = new ArrayList<>();

        }
    }

    //  ArrayList를 Json으로 변환하여 SharedPreferences에 String을 저장
    private void setStringArrayPref(Context context, String key, ArrayList<RecordTimeDictionary> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            jsonArray.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, jsonArray.toString());
        } else {
            editor.putString(key, null);
        }
        editor.apply();
    }
    //  SharedPreferences에서 Json형식의 String을 가져와서 다시 ArrayList로 변환
    private ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<>();
        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String url = jsonArray.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

//    public void dataSave() {
//        SharedPreferences pref = getSharedPreferences("key", MODE_PRIVATE);
//        SharedPreferences.Editor edit = pref.edit();
//        edit.putString("key", mArrayList);
//        edit.apply();
//
//    }

    // 툴바의 뒤로가기 버튼이 동작하도록 하는 메소드
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case android.R.id.home:{ //toolbar의 back키 눌렀을 때 동작
//                Intent intent = new Intent(RecordTimeActivity.this, MainActivity.class);
//                // FLAG_ACTIVITY_REORDER_TO_FRONT 는 호출하려는 액티비티가 Task에 존재하면 그 액티비티를 재생성하지 않고 앞으로 불러온다.
//                startActivity(intent.addFlags((FLAG_ACTIVITY_REORDER_TO_FRONT)));
//                return true;
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }

//    // 뒤로가기 버튼을 눌렀을 때
//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//
//        // 뒤로가기 눌렀을때 서비스 연결을 해제한다.
//        if (!StopWatchService.isRunning) {
//            unbindService(conn);
//        }
//
////       //moveTaskToBack(true);
////        Intent intent = new Intent(RecordTimeActivity.this, MainActivity.class);
////        // FLAG_ACTIVITY_REORDER_TO_FRONT 는 호출하려는 액티비티가 Task에 존재하면 그 액티비티를 재생성하지 않고 앞으로 불러온다.
////        startActivity(intent.addFlags((FLAG_ACTIVITY_REORDER_TO_FRONT)));
//
//    }

//    // 스탑워치 핸들러
//    @SuppressLint("HandlerLeak")
//    Handler handler = new Handler() {
//        @Override
//        public void handleMessage (Message msg){
//            int mSec = msg.arg1 % 100;
//            int sec = (msg.arg1 / 100) % 60;
//            int min = (msg.arg1 / 100) / 60;
//            int hour = (msg.arg1 / 100) / 360;
//            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간
//
//            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);
//
//            mTimeTextView.setText(result);
//        }
//    };

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
//
//            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);
//
//            mTimeTextView.setText(result);
//        }
//    };

//    // 스탑워치 스레드
//    public class timeThread implements Runnable {
//        @Override
//        public void run() {
//            int i = 0;
//
//            while (true) {
//                while (isRunning) { //일시정지를 누르면 멈춤
//                    Message msg = new Message();
//                    msg.arg1 = i++;
//                    handler.sendMessage(msg);
//                    Log.d("스레드", "동작");
//                    try {
//                        Thread.sleep(10);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                        runOnUiThread(new Runnable(){
//                            @Override
//                            public void run() {
//                                Log.d("스레드", "종료");
//                                mTimeTextView.setText("");
//                                mTimeTextView.setText("00:00:00:00");
//                            }
//                        });
//                        return; // 인터럽트 받을 경우 return
//                    }
//                }
//            }
//        }
//    }

    // dict 객체를 저장
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d("파셀러블", "저장");
        super.onSaveInstanceState(outState);
        outState.putParcelable("save", dict);

    }


//    @Override
//    protected void onActivityResult (int requestCode, int resultCode, Intent intent) {
//        //  WriteActivity에서 등록 버튼을 눌러 CommunityActivity로 돌아올 때. WriteActivity에서 입력한 데이터를 전달받음
//        if (requestCode == 10 && resultCode == RESULT_OK) {
//            String title = intent.getStringExtra("title");
//            String content = intent.getStringExtra("content");
//            String time = intent.getStringExtra("time");
//            //Toast toast = Toast.makeText(this, key, Toast.LENGTH_SHORT);
//            //toast.show();
//
//            //CommunityDictionary의 객체 생성 후 매개변수 대입
//            CommunityDictionary dict = new CommunityDictionary(title, time, content);
//            mArrayList.add(0, dict); //첫 줄에 삽입
//            //mArrayList.add(dict); //마지막 줄에 삽입
//            mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영
//
//            // 수정 버튼을 눌러 CommunityActivity로 돌아올 때. EditActivity에서 입력한 내용을 전달받음
//        } else if (requestCode == 20 && resultCode == RESULT_OK) {
//
//            String title = intent.getStringExtra("title");
//            String content = intent.getStringExtra("content");
//            String time = intent.getStringExtra("time");
//            int position = intent.getIntExtra("position", 0);
//            //Toast toast = Toast.makeText(this, key, Toast.LENGTH_SHORT);
//            //toast.show();
//
//            //CommunityDictionary의 객체 생성 후 매개변수 대입
//            CommunityDictionary dict = new CommunityDictionary(title, time, content);
//            mArrayList.set(position, dict); //첫 줄에 삽입
//            //mArrayList.add(dict); //마지막 줄에 삽입
//            mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영
//        }
//
//
//    }


    // 클릭 리스너 구현
    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private RecordTimeActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final  RecordTimeActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }



        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }
}

//// 스탑워치 스레드
//class AThread extends Thread {
//    Handler handler;
//    Boolean isRunning = true;
//
//    AThread(Handler handler){
//        this.handler = handler;
//    } // end constructor
//
//    @Override
//    public void run() {
//        int i = 0;
//
//        while (true) {
//
//            Log.d("test", "서비스의 onCreate");
//            while (isRunning) { //일시정지를 누르면 멈춤
//                Message msg = new Message();
//                msg.arg1 = i++;
//                handler.sendMessage(msg);
//                Log.d("", "AThread 동작");
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
////                        runOnUiThread(new Runnable(){
////                            @Override
////                            public void run() {
////                                Log.d("스레드", "종료");
////                                mTimeTextView.setText("");
////                                mTimeTextView.setText("00:00:00:00");
////                            }
////                        });
//                    return; // 인터럽트 받을 경우 return
//                }
//            }
//        }
//    }
//}
