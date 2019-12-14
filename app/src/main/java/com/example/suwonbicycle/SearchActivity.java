package com.example.suwonbicycle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
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

        final EditText editText = (EditText)findViewById(R.id.edit_text_view);
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
        Log.d(TAG,"제이슨에서 자전거 정보 불러오기");
        Log.d(TAG,"불러온 정보 리사이클러뷰에 반영");
        loadBikeDataFromJson();
        filterList.addAll(mList); // 입력된 문자가 없으면 mList의 모든 데이터를 filterList에 추가해 리사이클러뷰에 보여준다.
        mAdapter.notifyDataSetChanged(); // 변경된 내용을 반영하여 보여줌




        //검색창
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {  // EditText에 입력이 끝났을 때
                Log.d(TAG,"editText에 입력된 값을 text에 저장");
                String text = editText.getText().toString() //  editText에 입력된 값
                        .toLowerCase(Locale.getDefault());
                Log.d(TAG,"text를 매개변수로 하여 filter 메소드 호출");
                mAdapter.filter(text);  //  editText에 입력된 값을 filter 메소드의 매개변수로 하여 filter 메소드 실행
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,  // 입력하기 전에 호출

                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
                Log.d(TAG,"beforeTextChanged 호출");
                Log.d(TAG, "텍스트를 입력하기 전에 입력된 문자가 없어 mList의 모든 데이터를 filterList에 추가");
//                filterList.addAll(mList); // 입력된 문자가 없으면 mList의 모든 데이터를 filterList에 추가해 리사이클러뷰에 보여준다.
//                mAdapter.notifyDataSetChanged(); // 변경된 내용을 반영하여 보여줌
//                filterList.clear();
                //Log.d(TAG, "필터리스트 초기화"); // on create 될때 필터리스트에 들어간 값을 전부 지운다.
               // filterList.clear();
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, // EditText에 입력이 진행될 때
                                      int arg3) {
                // TODO Auto-generated method stub
                Log.d(TAG,"onTextChanged 호출");
            }
        });

        // 리사이클러뷰 아이템 클릭시
//        mRecyclerView.addOnItemTouchListener(new SearchActivity.RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
//        @Override
//        public void onClick(View view, int position) {
//            SearchDictionary dict = mList.get(position);
//            // 클릭시 토스트 생성
//            // Toast.makeText(getApplicationContext(), dict.getTime()+' '+dict.getTitle(), Toast.LENGTH_LONG).show();
//            Log.d(TAG,  "클릭된 보관소명: "+dict.getRackName() );
//            Log.d(TAG,  "클릭된 지번주소: "+dict.getLandBasedAddress() );
//            // 데이터를 ShortInfoActivity에 전달하고 ShortInfoActivity를 실행
//            Intent intent = new Intent(getBaseContext(), ShortInfoActivity.class);
//            intent.putExtra("dpstryNm", dict.getRackName()); // 자전거 보관소명
//            intent.putExtra("lnmadr", dict.getLandBasedAddress()); // 보관소 지번주소
//            intent.putExtra("latitude", dict.getLatitude()); // 위도
//            intent.putExtra("longitude", dict.getHardness()); // 경도
//            intent.putExtra("airInjectorYn", dict.getAirInjectorYn()); // 공기주입기 설치여부
//            intent.putExtra("repairStandYn", dict.getRepairStandYn()); // 수리대 설치여부
//            intent.putExtra("phoneNumber", dict.getPhoneNumber()); // 관리기관 전화번호
//            intent.putExtra("institutionNm", dict.getInstitutionName()); // 관리기관명
//            Log.d(TAG,  "전달된 값:"+ dict.getRackName());
//            Log.d(TAG,  "전달된 값:"+ dict.getLandBasedAddress());
//            Log.d(TAG,  "전달된 값:"+ dict.getLatitude());
//            Log.d(TAG,  "전달된 값:"+ dict.getHardness());
//            Log.d(TAG,  "전달된 값:"+ dict.getAirInjectorYn());
//            Log.d(TAG,  "전달된 값:"+ dict.getRepairStandYn());
//            Log.d(TAG,  "전달된 값:"+ dict.getPhoneNumber());
//            Log.d(TAG,  "전달된 값:"+ dict.getInstitutionName());
//            startActivity(intent);
//        }
//            @Override
//            public void onLongClick(View view, int position) {
//
//            }
//        }));


        // 로딩스레드 객체 생성 및 실행
//        CheckTypesTask task = new CheckTypesTask();
//        task.execute();
//        Log.d(TAG,"로딩 AsyncTask 실행");
        // 공공데이터를 불러오는 AsyncTask 객체 생성 및 실행
