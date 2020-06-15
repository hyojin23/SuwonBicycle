package com.example.suwonbicycle;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/** 자전거 보관소 데이터 클래스 */

public class RackData {
    private static final String TAG = "RackData";
    private static RackData rackData = new RackData();

    private RackData() {}

    public static RackData getInstance() {
        return rackData;

    }

    // csv 파일을 한줄씩 읽어 어레이리스트에 저장 후 반환
    public ArrayList<String[]> readData(Context context) {
        // raw에 있는 csv 파일을 읽어옴
        InputStream is = context.getResources().openRawResource(R.raw.suwon_bicycle_rack_data);
        BufferedReader bf = null;
        try {
            bf = new BufferedReader(new InputStreamReader(is, "euc-kr"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ArrayList<String[]> rackList = new ArrayList<>();

        // 한줄씩 읽고 문자열을 탭을 기준으로 나눠 어레이리스트에 저장
        while (true) {
            try {
                String[] strings = bf.readLine().split("\t");
                rackList.add(strings);
            } catch (Exception e) {
                Log.d(TAG, "readData: while문 종료");
                e.printStackTrace();
                break;
            }
        }
        return rackList;
    }
}
