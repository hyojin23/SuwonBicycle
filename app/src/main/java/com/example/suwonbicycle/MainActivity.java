package com.example.suwonbicycle;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = " MainActivity";

    // 처음 AsyncTask를 통해 파싱한 공공데이터를 보관하는 리스트
    public static ArrayList<SearchDictionary> mainArrayList = new ArrayList<SearchDictionary>();
    // 뒤로가기 버튼을 눌렀을 때 동작을 제어
    private BackPressCloseHandler backPressCloseHandler;
    // GPS 사용을 위한 리퀘스트 코드, 퍼미션
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("onCreadte", "시작");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_main);

        // 리스트에 데이터가 없으면 데이터를 요청하는 asyncTask 실행
        if (mainArrayList.size() == 0) {
            // 공공데이터 불러오는 AsyncTask 실행
            Log.d(TAG, "공공데이터 불러오는 PublicAsyncTask 실행");
//            PublicAsyncTask asyncTask = new PublicAsyncTask();
//            asyncTask.execute();
        } else {
            Log.d(TAG, "onCreate: mainArrayList.size(): " + mainArrayList.size());
        }

        // 로티 실행
        setupLottie();

        // 뒤로가기 버튼 두 번 눌러야 종료되게 하는 클래스의 객체 생성
        backPressCloseHandler = new BackPressCloseHandler(this);

        // 현재 단말기가 위치정보 서비스를 사용 가능한지 확인
        // 불가능하면 GPS 활성화를 위한 메소드를 실행
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        // 가능하면 권한 확인
        } else {
            checkRunTimePermission();
        }

        // 로딩화면 출력
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY));

        // 툴바 기능
        Toolbar toolbar = findViewById(R.id.toolbar); //툴바설정
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"));
        setSupportActionBar(toolbar);//액션바와 같게 만들어줌
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 화살표를 만들어줌
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 툴바의 기본 제목을 보여주지 않음

        // 우측 상단 메일 아이콘 클릭하여 개발자에게 메일 보내는 인텐트 기능
        ImageView imageView = (ImageView) findViewById(R.id.email);
        imageView.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uriText =
                        "mailto:kimhj9292@gmail.com" +
                                "?subject=" + Uri.encode("") + // 제목에 입력될 텍스트
                                "&body=" + Uri.encode(""); // 본문에 입력될 텍스트

                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                String title = getString(R.string.message);
                startActivity(Intent.createChooser(sendIntent, title));
            }
        });


//         상태표시줄 색상 변경
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().setStatusBarColor(Color.parseColor("#4ea1d3"));
//        }

        // 내 주변 자전거 보관소 버튼 클릭시
        Button buttonAround = (Button) findViewById(R.id.around_rack_btn);
        buttonAround.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        // 전국 자전거 보관소 검색 클릭시
        Button buttonSearch = (Button) findViewById(R.id.search_rack_btn);

        buttonSearch.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        // 커뮤니티 게시판 클릭시
        Button buttonCommunity = (Button) findViewById(R.id.community_board_btn);

        buttonCommunity.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CommunityActivity.class);
                startActivity(intent);
            }
        });

        // 주행시간 기록 클릭시
        Button buttonRecord = (Button) findViewById(R.id.record_time_btn);

        buttonRecord.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordTimeActivity.class);
                startActivity(intent);
                // 호출하려는 액티비티가 stack에 존재할때 최상위로 올려줌
                // startActivity(intent.addFlags((FLAG_ACTIVITY_REORDER_TO_FRONT)));
            }
        });
        //onCreate 종료
    }


    // TODO: 현재 위치를 위도, 경도로 나타내고 주소로 표현시키는 메소드
    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {
        // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {

                //위치 값을 가져올 수 있음
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }

        // 외부 저장소 퍼미션 허용에 대한 반응(GPS랑 상관없는 메소드)
        if (permsRequestCode == 100) {
            // If request is cancelled, the result arrays are empty.
            if (grandResults.length > 0 && grandResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;
        }
    }

    private void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자에게 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionsResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionsResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


