package com.example.suwonbicycle;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ShortInfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_information);
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




        TextView rackNameTextView = (TextView)findViewById(R.id.text_view_rack_name);
        TextView rackAddressTextView = (TextView)findViewById(R.id.text_view_rack_address);
        TextView airInjectorTextView = (TextView)findViewById(R.id.text_view_air_injector);
        TextView repairStandTextView = (TextView)findViewById(R.id.text_view_repair_stand);
        TextView phoneNumberTextView = (TextView)findViewById(R.id.text_view_phone_number);
        TextView institutionTextView = (TextView)findViewById(R.id.text_view_institution_name);

        // SearchActivity에서 전달받은 데이터
        Bundle extras = getIntent().getExtras();
        String rackName = extras.getString("dpstryNm"); // 보관소 이름
        String rackAddress = extras.getString("lnmadr"); // 보관소 지번주소
        double latitude = extras.getDouble("latitude"); // 위도
        double longitude = extras.getDouble("longitude"); // 경도
        String airInjectorYn = extras.getString("airInjectorYn"); // 공기주입기 설치여부
        String repairStandYn = extras.getString("repairStandYn"); // 수리대 설치여부
        String phoneNumber = extras.getString("phoneNumber"); // 관리기관 전화번호
        String institutionNm = extras.getString("institutionNm"); // 관리기관명

        rackNameTextView.setText("자전거 보관소명: "+rackName);
        rackAddressTextView.setText("보관소 주소: "+rackAddress);
        airInjectorTextView.setText(("공기주입기 비치여부: "+airInjectorYn));
        repairStandTextView.setText("수리시설 설치여부: "+repairStandYn);
        phoneNumberTextView.setText("관리기관 전화번호: "+phoneNumber);
        institutionTextView.setText("관리기관명: "+institutionNm);

//       // 상세정보 더보기 버튼을 누를 경우
//        Button button = (Button) findViewById(R.id.longinfobtn);
//        button.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ShortInfoActivity.this, LongInfoActivity.class);
//                startActivity(intent);
//            }
//        });

    }



    // ShortInfoActivity에서 상단에 보이는 지도
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Bundle extras = getIntent().getExtras();
        String rackName = extras.getString("dpstryNm");
        double latitude = extras.getDouble("latitude");
        double longitude = extras.getDouble("longitude");
        LatLng SEOUL = new LatLng(latitude,  longitude); // 지도에 위도와 경도 입력

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL).title(rackName).snippet("자전거 보관소");
        Marker marker = googleMap.addMarker(markerOptions);
        marker.showInfoWindow();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));
    }
}
