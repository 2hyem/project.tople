package com.example.meetingactivity.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meetingactivity.R;
import com.example.meetingactivity.adapter.BoardAdapter;
import com.example.meetingactivity.adapter.ShowAdapter;
import com.example.meetingactivity.model.Board;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BoardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BoardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BoardFragment extends Fragment implements View.OnClickListener {

    ImageButton noticeButton;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private Animation fab_open,fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    LinearLayout fabLayout,fabLayout1,fabLayout2,boardLayout;
    LinearLayout binputLayout;
    TextView textFab1,textFab2;
    Button bbntBack, bbntWrite;
    EditText bSubject,bContent;
    ListView listNotice,listBoard;
    ArrayList<Board> list1,list2;

    ShowAdapter adapter2;
    ShowAdapter adapter1;
    //통신용 객체 선언
    AsyncHttpClient client;
    HttpResponse response;

    String masterLev="1";
    String lev="";


    String URL= "http://192.168.0.64:8080/0823/board/board_insert.jsp";
    String URLlist= "http://192.168.0.64:8080/0823/board/board_list.jsp";




    public BoardFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static BoardFragment newInstance(String param1, String param2) {
        BoardFragment fragment = new BoardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        fab_open= AnimationUtils.loadAnimation(getActivity(),R.anim.fab_open);
        fab_close=AnimationUtils.loadAnimation(getActivity(),R.anim.fab_close);

        client=new AsyncHttpClient();
        response= new HttpResponse(getActivity());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =inflater.inflate(R.layout.fragment_board, container, false);
        //플로팅 액션버튼
        fab=(FloatingActionButton) view.findViewById(R.id.fab);
        fab1=(FloatingActionButton)view.findViewById(R.id.fab1);
        fab2=(FloatingActionButton)view.findViewById(R.id.fab2);

        textFab1=view.findViewById(R.id.textFab1);
        textFab2=view.findViewById(R.id.textFab2);

        fabLayout=view.findViewById(R.id.fabLayout);
        fabLayout1=view.findViewById(R.id.fabLayout1);
        fabLayout2=view.findViewById(R.id.fabLayout2);
        //게시판 레이아웃
        boardLayout=view.findViewById(R.id.boardLayout);
        //공지사항 레이아웃
        //일반게시글 레이아웃
        binputLayout=view.findViewById(R.id.binputLayout);


        //공지사항 입력 후 리스트에 추가
        listNotice=view.findViewById(R.id.listNotice);
        listBoard=view.findViewById(R.id.listBoard);
        list1 = new ArrayList<>();
        list2=new ArrayList<>();

        adapter1=new ShowAdapter(getActivity(),R.layout.list_notice,list1);
        adapter2=new ShowAdapter(getActivity(),R.layout.list_item,list2);

        listNotice.setAdapter(adapter1);
        listBoard.setAdapter(adapter2);




        //게시글
        bbntBack=view.findViewById(R.id.bbntBack);
        bbntWrite=view.findViewById(R.id.bbntWrite);

        bSubject=view.findViewById(R.id.bSubject);
        bContent=view.findViewById(R.id.bContent);



        bbntBack.setOnClickListener(this);
        bbntWrite.setOnClickListener(this);

        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter2.clear();
        adapter1.clear();
        client.post(URLlist,response);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    //이벤트설정
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //플로팅 액션 버튼(+) 눌렀을때
            case R.id.fab:
                anim();
                break;
            case R.id.fab1://일반게시글 입력화면으로
                masterLev="2";
                anim();
                boardLayout.setVisibility(View.GONE);
                fabLayout.setVisibility(View.GONE);
                binputLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.fab2: //공지사항 입력화면으로  //+모임장 또는 관리자 권한을 가진사람만 보여주게해야함
                masterLev="1";
                anim();
                boardLayout.setVisibility(View.GONE);
                fabLayout.setVisibility(View.GONE);
                binputLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.bbntBack: //돌아가기 버튼
                bSubject.setText("");
                bContent.setText(""); //입력내용초기화
                binputLayout.setVisibility(View.GONE);
                boardLayout.setVisibility(View.VISIBLE);
                fabLayout.setVisibility(View.VISIBLE);
                break;
            case  R.id.bbntWrite: //공지사항입력
                String subject=bSubject.getText().toString().trim();
                String content=bContent.getText().toString().trim();
                String id="1";
                String moimcode="1";
                String filename="1";
                String listnum="2";
                String thumb="4";
                String editdate="2";

                if(masterLev.equals("1")){
                    lev = "1"; // 공지-1 일반-2

                }else if(masterLev.equals("2")){
                    lev="2";

                }
                Toast.makeText(getActivity(),lev,Toast.LENGTH_SHORT).show();

                //입력값이 있으면, 서버로 데이터 전송 및 요청
                RequestParams params = new RequestParams();

                Board item = new Board();
                item.setSubject(subject);
                item.setContent(content);

                if(masterLev.equals("1")){
                    list1.add(item);
                }else if(masterLev.equals("2")){
                    list2.add(item);
                }



                params.put("id",id);
                params.put("subject",subject);
                params.put("content",content);
                params.put("moimcode",moimcode);
                params.put("filename",filename);
                params.put("listnum",listnum);
                params.put("thumb",thumb);
                params.put("editdate",editdate);
                params.put("lev",lev);
                System.out.println("lev="+lev);
                //  params.put();

                client.post(URL, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String strJson = new String(responseBody);
                        try {
                            JSONObject json = new JSONObject(strJson);
                            String rt  = json.getString("rt");
                            if(rt.equals("OK")){
                                Toast.makeText(getActivity(),"저장성공",Toast.LENGTH_SHORT).show();

                            }else {
                                Toast.makeText(getActivity(),"실패",Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getActivity(), "통신실패"+statusCode, Toast.LENGTH_SHORT).show();
                    }
                });

                bSubject.setText("");
                bContent.setText("");
                binputLayout.setVisibility(View.GONE);
                boardLayout.setVisibility(View.VISIBLE);
                fabLayout.setVisibility(View.VISIBLE);
                break;
        }
    }


    //플로팅액션버튼 동작 함수
    public void anim() {
        if (isFabOpen) {
            fab.animate().rotationBy(60);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fabLayout1.setVisibility(View.GONE);
            fabLayout2.setVisibility(View.GONE);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab.animate().rotationBy(-60);

            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fabLayout1.setVisibility(View.VISIBLE);
            fabLayout2.setVisibility(View.VISIBLE);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    class HttpResponse extends AsyncHttpResponseHandler {
        Activity activity;
        ProgressDialog dialog;

        public HttpResponse(Activity activity) {
            this.activity = activity;
        }

        //통신 시작
        @Override
        public void onStart() {
            dialog = new ProgressDialog(activity);
            dialog.setMessage("잠시만 기다려 주세요...");
            dialog.setCancelable(false);
            dialog.show();
        }
        // 통신 종료
        @Override
        public void onFinish() {
            dialog.dismiss();
            dialog = null;
        }
        //통신 성공
        @Override
        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            String strJson = new String(responseBody);
            try {
                JSONObject json = new JSONObject(strJson);
                String rt = json.getString("rt");
                int total = json.getInt("total");
                JSONArray item = json.getJSONArray("item");
                for (int i=0; i<item.length(); i++) {
                    JSONObject temp = item.getJSONObject(i);
                    Board notice = new Board();
                    notice.setListnum(temp.getInt("listnum"));
                    notice.setId(temp.getString("id"));
                    notice.setMoimcode(temp.getInt("moimcode"));
                    notice.setSubject(temp.getString("subject"));
                    notice.setContent(temp.getString("content"));
                    notice.setFilename(temp.getString("filename"));
                    notice.setThumb(temp.getString("thumb"));
                    notice.setEditdate(temp.getString("editdate"));
                    notice.setLev(temp.getInt("lev"));

                    if(notice.getLev()==1){
                        adapter1.add(notice);
                    } else {
                        adapter2.add(notice);
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //통신 실패
        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Toast.makeText(activity, "통신실패"+statusCode, Toast.LENGTH_SHORT).show();
        }
    }
}
