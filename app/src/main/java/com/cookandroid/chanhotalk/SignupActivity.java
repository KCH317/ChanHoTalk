package com.cookandroid.chanhotalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.cookandroid.chanhotalk.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class SignupActivity extends AppCompatActivity {


    private EditText email, name, password;
    private Button signup;
    private String splash_background;
    private RadioGroup radioGroup_gender;
    private RadioButton radio_man, radio_woman;
    private DatePicker datePicker;
    private int gender = 1;
    View dialogView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        splash_background = mFirebaseRemoteConfig.getString(getString(R.string.rc_color));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }


        email = (EditText) findViewById(R.id.signupActivity_edittext_email);
        name = (EditText) findViewById(R.id.signupActivity_edittext_name);
        password = (EditText) findViewById(R.id.signupActivity_edittext_password);
        signup = (Button) findViewById(R.id.signupActivity_button_signup);
        signup.setBackgroundColor(Color.parseColor(splash_background));
        radioGroup_gender = (RadioGroup) findViewById(R.id.signupActivity_radiogroup_gender);
        radio_man = (RadioButton) findViewById(R.id.signupActivity_radio_man);
        radio_woman = (RadioButton) findViewById(R.id.signupActivity_radio_woman);
        datePicker = (DatePicker) findViewById(R.id.signupActivity_datepicker);

        // 회원가입 규칙설명
        ImageButton rule = (ImageButton) findViewById(R.id.signupActivity_imagebutton_signuprule);
        rule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = (View) View.inflate(SignupActivity.this, R.layout.dialog_signup, null);
                AlertDialog.Builder dig = new AlertDialog.Builder(SignupActivity.this);
                dig.setView(dialogView);
                dig.setPositiveButton("확인", null);
                dig.show();
            }
        });

        final int month = datePicker.getMonth() + 1;

        radioGroup_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.signupActivity_radio_man) {
                    gender = 1;
                } else if (i == R.id.signupActivity_radio_woman) {
                    gender = 2;
                }
            }
        });


        //회원가입
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //회원가입 오류 검사
                if (email.getText().toString() == null || name.getText().toString() == null || password.getText().toString() == null) {
                    return;
                }

                if (password.getText().toString().length() < 6) {
                    password.requestFocus();
                    Toast.makeText(SignupActivity.this, "PASSWORD는 최소 6자리입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }


                // firabase realtime database에 회원 정보 저장
                FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                UserModel userModel = new UserModel();
                                userModel.uesrName = name.getText().toString();
                                userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                if (gender == 1) {
                                    userModel.gender = radio_man.getText().toString();
                                } else if (gender == 2) {
                                    userModel.gender = radio_woman.getText().toString();
                                }

                                userModel.birthday = datePicker.getYear() + "." + month + "." + datePicker.getDayOfMonth();

                                String uid = task.getResult().getUser().getUid();
                                FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //회원가입 성공
                                        SignupActivity.this.finish();
                                    }
                                });


                            }
                        });
            }

        });
    }



}