//    public String getCurrentAddress(double latitude, double longitude) {
//
//        //지오코더... GPS를 주소로 변환
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//
//        List<Address> addresses;
//
//        try {
//
//            addresses = geocoder.getFromLocation(
//                    latitude,
//                    longitude,
//                    7);
//        } catch (IOException ioException) {
//            //네트워크 문제
//            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
//            return "지오코더 서비스 사용불가";
//        } catch (IllegalArgumentException illegalArgumentException) {
//            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
//            return "잘못된 GPS 좌표";
//
//        }
//
//
//        if (addresses == null || addresses.size() == 0) {
//            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
//            return "주소 미발견";
//
//        }
//
//        Address address = addresses.get(0);
//        return address.getAddressLine(0).toString() + "\n";
//
//    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    // 위치정보 서비스를 사용 가능한지 확인하는 메소드
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // 네트워크나 GPS로부터 현재위치를 확인가능한지 확인하여 true or false 값을 리턴
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    // 툴바의 뒤로가기 버튼이 동작하도록 하는 메소드
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                moveTaskToBack(true);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    // 뒤로가기 버튼을 눌렀을 때
    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    // 공공데이터를 불러오기 위한 Asynctask
    public class PublicAsyncTask extends AsyncTask<String, String, String> {

        // 로딩 프로그레스 다이얼로그
        ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "onPreExecute() 실행");
            super.onPreExecute();
            // 다이얼로그 스타일
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // 화면 밖 터치시 종료 불가
            asyncDialog.setCancelable(false);
            // 다이얼로그 메세지
            asyncDialog.setMessage("데이터를 불러오는 중입니다. 잠시만 기다려 주세요...");
            asyncDialog.show();


        }

        @Override
        protected String doInBackground(String... abs) {
            // TODO json데이터 가져오기
            Log.d(TAG, "doInBackground() 실행");
            // strUrl: api 요청 주소
            String strUrl = "http://api.data.go.kr/openapi/bcycl-dpstry-std";
            strUrl += "?serviceKey=qK66giaNBLBdzCKVTFvwC1weXrhR9GIOrLDOdHfDYgiAsCZLnrbh%2Blhjj6VPFTaxNICs0lyefTHP3W0x%2FvhK9w%3D%3D";
            strUrl += "&type=json";
            strUrl += "&insttCode=3740000";
            //strUrl += "&rdnmadr="+ URLEncoder.encode(str);
            //Log.d(TAG, "구리:"+URLEncoder.encode(str));
            strUrl += "&numOfRows=326";
            strUrl += "&pageNo=1";
            //strUrl += "&instt_nm=UTF-8로 인코딩된 value";
            Log.d(TAG, "doInBackground() 실행2");
            URL url = null;
            try {
                url = new URL(strUrl);
                Log.d(TAG, "strUrl 값:" + strUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            URLConnection urlConnection = null;
            try {
                urlConnection = url.openConnection();
                Log.d(TAG, "try2");
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;

            if (urlConnection instanceof HttpURLConnection) {
                connection = (HttpURLConnection) urlConnection;

                Log.d(TAG, "if문");
            } else {
                Log.d(TAG, "else문");
                // System.out.println("error");
                // return;
            }
            Log.d(TAG, "doInBackground() 실행3");
            BufferedReader in = null;
            try {
                Log.d(TAG, "doInBackground() 실행4");
//                connection.setConnectTimeout(5000);
//                connection.setReadTimeout(5000);
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                Log.d(TAG, "doInBackground() 실행5");
                Log.d(TAG, "connection 값:" + connection.getInputStream());
                Log.d(TAG, "try3");
            } catch (java.net.SocketTimeoutException e) {
                Log.d(TAG, "connection timeout 예외 발생");
                // 다이얼로그 종료
                asyncDialog.dismiss();
            } catch (IOException e) {
                Log.d(TAG, "예외 발생");
                Toast.makeText(MainActivity.this, "데이터를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            String urlString = "";
            String current;

                while (true) {
                    try {
                        if ((current = in.readLine()) == null)
                            Log.d(TAG, "urlString 값:" + urlString);
                        urlString += current + "\n";
                        Log.d(TAG, "urlString 값:" + urlString);
                        Log.d(TAG, "try4");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "urlString 값:" + urlString);
                    Log.d(TAG, "doInBackground() 실행6");
                    return urlString;
                }

        }

        @Override
        protected void onProgressUpdate(String... params) {
            Log.d(TAG, "onProgressUpdate() 실행");

        }

        @Override
        protected void onPostExecute(String urlString) {
            Log.d(TAG, "onPostExecute() 실행");
            super.onPostExecute(urlString);
            asyncDialog.dismiss();
            try {
                JSONObject object = new JSONObject(urlString);
                JSONObject response = new JSONObject(object.getString("response"));
                JSONObject body = new JSONObject(response.getString("body"));
                JSONArray jarr = new JSONArray(body.getString("items"));
                // 메인 리스트에 데이터가 계속 쌓이지 않게 한번 초기화 후 데이터 저장
                // 자전거 보관소 검색에서 수원역과 같은 하나의 보관소를 검색시 수원역이 여러 개 들어있는 에러 해결
                // 현재 AsyncTask에서 json 데이터를 파싱하여 mainArrayList에 저장하고, 쉐어드에서는 mainArrayList.size()로, 즉 메인 리스트의 크기로
                // 반복문을 돌려 데이터를 저장하는데 mainArrayList가 초기화되지 않으면 계속 데이터가 쌓여 AsyncTask가 실행될 때마다 mainArrayList.size()=326, 652 이런식으로
                // 2배씩 증가한다. SearchActivity는 쉐어드에 저장된 데이터를 불러온 후 mList에 저장하고 검색하면 검색어와 일치하는 정보만 filterList에 저장하는데
                // 쉐어드에 저장된 정보가 계속 증가하니 SearchActivity에서 불러오는 정보도 늘어나 mList의 크기가 늘어난 것이다.
                mainArrayList.clear();
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject rack = new JSONObject(jarr.getJSONObject(i).toString());
                    String rackName = rack.getString("dpstryNm"); // 자전거보관소명
                    String landBasedAddress = rack.getString("lnmadr"); // 보관소지번주소
                    double latitude = rack.getDouble("latitude"); // 위도
                    double longitude = rack.getDouble("hardness"); // 경도
                    String airInjectorYn = rack.getString("airInjectorYn"); // 공기주입기 비치여부
                    String repairStandYn = rack.getString("repairStandYn"); // 수리대 설치여부
                    String phoneNumber = rack.getString("phoneNumber"); // 관리기관 전화번호
                    String institutionNm = rack.getString("institutionNm"); // 관리기관명
                    //  Log.d(TAG, "자전거보관소명:" + rackName);
                    //  Log.d(TAG, "보관소지번주소:" + landBasedAddress);
                    SearchDictionary dict = new SearchDictionary(rackName, landBasedAddress, latitude, longitude, airInjectorYn, repairStandYn, phoneNumber, institutionNm);
                    Log.d(TAG, "dict:" + dict.getRackName());
                    Log.d(TAG, "dict:" + dict.getLandBasedAddress());
                    Log.d(TAG, "dict:" + dict.getLatitude());
                    Log.d(TAG, "dict:" + dict.getLongitude());
                    Log.d(TAG, "dict:" + dict.getAirInjectorYn());
                    Log.d(TAG, "dict:" + dict.getRepairStandYn());
                    Log.d(TAG, "dict:" + dict.getPhoneNumber());
                    Log.d(TAG, "dict:" + dict.getInstitutionName());
                    mainArrayList.add(dict);
                    // SearchActivity.mArrayList.add(dict); //첫 줄에 삽입
                    //  Log.d(TAG, "dict:" + SearchActivity.mArrayList.size());


                    //mArrayList.add(dict); //마지막 줄에 삽입

                    //textView.setText(jarr[0]);
                }


                //  SearchActivity.mAdapter.notifyDataSetChanged(); //변경된 데이터를 화면에 반영
                Log.d(TAG, "제이슨에 자전거 정보 저장");
                saveBikeDataToJson();
//                Log.d(TAG,"제이슨에서 자전거 정보 불러오기");
//                loadBikeDataFromJson();
//                Log.d(TAG,"불러온 정보 리사이클러뷰에 반영");
//                SearchActivity.mAdapter.notifyDataSetChanged();


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    // json으로 데이터 저장(스탑워치의 AraayList를 json형식으로 저장)
    private void saveBikeDataToJson() {
        Log.d(TAG, "제이슨에 자전거 정보 저장 시작");
        // SharedPreferences 객체 생성
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        // Editor 객체 생성
        SharedPreferences.Editor editor = sharedPreferences.edit();
        // JSONObject 객체 생성
        JSONObject obj = new JSONObject();
        try {
            // 배열이 필요할때
            JSONArray jArray = new JSONArray();
            Log.d(TAG, "메인 리스트 사이즈: " + mainArrayList.size());
            // 배열
            for (int i = 0; i < mainArrayList.size(); i++)
            {
                //배열 내에 들어갈 json
                JSONObject object = new JSONObject();
                // mainArrayList에 기록된 자전거보관소 이름을 불러와서 object에 저장
                object.put("rakcName", mainArrayList.get(i).getRackName());
                // mainArrayList에 기록된 보관소 지번주소를 불러와서 object에 저장
                object.put("LandBasedAddress", mainArrayList.get(i).getLandBasedAddress());
                // mainArrayList에 기록된 위도를 불러와서 object에 저장
                object.put("latitude", mainArrayList.get(i).getLatitude());
                // mainArrayList에 기록된 경도를 불러와서 object에 저장
                object.put("longitude", mainArrayList.get(i).getLongitude());
                // mainArrayList에 기록된 공기주입기 비치여부를 불러와서 object에 저장
                object.put("airInjectorYn", mainArrayList.get(i).getAirInjectorYn());
                // mainArrayList에 기록된 수리대 설치여부를 불러와서 object에 저장
                object.put("repairStandYn", mainArrayList.get(i).getRepairStandYn());
                // mainArrayList에 기록된 관리기관 전화번호를 불러와서 object에 저장
                object.put("phoneNumber", mainArrayList.get(i).getPhoneNumber());
                // mainArrayList에 기록된 관리기관명을 불러와서 object에 저장
                object.put("institutionNm", mainArrayList.get(i).getInstitutionName());

                // jArray에 object 저장
                jArray.put(object);

                Log.d(TAG, "저장된 보관소명:" + mainArrayList.get(i).getRackName());
                Log.d(TAG, "저장된 지번주소:" + mainArrayList.get(i).getLandBasedAddress());
                Log.d(TAG, "저장된 위도:" + mainArrayList.get(i).getLatitude());
                Log.d(TAG, "저장된 경도:" + mainArrayList.get(i).getLongitude());
                Log.d(TAG, "저장된 공기주입기 설치여부:" + mainArrayList.get(i).getAirInjectorYn());
                Log.d(TAG, "저장된 수리대 설치여부:" + mainArrayList.get(i).getRepairStandYn());
                Log.d(TAG, "저장된 관리기관 전화번호:" + mainArrayList.get(i).getPhoneNumber());
                Log.d(TAG, "저장된 관리기관명:" + mainArrayList.get(i).getInstitutionName());

            }

            // 오브젝트에 배열을 넣음
            obj.put("bikeInfo", jArray);
            // 오브젝트를 String 형식으로 바꿈
            String some = obj.toString();
            // editor에 some 넣고 적용
            editor.putString("bikeDataString", some);
            editor.apply();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 로티 실행 메소드
    public void setupLottie() {
        LottieAnimationView lottie = findViewById(R.id.bike_lottie);
        lottie.setAnimation("bicycle_man.json");
        lottie.loop(true);   // or
        lottie.setRepeatCount(LottieDrawable.INFINITE);
        lottie.playAnimation();
    }

}



