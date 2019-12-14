package com.example.suwonbicycle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class CommunityCustomAdapter extends RecyclerView.Adapter<CommunityCustomAdapter.CustomViewHolder> {

    private static final String TAG = "CommunityCustomAdapter";
    private ArrayList<CommunityDictionary> mList; // 텍스트 입력을 저장하는 리스트
    //private ArrayList<CommunityDictionary> timeList; // 시간 입력을 저장하는 리스트
    private Context mContext;
    private String imageFilePath;


    // 1. 컨텍스트 메뉴를 사용을 위해 RecyclerView.ViewHolder를 상속받은 클래스에서
    // OnCreateContextMenuListener 리스너를 구현
    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener { // 1. 리스너 추가
        // 텍스트뷰 생성
        protected TextView mtitle;
        protected TextView mtime;
        protected ImageView mImage;


        public CustomViewHolder(View view) {
            super(view);


            this.mtitle = (TextView) view.findViewById(R.id.textview_recyclerview_title); // 뷰의 위치 참조
            this.mtime = (TextView) view.findViewById(R.id.textview_recyclerview_time); // 뷰의 위치 참조
            this.mImage = (ImageView) view.findViewById(R.id.imageview_recyclerview_image);
            view.setOnCreateContextMenuListener(this); //2. OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정
        }


        // 3. 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록. ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됨
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {  // 3. 메뉴 추가U

            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "수정");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

        // 4. 캔텍스트 메뉴 클릭시 동작을 설정
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case 1001:  // 5. 수정 항목을 선택시
                        // 인텐트로 다른 액티비티로 전환 후 그 액티비티에서 수정
                        Log.d(TAG, "메뉴에서 수정 항목 선택");
                        Intent intent = new Intent(mContext, EditActivity.class);
                        Log.d(TAG, "EditActivity로 이동");
                        int position = getAdapterPosition();
                        intent.putExtra("title", mList.get(getAdapterPosition()).getTitle());
                        Log.d(TAG, "EditActivity로 전달된 제목: "+mList.get(getAdapterPosition()).getTitle() );
                        intent.putExtra("content", mList.get(getAdapterPosition()).getContent());
                        Log.d(TAG, "EditActivity로 전달된 내용: "+mList.get(getAdapterPosition()).getContent());
                        intent.putExtra("position", position);
                        Log.d(TAG, "EditActivity로 전달된 포지션: "+position);
                        intent.putExtra("time", mList.get(getAdapterPosition()).getTime());
                        Log.d(TAG, "EditActivity로 전달된 시간: "+mList.get(getAdapterPosition()).getTime());
                        intent.putExtra("image", mList.get(getAdapterPosition()).getImage());
                        Log.d(TAG, "EditActivity로 전달된 이미지: "+mList.get(getAdapterPosition()).getImage());
                        Activity origin = (Activity) mContext;
                        Log.d(TAG, "인텐트 시작");
                        origin.startActivityForResult(intent, 20);


                        // 다이얼로그 띄워서 수정
//                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                        View view = LayoutInflater.from(mContext)
//                                .inflate(R.layout.community_edit_box, null, false);
//                        builder.setView(view);
//                        final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
//                        final EditText editTextTitle = (EditText) view.findViewById(R.id.edittext_dialog_title);
//
//
//                        editTextTitle.setText(mList.get(getAdapterPosition()).getTitle());
//
//
//
//                        final AlertDialog dialog = builder.create();
//                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
//                            public void onClick(View v) {
//                                String strTitle = editTextTitle.getText().toString();
//
//
//
//                                CommunityDictionary dict = new CommunityDictionary(strTitle);
//
//                                mList.set(getAdapterPosition(), dict);
//                                notifyItemChanged(getAdapterPosition());
//
//                                dialog.dismiss();
//                            }
//                        });
//
//                        dialog.show();
//
                        break;

                    case 1002:  // 삭제 항목을 선택시

                        mList.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), mList.size());

                        break;

                }
                return true;
            }
        };


    }

    public CommunityCustomAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public CommunityCustomAdapter(Context context, ArrayList<CommunityDictionary> list) {
        mList = list;
        mContext = context;
        //timeList = time;


    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.community_item_list, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.mtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        Log.d(TAG, "커뮤니티 게시판 리사이클러뷰에 표시된 카메라 이미지: "+mList.get(position).getImage());


        viewholder.mtitle.setGravity(Gravity.LEFT);

        // 각 데이터를 아이템 텍스트뷰에 표시
        viewholder.mtitle.setText(mList.get(position).getTitle());
        viewholder.mtitle.setTextColor(Color.parseColor("#000000"));
        viewholder.mtime.setText(mList.get(position).getTime());
        Log.d(TAG, "커뮤니티 게시판 리사이클러뷰에 표시된 제목: "+mList.get(position).getTitle());
        Log.d(TAG, "커뮤니티 게시판 리사이클러뷰에 표시된 시간: "+mList.get(position).getTime());
        // WriteActivity에서 보낸 이미지 중 카메라 이미지가 없으면 갤러리 이미지를 보여주고, 카메라 이미지가 있으면 카메라 이미지를 보여줌
        Glide.with(mContext).load(mList.get(position).getImage()).into(viewholder.mImage); // 글라이드 사용해서 이미지 보여주면 사진이 회전되어 나오지 않고 정상적으로 잘 나온다.
        //viewholder.mImage.setImageURI(mList.get(position).getImage());
        Log.d(TAG, "이미지를 보여준다.");
        Log.d(TAG, "커뮤니티 게시판 리사이클러뷰에 표시된 이미지: "+mList.get(position).getImage());
        Glide.with(mContext).load(mList.get(position).getImage()).into(viewholder.mImage);

        //Bitmap rotatedBitMap = makeBitmapImage();
        //viewholder.mImage.setImageBitmap(rotatedBitMap);
        // viewholder.mtime.setText(mList.get(position).getContent());
    }

//    // uri를 bitmap으로 변환시킴
//    protected Bitmap uriToBitmap() {
//        Bitmap bitmap = null;
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(),mList.get(0).getUri());
//        } catch (
//                FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
//
//    // uri 주소로 된 이미지를 받아 비트맵 이미지로 변환하면서 회전시킴
//    protected Bitmap makeBitmapImage() {
//        Bitmap bitmap = uriToBitmap();
//       // Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath);
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
//        Bitmap rotatedBitMap = rotate(bitmap, exifDegree);
//        return rotatedBitMap;
//        //((ImageView)findViewById(R.id.photo)).setImageBitmap(rotate(bitmap, exifDegree));
//
//
//    }
//

//
//    // 상수를 받아 각도로 변환시켜주는 메소드
//    private int exifOrientationToDegrees(int exifOrientation) {
//        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
//            return 90;
//        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
//            return 180;
//        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
//            return 270;
//        }
//        return 0;
//    }
//
//    // 비트맵을 각도대로 회전시켜 결과를 반환해주는 메소드
//    private Bitmap rotate(Bitmap bitmap, float degree) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(degree);
//        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//    }
        @Override
        public int getItemCount() {
        return (null != mList ? mList.size() : 0);
}

}