package com.example.igclone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.igclone.CommentActivity;
import com.example.igclone.FollowerActivity;
import com.example.igclone.Models.Post;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    Context context;
    List<Post> list;
    FirebaseUser mFirebaseUser;
    String token;
    String name;

    public PostAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = list.get(position);
        Picasso.get().load(post.getImageurl()).into(holder.postImg);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myReference = database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
        myReference.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = (String) snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Picasso.get().load(user.getImageurl()).into(holder.avatar);
                holder.username.setText(user.getUsername());
                holder.tokentv.setText(user.getToken());
                token = user.getName();
                holder.author.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.description.setText(post.getDescription());


        // anh xa ham
        isLike(post.getPostid(), holder.like);
        isPost(post.getPostid(), holder.save);
        numberOfLike(post.getPostid(), holder.no_of_like);
        numberOfComment(post.getPostid(), holder.comment);





        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(mFirebaseUser.getUid())
                            .setValue(true);
                    addNoti(post.getPostid() , post.getPublisher());
                    sendNotifications(name, "Like your post : "+ post.getDescription(),post.getImageurl(), holder.tokentv.getText().toString());
                    Log.e("a", holder.tokentv.getText().toString());
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid()).child(mFirebaseUser.getUid())
                            .removeValue();
                }

            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , CommentActivity.class);
                intent.putExtra("postId" , post.getPostid());
                intent.putExtra("authorId", post.getPublisher());
                intent.putExtra("token", holder.tokentv.getText().toString());
                intent.putExtra("name", name);
                intent.putExtra("postDes", post.getDescription());
                context.startActivity(intent);
            }
        });
        holder.commentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , CommentActivity.class);
                intent.putExtra("postId" , post.getPostid());
                intent.putExtra("authorId", post.getPublisher());
                intent.putExtra("token", holder.tokentv.getText().toString());
                intent.putExtra("name", name);
                intent.putExtra("postDes", post.getDescription());
                context.startActivity(intent);
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(mFirebaseUser.getUid()).child(post.getPostid())
                            .setValue(true);
                    Toast.makeText(context, "Save success ", Toast.LENGTH_SHORT).show();
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(mFirebaseUser.getUid()).child(post.getPostid())
                            .removeValue();
                }
            }
        });

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("profileid", post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        holder.author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("profileid", post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getSharedPreferences("Profile",Context.MODE_PRIVATE).edit().putString("profileid", post.getPublisher()).apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
        holder.no_of_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , FollowerActivity.class);
                intent.putExtra("id",post.getPublisher());
                intent.putExtra("postId" , post.getPostid());
                intent.putExtra("titel","like");
                context.startActivity(intent);
            }
        });

//        holder.postImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit().putString("postid", post.getPostid()).apply();
//
//                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_container, new PostDetailFragment()).commit();
//            }
//        });

    }

    private void addNoti(String postid, String publisher) {
        HashMap<String , Object> map = new HashMap<>();

        map.put("userid", publisher);
        map.put("text", "Liked your post");
        map.put("postid",postid);
        map.put("isPost", true);

//        String push =
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(mFirebaseUser.getUid()).push().setValue(map);
    }

    private void isPost(String postid, ImageView img) {
        FirebaseDatabase.getInstance().getReference().child("Saves").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(postid).exists()){
                    img.setImageResource(R.drawable.ic_saved);
                    img.setTag("saved");
                }else {
                    img.setImageResource(R.drawable.ic_save);
                    img.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    void sendNotifications(String name , String mes , String urlImg , String token){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        try {
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("title", name);
            jsonObject.put("body" , mes);


            JSONObject data = new JSONObject();

            data.put("notification" , jsonObject);
            data.put("icon" , urlImg);
            data.put("to" , token);


            JsonObjectRequest request = new JsonObjectRequest( url ,data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context,"loi", Toast.LENGTH_SHORT).show();
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String , String> map = new HashMap<>();

                    String key= "Key=AAAALaC2eiA:APA91bEYFXNBrH0RK05iqJDG0W-eY_pLN1snqK4ICrYVWEtkb6XUzjloJ5vEZdTNigI7KTN_T6_-Qe-LXH4U6zJ-8j7ouJfND3RZPsLyi59XhAiu5P8NNjY_d5VhBSqF-R2N3iRWLgIK";
                    map.put("Content-Type", "application/json");
                    map.put("Authorization", key);

                    return map;
                }
            };

            requestQueue.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView avatar;
        TextView username , no_of_like , author , description , comment, tokentv;
        ImageView postImg , like , commentImg , save;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            no_of_like = itemView.findViewById(R.id.no_of_like);
            author = itemView.findViewById(R.id.text_author);
            description = itemView.findViewById(R.id.text_description);
            comment = itemView.findViewById(R.id.no_of_comment);
            postImg = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            commentImg = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            tokentv= itemView.findViewById(R.id.tokenn);
        }
    }
    private void isLike(String postid , ImageView img){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(mFirebaseUser.getUid()).exists()){
                    img.setImageResource(R.drawable.ic_heart);
                    img.setTag("liked");
                }else {
                    img.setImageResource(R.drawable.ic_like);
                    img.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void numberOfLike(String postid , TextView like){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                like.setText(snapshot.getChildrenCount()+ " Likes");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void numberOfComment(String postid , TextView comment){
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comment.setText(snapshot.getChildrenCount()+ " Commets");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
