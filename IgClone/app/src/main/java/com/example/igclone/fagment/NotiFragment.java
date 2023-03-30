package com.example.igclone.fagment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.igclone.Adapter.NotiAdapter;
import com.example.igclone.Models.Notification;
import com.example.igclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class NotiFragment extends Fragment {


    RecyclerView recyclerView;
    NotiAdapter notiAdapter;
    List<Notification> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noti, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_noti);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        notiAdapter = new NotiAdapter(getContext() , list );
        recyclerView.setAdapter(notiAdapter);

        readNoti();

        return view;

    }

    private void readNoti() {
        FirebaseDatabase.getInstance().getReference("Notifications").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                       for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                           if(!(dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))){
                               list.add(dataSnapshot.getValue(Notification.class));
                           }

                       }

                       notiAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}