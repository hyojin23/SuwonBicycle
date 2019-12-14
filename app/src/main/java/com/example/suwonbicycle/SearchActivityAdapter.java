package com.example.suwonbicycle;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivityAdapter extends RecyclerView.Adapter<SearchActivityAdapter.ViewHolder> {

    private static final String TAG = "SearchActivityAdapter";

    private String searchRackName; // 보관소 이름
    private String landBasedAddress; // 보관소 지번주소
    private double latitude; // 위도
    private double longitude; // 경도
    private String airInjectorYn; // 공기주입기 설치여부
    private String repairStandYn; // 수리대 설치여부
    private String phoneNumber; // 관리기관 전화번호
    private String institutionNm; // 관리기관명
    private Context context;

    public List<SearchDictionary> filterList = null; // 검색 후 나오는 내용을 담는 리스트
    public static ArrayList<SearchDictionary> mList; // 모든 데이터를 담는 리스트
    //private Context mContext;
    //private ArrayList<String> nData = null ;



    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewN ;
        TextView textViewA ;
        ImageView map;
        ViewHolder(View itemView) {
            super(itemView) ;

            // 아이템 클릭 이벤트 처리.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO : 리사이클러뷰 아이템을 클릭했을때
                    Intent intent = new Intent(context, ShortInfoActivity.class);
                    searchRackName = filterList.get(getAdapterPosition()).getRackName(); // 보관소 이름
                    landBasedAddress = filterList.get(getAdapterPosition()).getLandBasedAddress(); // 보관소 지번주소
                    latitude= filterList.get(getAdapterPosition()).getLatitude(); // 위도
                    longitude = filterList.get(getAdapterPosition()).getLongitude(); // 경도
                    airInjectorYn = filterList.get(getAdapterPosition()).getAirInjectorYn();// 공기주입기 설치여부
                    repairStandYn = filterList.get(getAdapterPosition()).getRepairStandYn(); // 수리대 설치여부
                    phoneNumber = filterList.get(getAdapterPosition()).getPhoneNumber(); // 관리기관 전화번호
                    institutionNm = filterList.get(getAdapterPosition()).getInstitutionName(); // 관리기관명

                    intent.putExtra("dpstryNm", searchRackName); // 자전거 보관소명
                    intent.putExtra("lnmadr",landBasedAddress); // 보관소 지번주소
                    intent.putExtra("latitude", latitude); // 위도
                    intent.putExtra("longitude", longitude); // 경도
                    intent.putExtra("airInjectorYn", airInjectorYn); // 공기주입기 설치여부
                    intent.putExtra("repairStandYn", repairStandYn); // 수리대 설치여부
                    intent.putExtra("phoneNumber", phoneNumber); // 관리기관 전화번호
                    intent.putExtra("institutionNm", institutionNm); // 관리기관명
                    Log.d(TAG,  "전달된 값:"+ searchRackName);
                    Log.d(TAG,  "전달된 값:"+ landBasedAddress);
                    Log.d(TAG,  "전달된 값:"+ latitude);
                    Log.d(TAG,  "전달된 값:"+ longitude);
                    Log.d(TAG,  "전달된 값:"+ airInjectorYn);
                    Log.d(TAG,  "전달된 값:"+ repairStandYn);
                    Log.d(TAG,  "전달된 값:"+ phoneNumber);
                    Log.d(TAG,  "전달된 값:"+ institutionNm);
                    context.startActivity(intent);
                }
            });


            // 뷰 객체에 대한 참조. (hold strong reference)
            textViewN = itemView.findViewById(R.id.search_rack_name) ;
            textViewA = itemView.findViewById(R.id.search_rack_address) ;
            map = itemView.findViewById(R.id.search_blue_map);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    SearchActivityAdapter(Context context, List<SearchDictionary> filterList) {
        this.filterList = filterList;
        mList = new ArrayList<>();
        mList.addAll(filterList); // 검색 후 나오는 데이터인 filterList를 mList에 추가 (이유 모름)
        this.context = context;
    }




    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public SearchActivityAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.search_recyclerview_item, parent, false) ;
        SearchActivityAdapter.ViewHolder vh = new SearchActivityAdapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(SearchActivityAdapter.ViewHolder holder, int position) {
        String name = filterList.get(position).getRackName(); // 자전거보관소명
        String address = filterList.get(position).getLandBasedAddress(); // 소재지도로명주소
        holder.textViewN.setText(name) ;
        holder.textViewA.setText(address) ;

//        // 지도 모양 클릭시
//        holder.map.setOnClickListener(new ImageView.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:37.253852,126.982348"));
//                    v.getContext().startActivity(intent);
//                }
//            });
        }




    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return (null != filterList ? filterList.size() : 0);
        //return mList.size() ;

    }

    // 검색을 위한 필터 메소드
    public void filter(String charText) {
        Log.d(TAG, "필터 메소드 시작");
        charText = charText.toLowerCase(Locale.getDefault()); // editText에 입력된 문자를 소문자로 바꾼다. charText = 입력된 문자
        filterList.clear();
        Log.d(TAG, "필터리스트 사이즈: "+filterList.size());
        Log.d(TAG, "(charText.length():"+(charText.length()));
        if (charText.length() == 0) {
            Log.d(TAG, "입력된 문자가 없어 mList의 모든 데이터를 filterList에 추가");
            filterList.addAll(mList); // 입력된 문자가 없으면 mList의 모든 데이터를 filterList에 추가해 리사이클러뷰에 보여준다.
        } else {
            Log.d(TAG, "입력된 문자가 있어 mList의 데이터를 dictionary에 추가");
            for (SearchDictionary dictionary : mList) {  // 입력된 문자가 있으면 mList의 데이터를 dictionary에 추가
                String rackName = dictionary.getRackName(); // dictionary에 추가된 자전거 보관소명을 String에 저장
                String roadNameAddress = dictionary.getLandBasedAddress(); // dictionary에 추가된 지번주소를 String에 저장
                Log.d(TAG, "dictionary.getRackName() 값:"+dictionary.getRackName());
                Log.d(TAG, "charText값(입력된 텍스트):"+charText);
                Log.d(TAG, "rackName.toLowerCase().contains(charText) 값:"+rackName.toLowerCase().contains(charText));
                if (rackName.toLowerCase().contains(charText) || roadNameAddress.toLowerCase().contains(charText) ) { // 보관소명이나 주소에 editText에 입력된 문자가 있으면 filterList에 추가
                    Log.d(TAG, "filterList에 추가된 값:"+dictionary);
                    filterList.add(dictionary);

                }
            } //|| roadNameAddress.toLowerCase().contains(charText)
        }
        notifyDataSetChanged(); // 변경된 내용을 반영하여 보여줌
        Log.d(TAG, "필터 메소드에서 데이터 변경");
    }

}