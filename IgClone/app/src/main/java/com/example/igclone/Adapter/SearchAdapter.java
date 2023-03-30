package com.example.igclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.igclone.ActivityChinh;
import com.example.igclone.Models.User;
import com.example.igclone.R;
import com.example.igclone.fagment.ProfileFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    Context context;
    List<User> list;
    boolean isfragment;
    FirebaseUser mFirebaseUser;

    public SearchAdapter(Context context, List<User> list, boolean isfragment) {
        this.context = context;
        this.list = list;
        this.isfragment= isfragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        User user = list.get(position);
        Picasso.get().load(user.getImageurl()).into(holder.avatar);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getName());


        if(user.getId().equals(mFirebaseUser.getUid())){
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

        }

//        holder.username.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                context.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("profileid", user.getId()).apply();
//                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
//            }
//        });
//        holder.avatar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                context.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("profileid", user.getId()).apply();
//                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
//            }
//        });

        holder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(holder.btnFollow.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follows").child(mFirebaseUser.getUid())
                            .child("Following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follows").child(user.getId())
                            .child("Followers").child(mFirebaseUser.getUid()).setValue(true);

                    holder.btnFollow.setText("Following");
                }else {

                    FirebaseDatabase.getInstance().getReference().child("Follows").child(mFirebaseUser.getUid())
                            .child("Following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follows").child(user.getId())
                            .child("Followers").child(mFirebaseUser.getUid()).removeValue();

                    holder.btnFollow.setText("Follow");
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(isfragment){
//                    context.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("profileid", user.getId()).apply();
//                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
//                }else {
//                    Intent intent = new Intent(context, ActivityChinh.class);
//                    intent.putExtra("publisherId", user.getId());
//                    context.startActivity(intent);
//                }
//                context.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("profileid", user.getId()).apply();
//                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();

                Intent intent = new Intent(context, ActivityChinh.class);
                    intent.putExtra("publisherId", user.getId());
                    context.startActivity(intent);
            }
        });
//        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("Follows").child(mFirebaseUser.getUid())
//               ;
//        mReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.child(user.getId()).exists()){
//                    holder.btnFollow.setText("Following");
//                }else {
//                    holder.btnFollow.setText("Follow");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
       // isFollow(user.getId() , holder.btnFollow);

        isFollow(user.getId() , holder.btnFollow);
    }

    private void isFollow(String id, Button btnFollow) {
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("Follows").child(mFirebaseUser.getUid())
                .child("Following");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(id).exists()){
                    btnFollow.setText("Following");
                }else {
                    btnFollow.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView username , fullname;
        Button btnFollow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar= itemView.findViewById(R.id.image_profile);
            username= itemView.findViewById(R.id.username);
            fullname= itemView.findViewById(R.id.fullname);
            btnFollow= itemView.findViewById(R.id.btn_follow);
        }
    }
}
