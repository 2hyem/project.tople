package com.example.meetingactivity.Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.meetingactivity.R;
import com.example.meetingactivity.adapter.BoardAdapter;
import com.example.meetingactivity.adapter.ShowAdapter;
import com.example.meetingactivity.model.Board;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InforFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InforFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InforFragment extends Fragment implements View.OnClickListener {
    ImageButton imageButton;
    ListView listNotice2;
    ArrayList<Board> list;
    ShowAdapter adapter;
    AsyncHttpClient client;
    HttpResponse response;
    //URL
    String URL= "http://192.168.0.64:8080/0823/board/board_list.jsp";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static Object OnFragmentInteractionListener;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public InforFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.clear();    //list에 데이터 삭제
        client.post(URL,response);
    }

    // TODO: Rename and change types and number of parameters
    public static InforFragment newInstance(String param1, String param2) {
        InforFragment fragment = new InforFragment();
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



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view =inflater.inflate(R.layout.fragment_infor, container, false);


        listNotice2 = view.findViewById(R.id.listNotice2);
        list = new ArrayList<>();
        adapter= new ShowAdapter(getActivity(),R.layout.list_item,list);
        client = new AsyncHttpClient();
        response=new HttpResponse(getActivity());
        listNotice2.setAdapter(adapter);

        return view;
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){

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
        // 통신 시작
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
        // 통신 성공
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


                    adapter.add(notice);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        // 통신 실패
        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            Toast.makeText(activity, "통신실패"+statusCode, Toast.LENGTH_SHORT).show();
        }
    }
}
