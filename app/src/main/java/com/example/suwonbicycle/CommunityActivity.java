package com.example.suwonbicycle;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class CommunityActivity extends AppCompatActivity {

    private static final String TAG = "CommunityActivity";

    // 리스트, 어댑터, 리사이클러뷰 객체 생성
    private ArrayList<CommunityDictionary> mArrayList;
    private CommunityCustomAdapter mAdapter;
    private RecyclerView mRecyclerView;
    public Context context;
    private static final int PERMISSIONS_REQUEST_CODE = 200;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        // 레이아웃 화면 표시
        setContentView(R.layout.activity_community_main);
        // 외부저장소 권한 요청
        requestReadExternalStoragePermission();

        // 툴바
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        // 액션바와 같게 만들어줌
        setSupportActionBar(toolbar);
        // 뒤로가기 화살표를 만들어줌
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // 툴바의 기본 제목을 보여주지 않음
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // 레이아웃의 리사이클러뷰 id와 리사이클러뷰 객체를 연결
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_main_list);
        // 리사이클러뷰에 레이아웃 매니저 생성
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // CommunityMainActivity에서 RecyclerView의 데이터에 접근시 사용
        mArrayList = new ArrayList<>();
        loadDataFromJson();
        // RecyclerView를 위해 CustomAdapter를 사용
        mAdapter = new CommunityCustomAdapter(this, mArrayList);
        // 리사이클러뷰 객체와 어댑터 연결
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        // 리사이클러뷰 아이템을 클릭할 경우
        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CommunityDictionary dict = mArrayList.get(position);
                // 클릭시 토스트 생성
                // Toast.makeText(getApplicationContext(), dict.getTime()+' '+dict.getTitle(), Toast.LENGTH_LONG).show();

                // 제목, 시간, 내용 이미지 데이터를 ReadPostActivity에 전달하고 ReadPostActivity를 실행시킴, ReadPostActivity에서 제목과 내용, 이미지를 볼 수 있게 함
                Intent intent = new Intent(getBaseContext(), ReadPostActivity.class);


                intent.putExtra("time", dict.getTime());
                intent.putExtra("title", dict.getTitle());
                intent.putExtra("content", dict.getContent());
                intent.putExtra("image", dict.getImage());
                // 갤러리 이미지의 uri를 실제 저장된 경로로 바꿈(갤러리 이미지의 uri를 인텐트로 전달할 경우 이미지뷰에 제대로 나타나지 않음)
                Log.d(TAG, "dict.getimage(): " + dict.getImage());

                //gallaryImage = getRealPathFromUri(dict.getGallaryImage());
                //intent.putExtra("image", image );

                // String gallaryImage = getRealPathFromUri2(dict.getGallaryImage());
                // intent.putExtra("gallaryImage", gallaryImage );

                // 로그
                Log.d(TAG, "리사이클러뷰 아이템 클릭시 ReadPostActivity에 전달하는 제목: " + dict.getTitle());
                Log.d(TAG, "리사이클러뷰 아이템 클릭시 ReadPostActivity에 전달하는 내용: " + dict.getContent());
                Log.d(TAG, "리사이클러뷰 아이템 클릭시 ReadPostActivity에 전달하는 시간: " + dict.getTime());
                Log.d(TAG, "리사이클러뷰 아이템 클릭시 ReadPostActivity에 전달하는 카메라 이미지: " + dict.getImage());

                startActivity(intent);

            }

            // 롱클릭시 메소드
            @Override
            public void onLongClick(View view, int position) {
            }
        }));


        // 글쓰기 버튼 클릭시
        Button buttonInsert = (Button) findViewById(R.id.button_main_insert);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  WriteActivity를 실행
                Log.d(TAG, "글쓰기 버튼 누름: WriteActivity 실행");
                Intent intent = new Intent(CommunityActivity.this, WriteActivity.class);
                startActivityForResult(intent, 10);


//                AlertDialog.Builder builder = new AlertDialog.Builder(CommunityActivity.this);
//                View view = LayoutInflater.from(CommunityActivity.this)
//                        .inflate(R.layout.activity_write, null, false);
//                builder.setView(view);
//                final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
//                final EditText editTextID = (EditText) view.findViewById(R.id.edittext_dialog_id);
//                final EditText editTextEnglish = (EditText) view.findViewById(R.id.edittext_dialog_endlish);
//                final EditText editTextKorean = (EditText) view.findViewById(R.id.edittext_dialog_korean);
//
//                ButtonSubmit.setText("삽입");
//
//
//                final AlertDialog dialog = builder.create();
//                ButtonSubmit.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        String strtitle = editTextID.getText().toString();
//
//
//                       CommunityDictionary dict = new CommunityDictionary(strtitle);

