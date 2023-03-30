package com.example.igclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.igclone.fagment.HomeFragment;
import com.example.igclone.fagment.NotiFragment;
import com.example.igclone.fagment.ProfileFragment;
import com.example.igclone.fagment.SearchFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class ActivityChinh extends AppCompatActivity {


    BottomNavigationView navigationView;
    Fragment selectorFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chinh);

        navigationView = findViewById(R.id.bottom_navigation);

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.nav_search:
                        selectorFragment = new SearchFragment();
                        break;

                    case R.id.nav_heart:
                        selectorFragment = new NotiFragment();
                        break;

                    case R.id.nav_profile:
                        selectorFragment = new ProfileFragment();
                        getSharedPreferences("Profile",MODE_PRIVATE).edit()
                                .putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid()).apply();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                        break;

                    case R.id.nav_add:
                        selectorFragment = null;
                        startActivity(new Intent(ActivityChinh.this , PostActivity.class));
                        break;
                }

                if(selectorFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectorFragment).commit();

                }


                return true;
            }
        });

        Bundle bundle = getIntent().getExtras();
        if ( bundle != null){
            String profileid = bundle.getString("publisherId");
            navigationView.setSelectedItemId(R.id.nav_profile);
            getSharedPreferences("Profile",MODE_PRIVATE).edit().putString("profileid",profileid).apply();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        }
        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}