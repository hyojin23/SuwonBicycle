package com.example.suwonbicycle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.suwonbicycle.SearchActivityAdapter.mList;


public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    private TextView textView;
    public static ArrayList<SearchDictionary> mArrayList;
    private List<SearchDictionary> filterList = null; // 검색된 결과를 담는 리스트
    private RecyclerView mRecyclerView;
    public static SearchActivityAdapter mAdapter;
    private Context mContext;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_rack_search);

        final EditText editText = (EditText) findViewById(R.id.edit_text_view);
        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_search);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 리스트 객체 생성
        filterList = new ArrayList<>();
        // 어댑터 객체 생성
        mAdapter = new SearchActivityAdapter(this, filterList);
        // 리사이클러뷰에 어댑터 설정
        mRecyclerView.setAdapter(mAdapter);
        //mAdapter.notifyDataSetChanged();
        Log.d(TAG, "제이슨에서 자전거 정보 불러오기");
        Log.d(TAG, "불러온 정보 리사이클러뷰에 반영");
        loadBikeDataFromJson();
        filterList.addAll(mList); // 입력된 문자가 없으면 mList의 모든 데이터를 filterList에 추가해 리사이클러뷰에 보여준다.
        mAdapter.notifyDataSetChanged(); // 변경된 내용을 반영하여 보여줌

        //검색창
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {  // EditText에 입력이 끝났을 때
                Log.d(TAG, "editText에 입력된 값을 text에 저장");
                String text = editText.getText().toString() //  editText에 입력된 값
                        .toLowerCase(Locale.getDefault());
                Log.d(TAG, "text를 매개변수로 하여 filter 메소드 호출");
                mAdapter.filter(text);  //  editText에 입력된 값을 filter 메소드의 매개변수로 하여 filter 메소드 실행
            }

            // 입력 전 호출됨
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                Log.d(TAG, "beforeTextChanged 호출");
                Log.d(TAG, "텍스트를 입력하기 전에 입력된 문자가 없어 mList의 모든 데이터를 filterList에 추가");

            }

            // EditText에 입력이 진행될 때
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onTextChanged 호출");
            }
        });


    }

    // SharedPreferences에 저장된 데이터를 불러온다.
    private void loadBikeDataFromJson() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("bikeDataString", null); // 쉐어드에 저장된 String을 꺼내온다.

        if (json != null) {
            Log.d(TAG, "제이슨 not null");
            try {
                // object 객체 생성
                JSONObject object = new JSONObject(json);
                // 키값 "bikeInfo"을 넣어 object에서JSONArray를 꺼내온다.
                JSONArray jsonArray = object.getJSONArray("bikeInfo");
                Log.d(TAG, "mList의 크기: " + mList.size());
                mList.clear();
                Log.d(TAG, "클린 메소드 이후 mList의 크기: " + mList.size());
                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d(TAG, "반복문으로 JSONArray에 들어있는 데이터를 꺼낸다.");
                    // JSONArray에서 각 인덱스의 JSONObject를 불러와 jsonObject에 대입
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // 각 object의 String 변수 지정
                    String rackName = jsonObject.getString("rakcName"); // 보관소 이름
                    String LandBasedAddress = jsonObject.getString("LandBasedAddress"); // 보관소 지번주소
                    double latitude = jsonObject.getDouble("latitude"); // 위도
                    double longitude = jsonObject.getDouble("longitude"); // 경도
                    String airInjectorYn = jsonObject.getString("airInjectorYn"); // 공기주입기 설치여부
                    String repairStandYn = jsonObject.getString("repairStandYn"); // 수리대 설치여부
                    String phoneNumber = jsonObject.getString("phoneNumber"); // 관리기관 전화번호
                    String institutionNm = jsonObject.getString("institutionNm"); // 관리기관명
                    // dict 객체생성시 생성자에 대입
                    SearchDictionary dict = new SearchDictionary(rackName, LandBasedAddress, latitude, longitude, airInjectorYn, repairStandYn, phoneNumber, institutionNm);
                    Log.d(TAG, "값:" + rackName);
                    Log.d(TAG, "값:" + LandBasedAddress);
                    Log.d(TAG, "값:" + latitude);
                    Log.d(TAG, "값:" + longitude);
                    Log.d(TAG, "값:" + airInjectorYn);
                    Log.d(TAG, "값:" + repairStandYn);
                    Log.d(TAG, "값:" + phoneNumber);
                    Log.d(TAG, "값:" + institutionNm);

                    // mList 마지막줄에 추가
                    mList.add(dict);


                }
                Log.d(TAG, "제이슨에서 꺼낸 후 mList의 크기: " + mList.size());
            } catch (JSONException e) {
                Log.d(TAG, "캐치");
                e.printStackTrace();
            }
        }
    }

}
