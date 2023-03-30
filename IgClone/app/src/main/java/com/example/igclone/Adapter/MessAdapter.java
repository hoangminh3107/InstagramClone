package com.example.igclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.igclone.Models.Mess;
import com.example.igclone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessAdapter extends RecyclerView.Adapter {
    Context context;
    List<Mess> messList;

    final int ItemSend = 1;
    final int ItemRecive = 2;

    public MessAdapter(Context context, List<Mess> messList) {
        this.context = context;
        this.messList = messList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ItemSend) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
            return new SendViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recive, parent, false);
            return new ReciveViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        Mess mess = messList.get(position);
        if (FirebaseAuth.getInstance().getUid().equals(mess.getSenderId())) {
            return ItemSend;
        } else return ItemRecive;

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Mess mess = messList.get(position);
        if (holder.getClass() == SendViewHolder.class) {

            SendViewHolder sendViewHolder = (SendViewHolder) holder;
            if (mess.getMessage().equals("photo")) {
                ((SendViewHolder) holder).imgSender.setVisibility(View.VISIBLE);
                ((SendViewHolder) holder).send.setVisibility(View.GONE);
                Picasso.get().load(mess.getImgUrl()).into(sendViewHolder.imgSender);
            }
            if (mess.getImgUrl().equals("")){
                ((SendViewHolder) holder).imgSender.setVisibility(View.GONE);
                sendViewHolder.send.setText(mess.getMessage());
            }
            sendViewHolder.send.setText(mess.getMessage());


        } else {
            ReciveViewHolder reciveViewHolder = (ReciveViewHolder) holder;
            if (mess.getMessage().equals("photo")) {
                ((ReciveViewHolder) holder).imgReciver.setVisibility(View.VISIBLE);
                ((ReciveViewHolder) holder).recive.setVisibility(View.GONE);
                Picasso.get().load(mess.getImgUrl()).into(reciveViewHolder.imgReciver);
            }
            if (mess.getImgUrl().equals("")){
                ((ReciveViewHolder) holder).imgReciver.setVisibility(View.GONE);
                reciveViewHolder.recive.setText(mess.getMessage());
            }
            reciveViewHolder.recive.setText(mess.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messList.size();
    }

    public class SendViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgsend;
        TextView send;
        ImageView imgSender;

        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            send = itemView.findViewById(R.id.sendmess);
            imgSender = itemView.findViewById(R.id.imgSender);
        }
    }

    public class ReciveViewHolder extends RecyclerView.ViewHolder {
        TextView recive;
        CircleImageView imgrecive;
        ImageView imgReciver;

        public ReciveViewHolder(@NonNull View itemView) {
            super(itemView);
            recive = itemView.findViewById(R.id.recivemess);
            imgReciver = itemView.findViewById(R.id.imgReciever);

        }
    }
}
