package com.cookandroid.chanhotalk.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cookandroid.chanhotalk.R;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import java.util.Map;

public class AccountFragment extends Fragment {

    LinearLayout linearLayout;
    Button button_background;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account,container,false);

        linearLayout = view.findViewById(R.id.accountFragment_linearlayout);
        button_background = view.findViewById(R.id.accountFragment_button_background);
        registerForContextMenu(button_background);



        Button button = view.findViewById(R.id.accountFragment_button_comment);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(view.getContext());
            }
        });

        // 개발자에게 메일보내기
        Button email = (Button) view.findViewById(R.id.accountFragment_button_email);
        email.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View view) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("plain/text");
                String[] address = {"ch_317@naver.com"};
                email.putExtra(Intent.EXTRA_EMAIL, address);
                email.putExtra(Intent.EXTRA_SUBJECT, "개발자님에게 건의사항이 있어요.");
                email.putExtra(Intent.EXTRA_TEXT, "안녕하세요");
                startActivity(email);
            }
        });

        return view;
    }

    // 계정의 상태 메시지 입력
    void showDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_comment,null);
        final EditText editText = view.findViewById(R.id.commentDialog_edittext);

        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Map<String,Object> stringObjectMap = new HashMap<>();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                stringObjectMap.put("comment", editText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(stringObjectMap);
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }

    // Account 배경색 바꾸기 메뉴 화면 구성
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        super.onCreateContextMenu(menu, v, menuInfo);

        if (v == button_background) {
            menu.setHeaderTitle("배경색 변경");

            menu.add(0, 1, 0, "배경색 (하양)");
            menu.add(0, 2, 0, "배경색 (빨강)");
            menu.add(0, 3, 0, "배경색 (초록)");
            menu.add(0, 4, 0, "배경색 (파랑)");

        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                linearLayout.setBackgroundColor(Color.WHITE);
                return true;
            case 2:
                linearLayout.setBackgroundColor(Color.RED);
                return true;
            case 3:
                linearLayout.setBackgroundColor(Color.GREEN);
                return true;
            case 4:
                linearLayout.setBackgroundColor(Color.BLUE);
                return true;
        }
        return false;
    }

}
