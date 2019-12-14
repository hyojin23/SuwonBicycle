package com.example.suwonbicycle;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class ReadPostActivity extends AppCompatActivity {
    public static final String TAG = "ReadPostActivity";
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_post);

        String title = "";
        String content = "";
        String time = "";
        Uri image;
        // content://com.example.myapplication/my_images/TEST_20190622_165502_1711259088.jpg 형식의 실제경로(절대경로)는 Uri가 아닌 String 값이다.
        Bundle extras = getIntent().getExtras();

        title = extras.getString("title"); // 받은 제목
        content = extras.getString("content"); // 받은 내용
        time = extras.getString("time"); // 받은 시간
        image = extras.getParcelable("image"); // 받은 이미지
        //gallaryImage = extras.getParcelable("gallaryImage"); // 받은 갤러리 이미지
        //gallaryImage= getIntent().getStringExtra("gallaryImage"); //  받은 갤러리 이미지(실제경로 이미지이므로 String으로 불러옴)

        Log.d(TAG, "ReadPostActivity에서 받은 제목: " + title);
        Log.d(TAG, "ReadPostActivity에서 받은 내용: " + content);
        Log.d(TAG, "ReadPostActivity에서 받은 시간: " + time);
        Log.d(TAG, "ReadPostActivity에서 받은 이미지: " + image);


        // 뷰 객체와 연결 및 텍스트 등록
        TextView textViewTitle = (TextView) findViewById(R.id.text_view_title);
        textViewTitle.setText(title);
        textViewTitle.setTextColor(Color.parseColor("#000000"));
        TextView textViewContent = (TextView) findViewById(R.id.text_view_content);
        textViewContent.setText(content);
        TextView textViewTime = (TextView) findViewById(R.id.text_view_time);
        textViewTime.setText(time);
        ImageView imageView = (ImageView) findViewById(R.id.image_view_picture);
        //imageView.setImageBitmap(resize(context, uri, 500));
        // 글라이드 라이브러리로 사용한 이미지 등록

        getIntent().getData();
        Glide.with(this).load(image).into(imageView);
        //imageView.setImageURI(gallaryImage);
        Log.d(TAG, "이미지 등록: " + image);


//    // uri 이미지를 비트맵으로 바꾸고 용량을 줄이는 메소드
//    private Bitmap resize(Context context, Uri uri, int resize){
//        Bitmap resizeBitmap=null;
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        try {
//            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); // 1번
//
//            int width = options.outWidth;
//            int height = options.outHeight;
//            int samplesize = 1;
//
//            while (true) {//2번
//                if (width / 2 < resize || height / 2 < resize)
//                    break;
//                width /= 2;
//                height /= 2;
//                samplesize *= 2;
//            }
//
//            options.inSampleSize = samplesize;
//            Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options); //3번
//            resizeBitmap=bitmap;
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        return resizeBitmap;
//    }

    }
}