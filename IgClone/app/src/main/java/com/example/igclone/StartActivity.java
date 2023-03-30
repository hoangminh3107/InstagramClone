package com.example.igclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class StartActivity extends AppCompatActivity {
    ImageView imgIcon;
    LinearLayout linearLayout;
    Button login , register;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgIcon = findViewById(R.id.icon_img);
        login = findViewById(R.id.login);
        linearLayout = findViewById(R.id.linearlayout);
        register = findViewById(R.id.register);

        linearLayout.animate().alpha(0f).setDuration(1);
        TranslateAnimation animation = new TranslateAnimation(0,0,0,-1000);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAni());
        imgIcon.setAnimation(animation);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this , RegisterActivity.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this , LoginActivity.class));
            }
        });


    }


    private  class MyAni implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
        imgIcon.clearAnimation();
        imgIcon.setVisibility(View.GONE);
        linearLayout.animate().alpha(1f).setDuration(1000);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    if(FirebaseAuth.getInstance().getCurrentUser() != null){
        startActivity(new Intent(StartActivity.this, ActivityChinh.class));
//        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
//            @Override
//            public void onSuccess(String token) {
//                HashMap<String , Object> obj = new HashMap<>();
//                obj.put("token" , token);
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
//                        .updateChildren(obj);
//
//            }
//        });
        finish();
    }
    }
}