//                        mArrayList.add(0, dict); //첫 줄에 삽입
//                        //mArrayList.add(dict); //마지막 줄에 삽입
//                        mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영

                //dialog.dismiss();
            }
        });

        // dialog.show();


        //mArrayList.add(0, dict); //첫 줄에 삽입
        //mArrayList.add(dict); //마지막 줄에 삽입
        // mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영

    }

    //        갤러리의 content://media/external/images/media/17254 주소는 실제 파일이 저장되어 있는 경로와 다르므로
    //        실제 저장된 경로로 바꾸기 위한 메소드
//    public static String getRealPathFromUrixx(Context context, Uri contentUri) {
//        Cursor cursor = null;
//        try {
//            String[] proj = { MediaStore.Images.Media.DATA };
//            Log.d(TAG, "contentUri: "+contentUri );
//            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            return cursor.getString(column_index);
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }
//    }

    //
    public String getRealPathFromUri(Uri contentUri) {

        String[] proj = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        Log.d(TAG, "getRealPathFromURI(), path : " + uri.toString());

        cursor.close();
        return path;
    }

    protected void onStop() {
        Log.d(TAG, "onStop 데이터 저장");
        super.onStop();
        saveDataToJson();
    }

    //TODO: 툴바 뒤로가기
    // 툴바 뒤로가기 버튼이 동작하게 하는 메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //  WriteActivity에서 등록 버튼을 눌러 CommunityActivity로 돌아올 때. WriteActivity에서 입력한 데이터를 전달받음
        if (requestCode == 10 && resultCode == RESULT_OK) {
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            String time = intent.getStringExtra("time");
            Uri image = intent.getParcelableExtra("image");
            //Uri gallaryImage = intent.getParcelableExtra("gallaryImage");
            Log.d(TAG, "WriteActivity로부터 넘겨받은 제목: " + title);
            Log.d(TAG, "WriteActivity로부터 넘겨받은 내용: " + content);
            Log.d(TAG, "WriteActivity로부터 넘겨받은 시간: " + time);
            Log.d(TAG, "WriteActivity로부터 넘겨받은 이미지: " + image);
            //Log.d(TAG, "WriteActivity로부터 넘겨받은 갤러리 이미지: "+gallaryImage);

            //Toast toast = Toast.makeText(this, key, Toast.LENGTH_SHORT);
            //toast.show();

            //CommunityDictionary의 객체 생성 후 매개변수 대입
            CommunityDictionary dict = new CommunityDictionary(title, time, content, image);
            mArrayList.add(0, dict); //첫 줄에 삽입
            //mArrayList.add(dict); //마지막 줄에 삽입
            mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영

            // 수정 버튼을 눌러 CommunityActivity로 돌아올 때. EditActivity에서 입력한 내용을 전달받음
        } else if (requestCode == 20 && resultCode == RESULT_OK) {
            Log.d(TAG, "수정 버튼을 눌러 CommunityActivity로 돌아옴");
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            String time = intent.getStringExtra("time");
            Uri image = intent.getParcelableExtra("image");
            //Uri gallaryImage = intent.getParcelableExtra("gallaryImage");
            int position = intent.getIntExtra("position", 0);
            Log.d(TAG, "EditActivity로부터 넘겨받은 제목: " + title);
            Log.d(TAG, "EditActivity로부터 넘겨받은 내용: " + content);
            Log.d(TAG, "EditActivity로부터 넘겨받은 시간: " + time);
            Log.d(TAG, "EditActivity로부터 넘겨받은 이미지: " + image);
            //Log.d(TAG, "EditActivity로부터 넘겨받은 갤러리 이미지: "+gallaryImage);
            Log.d(TAG, "EditActivity로부터 넘겨받은 포지션: " + position);
            //Toast toast = Toast.makeText(this, key, Toast.LENGTH_SHORT);
            //toast.show();

            //CommunityDictionary의 객체 생성 후 매개변수 대입
            CommunityDictionary dict = new CommunityDictionary(title, time, content, image);
            mArrayList.set(position, dict); //첫 줄에 삽입
            //mArrayList.add(dict); //마지막 줄에 삽입
            mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영
        }


    }

    // json으로 데이터 저장
    private void saveDataToJson() {
        // SharedPreferences 객체 생성
        SharedPreferences sharedPreferences1 = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        // Editor 객체 생성
        SharedPreferences.Editor editor = sharedPreferences1.edit();
        // JSONObject 객체 생성
        JSONObject obj = new JSONObject();
        try {
            JSONArray jArray = new JSONArray();//배열이 필요할때
            for (int i = 0; i < mArrayList.size(); i++)//배열
            {
                JSONObject object = new JSONObject();//배열 내에 들어갈 json
                object.put("title", mArrayList.get(i).getTitle());  // mArrayList에 기록된 제목을 불러와서 object에 저장
                Log.d(TAG, "쉐어드에 저장된 제목: " + mArrayList.get(i).getTitle());
                object.put("content", mArrayList.get(i).getContent()); //  mArrayList에 기록된 글의 내용을 object에 저장
                Log.d(TAG, "쉐어드에 저장된 내용: " + mArrayList.get(i).getContent());
                object.put("time", mArrayList.get(i).getTime());  // mArrayList에 기록된 시간(글을 쓴 시각)을 불러와서 object에 저장
                Log.d(TAG, "쉐어드에 저장된 시간: " + mArrayList.get(i).getTime());
                if (mArrayList.get(i).getImage() != null) {
                    Log.d(TAG, "게시글에 이미지가 존재할 경우");
                    object.put("image", mArrayList.get(i).getImage().toString()); //  mArrayList에 기록된 이미지 uri을 스트링으로 바꿔 object에 저장
                } else {
                    Log.d(TAG, "게시글에 이미지가 없을 경우");
                    object.put("Image", "이미지 없음"); //  이미지가 없는데 저장하려고 할 경우 nullPointExeption에러가 발생하므로 아무 글자나 저장해준다.

                }
                jArray.put(object); // jArray에 object 저장
            }
            obj.put("item", jArray);//오브젝트에 배열을 넣음
            String some = obj.toString();
            // editor에 some 넣고 적용
            editor.putString("key", some);
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // json으로 데이터 불러오기
    private void loadDataFromJson() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("key", null);

        if (json != null) {
            Log.d(TAG, "제이슨 not null");
            try {
                // object 객체 생성
                JSONObject object = new JSONObject(json);
                // 키값 "item"을 넣어 object에서 배열을 꺼내온다.
                JSONArray jsonArray = object.getJSONArray("item");
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d(TAG, "제이슨 반복문 시작");
                    // 꺼내온 배열에서 각 인덱스의 JSONObject를 불러와 jsonObject에 대입
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // 각 object의 String 변수 지정
                    String title = jsonObject.getString("title");
                    Log.d(TAG, "jsonObject에서 제목을 꺼내옴");
                    String time = jsonObject.getString("time");
                    Log.d(TAG, "jsonObject에서 시간을 꺼내옴");
                    String content = jsonObject.getString("content");
                    Log.d(TAG, "jsonObject에서 내용을 꺼내옴");
                    if (jsonObject.getString("image").equals("이미지 없음")) {
                        Log.d(TAG, "저장된 이미지가 없는 경우");
                        CommunityDictionary dict = new CommunityDictionary(title, time, content); // 제목, 시간, 내용을 넣음
                        mArrayList.add(dict); // 리사이클러뷰에 반영하기 위해 mArrayList에 추가
                    } else {
                        Log.d(TAG, "저장된 이미지가 있는 경우");
                        String imageString = jsonObject.getString("image");
                        Log.d(TAG, "jsonObject에서 갤러리 이미지를 꺼내옴");
                        Log.d(TAG, "jsonObject에서 꺼내온 이미지: " + imageString);
                        Uri image = Uri.parse(imageString); // String을 Uri로 바꿈
                        CommunityDictionary dict = new CommunityDictionary(title, time, content, image); // 제목, 시간, 내용, 이미지를 넣음
                        mArrayList.add(dict); // 리사이클러뷰에 반영하기 위해 mArrayList에 추가
                    }

//                    Log.d(TAG, "커뮤니티 게시판에 이미지 없이 글이 올라서와 저장된 이미지가 없는 경우");
//                    // dict 객체생성시 생성자에 대입
//                    CommunityDictionary dict = new CommunityDictionary(title, time, content);
//                    // mArrayList 마지막줄에 추가
//                    mArrayList.add(dict);
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


    // 리사이클러뷰 클릭 리스너 구현
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private CommunityActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CommunityActivity.ClickListener clickListener) {
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


    // 외부 저장소에 대한 퍼미션 허용
    private void requestReadExternalStoragePermission() {
        // checkSelfPermission()으로 권한이 획득되었는지 확인, 권한이 없다면
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            // 유저가 권한 요청 거절을 누른 적이 있을 경우 shouldShowRequestPermissionRationale()가 true를 반환
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "앱의 원활한 실행을 위해서 외부 저장소에 대한 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                // requestPermissions()으로 권한 요청
            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_CODE);
                // MY_PERMISSIONS_REQUEST_READ_EXT_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {
        // 요청 코드가 PERMISSIONS_REQUEST_CODE 이면
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE) {

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            // 모든 퍼미션이 허용되었으면
            if (check_result) {

                // 사진을 가져올 수 있음
            } else {
                // 퍼미션을 거부했을 경우 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(CommunityActivity.this, "퍼미션이 거부되었습니다. 커뮤니티 게시판을 이용하려면 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(CommunityActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }


//    // 외부 저장소 퍼미션 허용시 반응
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 100 : {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//   }
    }
}



