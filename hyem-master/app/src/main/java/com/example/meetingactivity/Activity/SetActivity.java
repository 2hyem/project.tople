package com.example.moimproject4t;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kakao.auth.AccessTokenCallback;
import com.kakao.auth.ApiResponseCallback;
import com.kakao.auth.AuthService;
import com.kakao.auth.Session;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Gender;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SetActivity extends AppCompatActivity
        implements View.OnClickListener {
    Button buttonBackToHome, buttonToEdit;


    ImageView imageViewToEdit;
    LinearLayout layoutInfo, layoutEdit;

    TextView textView18, textViewName;

    Button button_logoff;

    String access_token = "";
    String refresh_token = "";

    //레이아웃 에딧 영역
    Button buttonBackToSet, buttonSave, buttonOut;
    ImageView imageView_profile;
    EditText editText_name, editText_intro, editText_pnum, editText_gender;
    TextView set_birth, set_region;




    String profile = ""; //프로필 사진 URL
    String name = "";   //닉네임
    String birthday = "";   //생일(mm/dd)
    String region = ""; //거주지역(기본값 null)
    String user_id = ""; //user 개별 id
    String phone_num = ""; //핸드폰 번호
    String self_int = ""; //자기소개
    String gender = ""; //성별
    String thumb = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        //타이틀바 제거
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        button_logoff = findViewById(R.id.button_logoff);
        textView18 = findViewById(R.id.textView18);
        textViewName=findViewById(R.id.textViewName_set);
        buttonBackToSet = findViewById(R.id.buttonBackToSet);
        buttonSave = findViewById(R.id.buttonSave);
        buttonOut = findViewById(R.id.buttonOut);

        buttonBackToHome = findViewById(R.id.buttonBackToHome);
        buttonToEdit = findViewById(R.id.buttonToEdit);
        imageViewToEdit = findViewById(R.id.imageViewToEdit);
        editText_gender=findViewById(R.id.editText_gender);
        imageView_profile = findViewById(R.id.imageViewFace);
        layoutInfo = findViewById(R.id.layoutInfo);
        layoutEdit = findViewById(R.id.layoutEdit);

        set_birth=findViewById(R.id.set_birth);
        set_region=findViewById(R.id.set_region);

        layoutEdit.setVisibility(View.GONE);
        layoutInfo.setVisibility(View.VISIBLE);

        editText_name=findViewById(R.id.editText_name);

        imageViewToEdit.setOnClickListener(this);
        buttonBackToHome.setOnClickListener(this);
        buttonToEdit.setOnClickListener(this);
        buttonBackToSet.setOnClickListener(this);
        buttonSave.setOnClickListener(this);
        button_logoff.setOnClickListener(this);
        set_region.setOnClickListener(this);
        set_birth.setOnClickListener(this);
        buttonOut.setOnClickListener(this);

        requestMe();


    }

    //로그아웃 = 세션 닫기
    private void logout() {
        UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
            @Override
            public void onCompleteLogout() {
                Toast.makeText(SetActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Log.d("[profile]", "로그아웃 성공");
            }

            @Override
            public void onSuccess(Long result) {
                Toast.makeText(SetActivity.this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                Log.d("[profile]", "로그아웃 성공 : " + result);
                Intent intent_goLogin = new Intent(SetActivity.this, MainActivity.class);
                intent_goLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent_goLogin.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent_goLogin);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_logoff:
                logout();
                break;
            case R.id.buttonBackToHome:
                Intent intent = new Intent(this, MypageActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.buttonToEdit:
                layoutInfo.setVisibility(View.GONE);
                layoutEdit.setVisibility(View.VISIBLE);
                break;
            case R.id.buttonBackToSet:
                layoutInfo.setVisibility(View.VISIBLE);
                layoutEdit.setVisibility(View.GONE);
                break;
            case R.id.buttonSave:

                break;

            case R.id.set_birth:
                break;

            case R.id.set_region:
                Log.d("[info]", "test");
                region_set();
                break;

            case R.id.buttonOut:
                onClickUnlink();
                break;

        }
    }

    private void region_set() {
      final String[] items = new String[]{"서울", "부산", "대구", "인천", "광주", "대전", "울산", "세종", "경기", "강원", "충북", "충남", "전북", "전남", "경북", "경남", "제주"};
      final int[] selectedIndex={0};

        AlertDialog.Builder dialog= new AlertDialog.Builder(SetActivity.this);
        dialog.setTitle("지역 선택").setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedIndex[0] = which;
            }
        }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                set_region.setText(items[selectedIndex[0]]);
            }
        });
        dialog.show();
    }

    private void requestMe() {
        //카카오서버에 기존 요청 정보외에 추가 정보(커스텀 파라미터등)을 요청하기 위한 키값
        final List<String> keys = new ArrayList<>();



        UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
            //v1에서는 세션종료에러, 비회원에러, 그외 에러가 있었으나 기존 v1이 종료되고 v2가 적용됨에 따라 비회원 에러는 사라짐

            //의도치 않은 세션 종료로 인한 에러
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Toast.makeText(SetActivity.this, "세션 종료", Toast.LENGTH_SHORT);
                Log.e("[error]", "세션 종료로 인한 에러 발생");

            }

            //세션 종료로 인한 에러를 제외한 모든 에러
            @Override
            public void onFailure(ErrorResult errorResult) {
                String error_message = "SessionClosed외의 에러 발생" + errorResult;
                Logger.d(error_message);
                Log.d("[error]", error_message);
            }

            @Override
            public void onSuccess(MeV2Response response) {


                Long id = response.getId();
                user_id = Long.toString(id);
                name = response.getNickname();
                UserAccount account = response.getKakaoAccount();
                thumb = response.getThumbnailImagePath();
                profile = response.getProfileImagePath();
                Map prop = response.getProperties();
                region = (String) prop.get("region");
                birthday = response.getKakaoAccount().getBirthday();
                self_int = (String) prop.get("self_int");
                phone_num = (String) prop.get("phone_num");
                Gender gender2 = response.getKakaoAccount().getGender();
                gender = String.valueOf(gender2);



                Log.d("[profile]", "user_id_set:" + user_id);
                Log.d("[profile]", "name_set:" + name);
                Log.d("[profile]", "thumb_set:" + thumb);
                Log.d("[profile]", "profile_set:" + profile);
                Log.d("[profile]", "region_set:"+region);
                Log.d("[profile]", "prop_set: "+ prop);
                Log.d("[profile]", "birthday_set:"+birthday);
                Log.d("[profile]", "phone_num_set:"+phone_num);
                Log.d("[profile]", "self_int_set:"+self_int);
                Log.d("[profile]", "gender:"+gender);


                requestAccessTokenInfo();
                handleScopeError(account);



                if ((name!=null)) {//이름 출력
                    editText_name.setText(name);
                    textViewName.setText(name);
                }
                if ((self_int!=null)) { //자기소개 출력
                    editText_intro.setText(self_int);
                }
                if ((phone_num!=null)) { //전화번호 출력
                    editText_pnum.setText(phone_num);
                }
                if ((region!=null)) { //거주지역 출력
                    set_region.setText(region);
                }
                if ((birthday!=null)) { //생일 출력
                    String birth_mm =  birthday.substring(0,2);
                    String birth_dd = birthday.substring(2);
                    set_birth.setText(birth_mm+"월 "+birth_dd+"일");
                }

                if(gender!="null"){ //성별 출력
                    if (gender=="MALE"){
                        editText_gender.setText("남성");
                    }else if(gender=="FEMALE"){
                        editText_gender.setText("여성");
                    }
                }

                if (profile!=null){ //프로필 이미지 출력
                    Glide.with(SetActivity.this).load(profile).into(imageViewToEdit);
                    Glide.with(SetActivity.this).load(profile).into(imageView_profile);
                }

            }

        });
    }


    //SDK환경하에서 액세스토큰의 유효성을 검사하는 메소드
    private void requestAccessTokenInfo() {
        AuthService.getInstance().requestAccessTokenInfo(new ApiResponseCallback<AccessTokenInfoResponse>() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                Toast.makeText(SetActivity.this, "세션 종료", Toast.LENGTH_SHORT);
                Log.e("[error]", "세션 종료로 인한 에러 발생");
            }


            @Override
            public void onFailure(ErrorResult errorResult) {
                Logger.e("failed to get access token info. msg=" + errorResult);
            }

            @Override
            public void onSuccess(AccessTokenInfoResponse accessTokenInfoResponse) {
                long userId = accessTokenInfoResponse.getUserId();
                String userId2 = Long.toString(userId);
                Logger.d("this access token is for userId=" + userId);
                Log.d("[profile]", "userId2 : " + userId2);

                long expiresInMilis = accessTokenInfoResponse.getExpiresInMillis();
                String exTime = Long.toString(expiresInMilis);
                Logger.d("this access token expires after " + expiresInMilis + " milliseconds.");
                Log.d("[profile]", "토큰유효시간 :" + exTime + "m/s");


            }
        });
    }

    //동적 동의 일종의 퍼미션체크 + 액세스토큰과 리프레쉬토큰에 대한 발급을 담당
    private void handleScopeError(UserAccount account) {
        List<String> neededScopes = new ArrayList<>();
        if (account.needsScopeAccountEmail()) {
            neededScopes.add("account_email");
        }
        if (account.needsScopeGender()) {
            neededScopes.add("gender");
        }
        Session.getCurrentSession().updateScopes(this, neededScopes, new
                AccessTokenCallback() {
                    @Override
                    public void onAccessTokenReceived(AccessToken accessToken) {
                        // 유저에게 성공적으로 동의를 받음. 부가정보에 접근 가능한 토큰을 재발급 받게 됨.
                        //Log.d("[token]", "sucess/"+accessToken);
                        access_token= accessToken.getAccessToken();
                        refresh_token = accessToken.getRefreshToken();

                        Log.d("[token]", "ACCESS TOKEN_1 : " + access_token); //REST-API에 접근할 수 있는 토큰
                        Log.d("[token]", "RefreshToken_1 : " + refresh_token);

                    }

                    @Override
                    public void onAccessTokenFailure(ErrorResult errorResult) {
                        // 동의 얻기 실패
                        Log.d("[token]", "fail/" + errorResult);
                    }
                }
                );


    }

    //회원탈퇴
    private void onClickUnlink() {
        final String appendMessage = "회원 탈퇴 하시겠습니까?";
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        finish();
                                    }

                                    @Override
                                    public void onNotSignedUp() {

                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        Toast.makeText(SetActivity.this, "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

        /**회원 탈퇴 DB처리 코드 넣을 영역
         *
         */

    }
}
