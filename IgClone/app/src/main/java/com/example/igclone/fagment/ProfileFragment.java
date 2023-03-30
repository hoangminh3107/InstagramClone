package com.example.igclone.fagment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.igclone.Adapter.PhotoAdapter;
import com.example.igclone.CommentActivity;
import com.example.igclone.EditProfileActivity;
import com.example.igclone.FollowerActivity;
import com.example.igclone.Models.Post;
import com.example.igclone.Models.User;
import com.example.igclone.OptionActivity;
import com.example.igclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    CircleImageView avtar;
    TextView post, follower, following, fullname, bio, username;
    ImageView mysave, myimage, menu;
    RecyclerView recyclerViewMyImage, recyclerViewMySave;
    FirebaseUser mFirebaseUser;
    String profileId;
    PhotoAdapter photoAdapter, photoAdapterSave;
    List<Post> list;
    List<Post> list2;
    Button editProfile;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        avtar = view.findViewById(R.id.image_profile);
        post = view.findViewById(R.id.posts);
        follower = view.findViewById(R.id.follower);
        following = view.findViewById(R.id.following);
        fullname = view.findViewById(R.id.fullname);
        bio = view.findViewById(R.id.bio);
        mysave = view.findViewById(R.id.mysave);
        myimage = view.findViewById(R.id.myimage);
        username = view.findViewById(R.id.username);
        editProfile= view.findViewById(R.id.editprofile);
        recyclerViewMyImage = view.findViewById(R.id.recycler_view_myimage);
        recyclerViewMySave = view.findViewById(R.id.recycler_view_mysave);
        menu = view.findViewById(R.id.menu);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        profileId = mFirebaseUser.getUid();
        String data = getContext().getSharedPreferences("Profile", Context.MODE_PRIVATE).getString("profileid","none");

        recyclerViewMySave.setVisibility(View.GONE);
        recyclerViewMyImage.setVisibility(View.VISIBLE);
        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , FollowerActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("titel","follower");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext() , FollowerActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("titel","following");
                startActivity(intent);
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), OptionActivity.class));
            }
        });

        myimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewMySave.setVisibility(View.GONE);
                recyclerViewMyImage.setVisibility(View.VISIBLE);
                mysave.setImageResource(R.drawable.ic_save);
            }
        });
        mysave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewMySave.setVisibility(View.VISIBLE);
                recyclerViewMyImage.setVisibility(View.GONE);
                mysave.setImageResource(R.drawable.ic_saved);
            }
        });




        if(data.equals("none")){
          profileId = mFirebaseUser.getUid();

        }
        else {
            profileId = data;
        }


        if(profileId.equals(mFirebaseUser.getUid())){


            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
            });
        } else {
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Follows").child(mFirebaseUser.getUid())
                    .child("Following");
            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if (snapshot.child(profileId).exists()) {
                        editProfile.setText("Following");
                    } else {
                        editProfile.setText("Follow");
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editProfile.getText().toString().equals("Follow")) {
                        FirebaseDatabase.getInstance().getReference().child("Follows").child(mFirebaseUser.getUid())
                                .child("Following").child(profileId).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follows").child(profileId)
                                .child("Followers").child(mFirebaseUser.getUid()).setValue(true);

                        editProfile.setText("Following");
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Follows").child(mFirebaseUser.getUid())
                                .child("Following").child(profileId).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follows").child(profileId)
                                .child("Followers").child(mFirebaseUser.getUid()).removeValue();

                        editProfile.setText("Follow");

                    }
                }
            });
        }



        //profileId = mFirebaseUser.getUid();
        list = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(), list);
        recyclerViewMyImage.setHasFixedSize(true);
        recyclerViewMyImage.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewMyImage.setAdapter(photoAdapter);

        list2 = new ArrayList<>();
        photoAdapterSave = new PhotoAdapter(getContext(), list2);
        recyclerViewMySave.setHasFixedSize(true);
        recyclerViewMySave.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerViewMySave.setAdapter(photoAdapterSave);

        getInFor();
        getMyPhoto();
        getSavePhoto();
        getCountFollow();
        getCountPost();


        return view;
    }

    private void getSavePhoto() {

        List<String> saveId = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Saves").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    saveId.add(dataSnapshot.getKey());
                }
                FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list2.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);

                            for (String id : saveId) {
                                if (post.getPostid().equals(id)) {

                                        list2.add(post);
                                    }
//                                    list2.add(post);
                                }
                            }

                        photoAdapterSave.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMyPhoto() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)) {
                        list.add(post);
                    }

                }
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCountPost() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Post post = snapshot1.getValue(Post.class);
                    if (post.getPublisher().equals(profileId))
                        count++;

                }
                post.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getCountFollow() {
        FirebaseDatabase.getInstance().getReference().child("Follows").child(profileId).child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                follower.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Follows").child(profileId).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getInFor() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getImageurl()).into(avtar);
                username.setText(user.getUsername());
                fullname.setText(user.getName());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}