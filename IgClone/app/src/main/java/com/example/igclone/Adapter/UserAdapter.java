package com.example.igclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.igclone.ChatActivity;
import com.example.igclone.Models.User;
import com.example.igclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Context context;
    List<User> listUser;
    FirebaseDatabase firebaseDatabase;

    public UserAdapter(Context context, List<User> listUser) {
        this.context = context;
        this.listUser = listUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_mess,parent,  false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    User user = listUser.get(position);
    holder.name.setText(user.getName());
    Picasso.get().load(user.getImageurl()).into(holder.avatar);
    if (user.getLastMessage()!=null){
        holder.lastMess.setText(user.getLastMessage());
    }
    holder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context , ChatActivity.class);
            intent.putExtra("name", user.getName());
            intent.putExtra("img", user.getImageurl());
            intent.putExtra("uid", user.getId());
            intent.putExtra("token", user.getToken());
            context.startActivity(intent);
        }
    });

    if (user.getId().equals(FirebaseAuth.getInstance().getUid())){
        holder.itemView.setVisibility(View.GONE);
        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
    }


    }

    @Override
    public int getItemCount() {
        return listUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView name , lastMess;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.image_profile);
            name = itemView.findViewById(R.id.username);
            lastMess = itemView.findViewById(R.id.lastmess);
        }
    }
}
