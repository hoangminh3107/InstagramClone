package com.example.igclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.igclone.Adapter.CommentAdapter;
import com.example.igclone.Models.Comment;
import com.example.igclone.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentActivity extends AppCompatActivity {

    EditText addcoment;
    TextView post;
    CircleImageView avatar;
    RecyclerView recyclerViewComment;

    String postId , authorId, token, name , postDes;
    FirebaseUser mFirebaseUser ;

    List<Comment> list;
    CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        list = new ArrayList<>();
        adapter = new CommentAdapter(this, list);

        recyclerViewComment = findViewById(R.id.recycler_view_comment);
        recyclerViewComment.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewComment.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addcoment= findViewById(R.id.comment);
        post = findViewById(R.id.post);
        avatar = findViewById(R.id.image_profile);


        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authorId = intent.getStringExtra("authorId");
        token = intent.getStringExtra("token");
        name = intent.getStringExtra("name");
        postDes = intent.getStringExtra("postDes");

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        getImageUser();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addcoment.getText().toString().isEmpty()){
                    Toast.makeText(CommentActivity.this, "No comment added ", Toast.LENGTH_SHORT).show();
                }else {
                    putComment();
                }
            }
        });

        // ham
        readComment();

    }

    private void readComment() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    list.add(comment);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void putComment() {

        HashMap<String , Object> map = new HashMap<>();
        map.put("comments", addcoment.getText().toString());
        map.put("publisher", mFirebaseUser.getUid());

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CommentActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                    addcoment.setText("");
                    sendNotifications(name , "Comment your post :  "+ postDes , token);
                }else {
                    Toast.makeText(CommentActivity.this, "No comment added", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void getImageUser() {
        FirebaseDatabase.getInstance().getReference("Users").child(mFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);
                    Picasso.get().load(user.getImageurl()).into(avatar);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    void sendNotifications(String name , String mes , String token){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        try {
            String url = "https://fcm.googleapis.com/fcm/send";

            JSONObject jsonObject = new JSONObject();

            jsonObject.put("title", name);
            jsonObject.put("body" , mes);

            JSONObject data = new JSONObject();
            data.put("notification" , jsonObject);
            data.put("to" , token);

            JsonObjectRequest request = new JsonObjectRequest( url ,data, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(CommentActivity.this, "success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(CommentActivity.this,"loi", Toast.LENGTH_SHORT).show();
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
}