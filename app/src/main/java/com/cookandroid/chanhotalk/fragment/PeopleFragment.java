package com.cookandroid.chanhotalk.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.chanhotalk.R;
import com.cookandroid.chanhotalk.chat.MessageActivity;
import com.cookandroid.chanhotalk.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class PeopleFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people,container,false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.peoplefragment_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        return view;
    }


    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<UserModel> userModels;

        public PeopleFragmentRecyclerViewAdapter() {
            userModels = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //???????????? ????????? ?????? ??????
                    userModels.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        UserModel userModel = snapshot.getValue(UserModel.class);

                        //people??? ?????? ???????????? ??????????????? ??????
                        if (userModel.uid.equals(myUid)){
                             continue;
                        }
                        userModels.add(userModel);
                    }
                    //????????????
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend,parent,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            //????????? ?????????, ?????? ????????????
            ((CustomViewHolder)holder).textView.setText((userModels.get(position).uesrName) + " (" + (userModels.get(position).gender) + ")");

            //??????????????? ??? ????????? ????????? ??????????????? ?????? (?????????????????? ??????)
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MessageActivity.class);
                    intent.putExtra("destinationUid",userModels.get(position).uid);
                    ActivityOptions activityOptions = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(view.getContext(),R.anim.fromright ,R.anim.toleft);
                        startActivity(intent, activityOptions.toBundle());
                    }
                }
            });

            if(userModels.get(position).comment != null){
                ((CustomViewHolder)holder).textView_comment.setText(userModels.get(position).comment);
            }

        }

        @Override
        public int getItemCount() {
            return userModels.size() ;
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView textView_comment;

            public CustomViewHolder(View view) {
                super(view);
                imageView = (ImageView) view.findViewById(R.id.frienditem_imgview);
                textView = (TextView) view.findViewById(R.id.frienditem_textview);
                textView_comment = (TextView) view.findViewById(R.id.frienditem_textview_comment);

            }
        }
    }
}
