package com.example.suwonbicycle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** 커뮤니티 게시판 글을 수정하는 액티비티 **/

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";

    private String imageFilePath; // 이미지가 저장되는 곳
    private Uri imageUri; // 이미지 Uri
    private ImageView editImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        String title = "";
        String content = "";
        String time = "";
        final int position;
        final Uri beforeImageUri;

        // 커뮤니티 글을 수정하는 액티비티에서 이전에 입력했던 제목, 내용, 이미지 등이 그대로 나타나게 함
        Bundle extras = getIntent().getExtras();
        title = extras.getString("title");
        content = extras.getString("content");
        position = extras.getInt("position");
        time = extras.getString("time");
        beforeImageUri = extras.getParcelable("image");
        imageUri = beforeImageUri;  // 수정 버튼을 누르면 원래 있던 이미지가 현재 적용할 이미지가 되게 한다.(제목이나 내용만 수정했을때 이미지가 사라지는 현상을 막음)
        Log.d(TAG, "전달받은 제목: "+title);
        Log.d(TAG, "전달받은 내용: "+content);
        Log.d(TAG, "전달받은 포지션: "+position);
        Log.d(TAG, "전달받은 시간: "+time);
        Log.d(TAG, "전달받은 이미지: "+beforeImageUri);
        Log.d(TAG, "수정 버튼을 누를 때 세팅된 imageUri: "+imageUri);



        // 뷰 객체가 레이아웃과 연결되게 함
        final EditText editTextTitle = (EditText) findViewById(R.id.edit_title);
        final EditText editTextContent = (EditText) findViewById(R.id.edit_content);
        editImageView = (ImageView) findViewById(R.id.edit_image);

        Button button = (Button) findViewById(R.id.edit_btn);

        editTextTitle.setText(title); // 입력했던 제목을 수정하는 액티비티의 텍스트뷰에 표시
        editTextContent.setText(content); // 입력했던 내용을 수정하는 액티비티의 텍스트뷰에 표시
        editImageView.setImageURI(beforeImageUri); // 입력되었던 사진을 수정하는 액티비티의 이미지뷰에 표시


        // 카메라 버튼을 클릭할 경우
        ImageView imageView = (ImageView)findViewById(R.id.write_camera_image_view_btn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent( WriteActivity.this, CommunityActivity.class);
                //sendTakePhotoIntent();
                show(); // 다이얼로그 실행
            }
        });

        //TODO: 수정 버튼을 클릭할 경우
        // 7. 수정 버튼을 클릭하면 현재 UI에 입력되어 있는 제목, 내용, 시간, 이미지로 내용이 바뀌도록 함
        final String finalTime = time;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent( WriteActivity.this, CommunityActivity.class);
                Intent intent = getIntent();
                intent.putExtra("title", editTextTitle.getText().toString());
                intent.putExtra("content", editTextContent.getText().toString());
                intent.putExtra("position", position);
                intent.putExtra("time", finalTime);
                intent.putExtra("image", imageUri);
                Log.d(TAG, "수정 버튼을 누를 때 커뮤니티 게시판에 전달하는 제목: "+editTextTitle.getText().toString());
                Log.d(TAG, "수정 버튼을 누를 때 커뮤니티 게시판에 전달하는 내용: "+editTextContent.getText().toString());
                Log.d(TAG, "수정 버튼을 누를 때 커뮤니티 게시판에 전달하는 포지션: "+position);
                Log.d(TAG, "수정 버튼을 누를 때 커뮤니티 게시판에 전달하는 시간: "+finalTime);
                Log.d(TAG, "수정 버튼을 누를 때 커뮤니티 게시판에 전달하는 이미지: "+imageUri);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    // 다이얼로그를 띄우는 메소드
    public void show()
    {
        final List<String> listItems = new ArrayList<>();

        listItems.add("갤러리에서 사진 불러오기");
        listItems.add("카메라로 사진 찍기");


        final CharSequence[] items =  listItems.toArray(new String[ listItems.size()]); // 리스트를 배열로 바꾸는데, 리스트 크기만큼의 배열로 바꾼다.

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("사진 선택"); // 다이얼로그 제목
        // 다이얼로그에서 각 아이템이 선택될 때
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0 : // 갤러리에서 사진 불러오기를 선택한 경우
                        takeImageFromGallery();
                        Log.d(TAG, "갤러리에서 사진 불러오기를 선택");
                        break;
                    case 1 : // 카메라로 사진 찍기를 선택한 경우
                        sendTakePhotoIntent();
                        Log.d(TAG, "카메라로 사진 찍기를 선택");
                        break;
                }
                String selectedText = items[position].toString(); // 선택된 텍스트는 배열의 각 포지션에 위치한 값이다.
                Toast.makeText(EditActivity.this, selectedText, Toast.LENGTH_SHORT).show(); // 선택된 텍스트를 토스트로 띄운다.
            }
        });
        builder.show(); // 다이얼로그 알림창을 띄운다.
    }
    // 갤러리를 실행해 사진을 불러오는 메소드
    private  void takeImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, 2);
    }



    // 이미지 파일을 생성하는 메소드(이미지가 저장될 파일을 만듬)
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }
    // 인텐트를 이용하여 카메라로 사진을 찍으라는 요청을 보냄
    private void sendTakePhotoIntent() {
        Log.d(TAG, "카메라 앱을 실행시킴");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {
                imageUri = FileProvider.getUriForFile(this, getPackageName(), photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }
    // intent로 불러온 이미지를 이미지뷰에 띄운다.(이미지 회전 후 onActivityResult)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 카메라 앱을 실행 후 종료한 상황에서 동작
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Log.d(TAG, "카메라 앱을 실행 후 종료한 상황");

//        // 이미지 회전 없이 사진으로 찍은 이미지를 이미지뷰에 세팅(이미지가 돌아가서 나옴)
//        ((ImageView)findViewById(R.id.write_image)).setImageURI(photoUriU);
            // ImageView imageView = (ImageView)findViewById(R.id.write_image);
            Glide.with(this).load(imageUri).into(editImageView);


//        // 이미지 회전시킬때 사용
//        Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
//        ExifInterface exif = null;
//
//        try {
//            exif = new ExifInterface(imageFilePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        int exifOrientation;
//        int exifDegree;
//
//        if (exif != null) {
//            exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            exifDegree = exifOrientationToDegrees(exifOrientation);
//        } else {
//            exifDegree = 0;
//        }
//
//        ((ImageView) findViewById(R.id.write_image)).setImageBitmap(rotate(bitmap, exifDegree));

            //갤러리 앱을 실행 후 종료한 상황에서 동작
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            Log.d(TAG, "갤러리 앱을 실행 후 종료한 상황");
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(editImageView);


            //((ImageView)findViewById(R.id.write_image)).setImageURI(selectedImageUri);
            //imageView.setImageURI(selectedImageUri);
        }
    }
}




