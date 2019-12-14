package com.example.suwonbicycle;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;


public class RecordTimeAdapter extends RecyclerView.Adapter<RecordTimeAdapter.CustomViewHolder>  {

    private ArrayList<RecordTimeDictionary> mList; // 텍스트 입력을 저장하는 리스트
    private Context mContext;



    // 1. 컨텍스트 메뉴를 사용을 위해 RecyclerView.ViewHolder를 상속받은 클래스에서
    // OnCreateContextMenuListener 리스너를 구현
    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener { // 1. 리스너 추가
        // 텍스트뷰 생성
        protected TextView mtitle;
        protected  TextView mtime;


        // 뷰홀더 생성
        public CustomViewHolder(View view) {
            super(view);
            this.mtitle = (TextView) view.findViewById(R.id.text_view_record_title); // 뷰의 위치 참조
            this.mtime = (TextView) view.findViewById(R.id.text_view_record_time); // 뷰의 위치 참조
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
//                        // 인텐트로 다른 액티비티로 전환 후 그 액티비티에서 수정
//                        Intent intent = new Intent(mContext,EditActivity.class);
//                        int position = getAdapterPosition();
//                        intent.putExtra("title", mList.get(getAdapterPosition()).getTitle());
//                        intent.putExtra("content", mList.get(getAdapterPosition()).getContent());
//                        intent.putExtra("position", position);
//                        intent.putExtra("time", mList.get(getAdapterPosition()).getTime());
//                        Activity origin = (Activity)mContext;
//                        origin.startActivityForResult(intent, 20);


                        // 다이얼로그 띄워서 수정
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        // 다이얼로그를 보여주기 위해 community_edit_box 레이아웃 사용
                        View view = LayoutInflater.from(mContext)
                                .inflate(R.layout.community_edit_box, null, false);
                        builder.setView(view);
                        final Button ButtonSubmit = (Button) view.findViewById(R.id.button_dialog_submit);
                        final EditText editTextTitle = (EditText) view.findViewById(R.id.edittext_dialog_title);

                        // 해당 줄에 입력되어있던 데이터를 불러와 다이얼로그에 보여줌
                        editTextTitle.setText(mList.get(getAdapterPosition()).getTitle());



                        final AlertDialog dialog = builder.create();
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {
                            // 수정 버튼을 클릭하면 현재 UI에 입력되어있는 내용으로
                            public void onClick(View v) {
                                // editTextTitle에 입력한 수정할 텍스트
                                String strTitle = editTextTitle.getText().toString();
                                String strTime = mList.get(getAdapterPosition()).getTime();
                                //수정할 텍스트를 RecordTimeDictionary에 넣는다.
                                RecordTimeDictionary dict = new RecordTimeDictionary(strTitle, strTime);

                                // ListArray에 있는 데이터를 변경하고
                                mList.set(getAdapterPosition(), dict);

                                // 어댑터에서 RecyclerView에 반영하도록 함
                                notifyItemChanged(getAdapterPosition());

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

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


    public RecordTimeAdapter(Context context, ArrayList<RecordTimeDictionary> list) {
        mList = list;
        mContext = context;
        //timeList = time;


    }



    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.record_time_item, viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        // mtitle의 텍스트 크기 지정
        viewholder.mtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        viewholder.mtitle.setTypeface(Typeface.DEFAULT_BOLD);

        //  mtitle의 텍스트 위치 지정
        viewholder.mtitle.setGravity(Gravity.LEFT);

        // 각 데이터를 아이템 텍스트뷰에 표시
        viewholder.mtitle.setText(mList.get(position).getTitle());
        viewholder.mtime.setText(mList.get(position).getTime());
        // viewholder.mtime.setText(mList.get(position).getContent());


    }



    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }



}