//        PublicAsyncTask publicData = new PublicAsyncTask();
//        publicData.execute();
//        Log.d(TAG,"PublicAsyncTask 실행");
        // Intent intent = getIntent();

        // Bundle bundle = intent.getExtras();
//        String rackName = bundle.getString("rackName");
//        String address = bundle.getString("address");
//
//        TextView textView = (TextView) findViewById(R.id.xt);
//        textView.setText(rackName);
//        Log.d(TAG, textView.getText().toString());

        // 리사이클러뷰에 com.example.recyclerviewexample.SearchActivityAdapter 객체 지정.


//        SearchDictionary dict = new SearchDictionary();
//        SearchActivityAdapter mAdapter = new SearchActivityAdapter(mArrayList);
//
//        mRecyclerView.setAdapter(mAdapter);
//        mArrayList.add(0, dict); //첫 줄에 삽입
//        //mArrayList.add(dict); //마지막 줄에 삽입
//        mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영

//        final ImageView map = (ImageView) findViewById(R.id.search_blue_map);
//        map.setOnClickListener(new ImageView.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:37.252743,127.071662"));
//                startActivity(intent);
//            }
//        });


        }

//    // 공공데이터를 불러오기 위한 Asynctask
//
//    public class PublicAsyncTask extends AsyncTask<String, String, String> {
//
//        @Override
//        protected void onPreExecute() {
//            Log.d(TAG, "onPreExecute() 실행");
//            super.onPreExecute();
//
//
//        }
//
//        @Override
//        protected String doInBackground(String... abs) {
//            // TODO json데이터 가져오기
//            Log.d(TAG, "doInBackground() 실행");
//            //String str = "구리";
//            String strUrl = "http://api.data.go.kr/openapi/bcycl-dpstry-std";
//            strUrl += "?serviceKey=qK66giaNBLBdzCKVTFvwC1weXrhR9GIOrLDOdHfDYgiAsCZLnrbh%2Blhjj6VPFTaxNICs0lyefTHP3W0x%2FvhK9w%3D%3D";
//            strUrl += "&type=json";
//            //strUrl += "&rdnmadr="+ URLEncoder.encode(str);
//            //Log.d(TAG, "구리:"+URLEncoder.encode(str));
//            strUrl += "&numOfRows=200";
//            strUrl += "&pageNo=1";
//            //strUrl += "&instt_nm=UTF-8로 인코딩된 value";
//            Log.d(TAG, "doInBackground() 실행2");
//            URL url = null;
//            try {
//                url = new URL(strUrl);
//                Log.d(TAG, "strUrl 값:" + strUrl);
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//            URLConnection urlConnection = null;
//            try {
//                urlConnection = url.openConnection();
//                Log.d(TAG, "try2");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            HttpURLConnection connection = null;
//
//            if (urlConnection instanceof HttpURLConnection) {
//                connection = (HttpURLConnection) urlConnection;
//
//                Log.d(TAG, "if문");
//            } else {
//                Log.d(TAG, "else문");
//                // System.out.println("error");
//                // return;
//            }
//            Log.d(TAG, "doInBackground() 실행3");
//            BufferedReader in = null;
//            try {
//                Log.d(TAG, "doInBackground() 실행4");
//                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                Log.d(TAG, "doInBackground() 실행5");
//                Log.d(TAG, "connection 값:" + connection.getInputStream());
//                Log.d(TAG, "try3");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            String urlString = "";
//            String current;
//
//            while (true) {
//                try {
//                    if (!((current = in.readLine()) != null))
//                        Log.d(TAG, "urlString 값:" + urlString);
//                    urlString += current + "\n";
//                    Log.d(TAG, "urlString 값:" + urlString);
//                    Log.d(TAG, "try4");
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                Log.d(TAG, "urlString 값:" + urlString);
//                Log.d(TAG, "doInBackground() 실행6");
//                return urlString;
//            }
//        }
//
//        @Override
//        protected void onProgressUpdate(String... params) {
//            Log.d(TAG, "onProgressUpdate() 실행");
//
//        }
//
//        @Override
//        protected void onPostExecute(String urlString) {
//            Log.d(TAG, "onPostExecute() 실행");
//            super.onPostExecute(urlString);
//            try {
//                JSONObject object = new JSONObject(urlString);
//                JSONObject response = new JSONObject(object.getString("response"));
//                JSONObject body = new JSONObject(response.getString("body"));
//                JSONArray jarr = new JSONArray(body.getString("items"));
//                for (int i = 0; i < jarr.length(); i++) {
//                    JSONObject rack = new JSONObject(jarr.getJSONObject(i).toString());
//                    String rackName = rack.getString("dpstryNm"); // 자전거보관소명
//                    String roadNameAddress = rack.getString("rdnmadr"); // 보관소도로명주소
//
//                    Log.d(TAG, "자전거보관소명:" + rackName);
//                    Log.d(TAG, "보관소도로명주소:" + roadNameAddress);
//                    SearchDictionary dict = new SearchDictionary(rackName, roadNameAddress );
//                    Log.d(TAG, "dict:" + dict.getRackName());
//                    Log.d(TAG, "dict:" + dict.getRoadNameAddress());
//                    mArrayList.add(dict); //첫 줄에 삽입
//                    Log.d(TAG, "dict:" + mArrayList.size());
//                    Log.d(TAG,"제이슨에 자전거 정보 저장");
//                    saveBikeDataToJson();
//                    Log.d(TAG,"제이슨에서 자전거 정보 불러오기");
//                    loadBikeDataFromJson();
//                    Log.d(TAG,"불러온 정보 리사이클러뷰에 반영");
//                    mAdapter.notifyDataSetChanged();
//
//                    //mArrayList.add(dict); //마지막 줄에 삽입
//
//                    //textView.setText(jarr[0]);
//
//
//                }
//
//
//                mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//    // json으로 데이터 저장(스탑워치의 AraayList를 json형식으로 저장)
//   private void saveBikeDataToJson() {
//        // SharedPreferences 객체 생성
//        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
//        // Editor 객체 생성
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        // JSONObject 객체 생성
//        JSONObject obj = new JSONObject();
//        try {
//            JSONArray jArray = new JSONArray();//배열이 필요할때
//            for (int i = 0; i < mArrayList.size(); i++)//배열
//            {
//                JSONObject object = new JSONObject();//배열 내에 들어갈 json
//                object.put("rakcName", mArrayList.get(i).getRackName());  // mArrayList에 기록된 자전거보관소 이름을 불러와서 object에 저장
//                object.put("roadNameAddress", mArrayList.get(i).getRoadNameAddress());  // mArrayList에 기록된 보관소 도로명주소를 불러와서 object에 저장
//                jArray.put(object); // jArray에 object 저장
//            }
//            obj.put("bikeInfo", jArray);//오브젝트에 배열을 넣음
//            String some = obj.toString(); // 오브젝트를 String 형식으로 바꿈
//            // editor에 some 넣고 적용
//            editor.putString("bikeDataString", some);
//            editor.apply();
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // json으로 데이터 불러오기
    private void loadBikeDataFromJson() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        String json = sharedPreferences.getString("bikeDataString", null); // 쉐어드에 저장된 String을 꺼내온다.

        if (json != null) {
            Log.d(TAG, "제이슨 not null");
            try {
                // object 객체 생성
                JSONObject object = new JSONObject(json);
                // 키값 "item"을 넣어 object에서JSONArray를 꺼내온다.
                JSONArray jsonArray = object.getJSONArray("bikeInfo");
                Log.d(TAG, "mList의 크기: "+mList.size());
                mList.clear();
                Log.d(TAG, "클린 메소드 이후 mList의 크기: "+mList.size());
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
                    Log.d(TAG, "값:"+ rackName);
                    Log.d(TAG, "값:"+ LandBasedAddress);
                    Log.d(TAG, "값:"+ latitude);
                    Log.d(TAG, "값:"+ longitude);
                    Log.d(TAG, "값:"+ airInjectorYn);
                    Log.d(TAG, "값:"+ repairStandYn);
                    Log.d(TAG, "값:"+ phoneNumber);
                    Log.d(TAG, "값:"+ institutionNm);

                    // mList 마지막줄에 추가
                   mList.add(dict);


                }
                Log.d(TAG, "제이슨에서 꺼낸 후 mList의 크기: "+mList.size());
            } catch (JSONException e) {
                Log.d(TAG, "캐치");
                e.printStackTrace();
            }
        }
    }
        // 로딩 스레드
        private class CheckTypesTask extends AsyncTask<Void, Void, Void> {

        ProgressDialog asyncDialog = new ProgressDialog(
                SearchActivity.this);

        @Override
        protected void onPreExecute() {
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("로딩중입니다..");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                for (int i = 0; i < 5; i++) {
                    //asyncDialog.setProgress(i * 30);
                    Thread.sleep(500);
                    Log.d(TAG, "로딩스레드 동작");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "로딩스레드 종료 후 onPostExecute 동작");
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }

    // 리사이클러뷰 클릭 리스너 구현
    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private SearchActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SearchActivity.ClickListener clickListener) {
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
