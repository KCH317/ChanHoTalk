package com.cookandroid.chanhotalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class LoginActivity extends AppCompatActivity {

    private EditText id;
    private EditText password;

    private Button login;
    private Button signup;
    private FirebaseRemoteConfig FirebaseRemoteConfig;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    View dialogView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();

        String splash_background = FirebaseRemoteConfig.getString(getString(R.string.rc_color ));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor(splash_background));
        }

        id = (EditText)findViewById(R.id.loginActivity_edittext_id);
        password = (EditText)findViewById(R.id.loginActivity_edittext_password);

        login = (Button)findViewById(R.id.loginActivity_button_login);
        signup = (Button)findViewById(R.id.loginActivity_button_signup);
        login.setBackgroundColor(Color.parseColor(splash_background));
        signup.setBackgroundColor(Color.parseColor(splash_background));

        // ???????????? ????????????
        ImageButton admin = (ImageButton) findViewById(R.id.loginActivity_imagebutton_admin);
        admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView1 = (View) View.inflate(LoginActivity.this, R.layout.dialog_admin, null);
                AlertDialog.Builder dig = new AlertDialog.Builder(LoginActivity.this);
                dig.setView(dialogView1);
                dig.setPositiveButton("??????", null);
                dig.show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //????????? null ?????? ??????
                if (id.getText().toString().length() == 0){
                    id.requestFocus();
                    Toast.makeText(LoginActivity.this,"ID??? ???????????????.",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.getText().toString().length() == 0){
                    password.requestFocus();
                    Toast.makeText(LoginActivity.this,"PASSWORD??? ???????????????.",Toast.LENGTH_SHORT).show();
                    return;
                }
                loginEvent();
            }
        });

        //???????????? ????????????
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });

        //????????? ??????????????? ?????????
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    //?????????
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //????????????
                }
            }
        };
    }

    void loginEvent() {
        firebaseAuth.signInWithEmailAndPassword(id.getText().toString(),password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    //????????? ????????? ??????
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
