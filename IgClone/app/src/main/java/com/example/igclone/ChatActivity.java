package com.example.igclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.igclone.Adapter.MessAdapter;
import com.example.igclone.Models.Mess;
import com.example.igclone.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    List<Mess> messList;
    MessAdapter messAdapter;
    RecyclerView recyclerView;
    EditText edtmess;
    TextView send;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;

    CircleImageView imgAvatar;
    ImageView cam;

    String senderRoom, reciverRoom;
    String sendertUid, reciverUid;
    String avatar ="" ;
    ProgressDialog dialog;
    String token;
    String name;
    String senderName;
    View activityRoot;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.recycler_view_chat);
        edtmess = findViewById(R.id.chat);
        send = findViewById(R.id.send);
        imgAvatar = findViewById(R.id.image_profile);
        activityRoot = findViewById(R.id.activityRoot);
        cam = findViewById(R.id.imgCam);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Uploading ...");
        user = new User();

         name = getIntent().getStringExtra("name");
         reciverUid = getIntent().getStringExtra("uid");
         token = getIntent().getStringExtra("token");



        sendertUid = FirebaseAuth.getInstance().getUid();
        senderRoom = sendertUid + reciverUid;
        reciverRoom = reciverUid + sendertUid;

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        messList = new ArrayList<>();
        messAdapter = new MessAdapter(this, messList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messAdapter);

        Toolbar toolbar = findViewById(R.id.bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        firebaseDatabase.getReference().child("Chats").child(senderRoom).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Mess mess = dataSnapshot.getValue(Mess.class);
                            messList.add(mess);
                        }
                        messAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        edtmess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkKey();
            }
        });

        getAvatar();
        SendMess();
        SendImage();
        if (messList.size() > 0){
            recyclerView.scrollToPosition(messList.size() - 1);

        }


    }

    private void checkKey() {

        activityRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();

                activityRoot.getWindowVisibleDisplayFrame(r);

                int heghtDreff = activityRoot.getRootView().getHeight() - r.height();
                if (heghtDreff > 0.25 * activityRoot.getRootView().getHeight() ){
                    if (messList.size() > 0){
                        recyclerView.scrollToPosition(messList.size() - 1);
                        activityRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
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
                    Toast.makeText(ChatActivity.this, "success", Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(ChatActivity.this,"loi", Toast.LENGTH_SHORT).show();
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

    private void SendImage() {
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,25);

            }
        });
    }

    private void getAvatar() {
        DatabaseReference myReference = firebaseDatabase.getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
        myReference.child("imageurl")
               .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       avatar = (String) snapshot.getValue();
                       Picasso.get().load(avatar).into(imgAvatar);
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
        myReference.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderName = (String) snapshot.getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void SendMess() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtMess = edtmess.getText().toString();
                Mess mess = new Mess("", sendertUid, txtMess, avatar ,"" );
                if (txtMess.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Not Null ! ", Toast.LENGTH_SHORT).show();
                    return;
                }
                firebaseDatabase.getReference().child("Chats")
                        .child(senderRoom).child("Messages").push().setValue(mess).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        firebaseDatabase.getReference().child("Chats")
                                .child(reciverRoom).child("Messages").push().setValue(mess).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                sendNotifications(senderName , mess.getMessage() , token);
                                HashMap<String , Object> obj = new HashMap<>();
                                obj.put("time" , System.currentTimeMillis());
                                obj.put("lastMessage" , txtMess);
                                firebaseDatabase.getReference().child("Users").child(reciverUid)
                                        .updateChildren(obj);
                                edtmess.setText("");
                            }
                        });
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==25){
            if (data != null){
                if (data.getData() != null){
                    dialog.show();
                    Uri imgSelect = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = firebaseStorage.getReference().child("chats").child(calendar.getTimeInMillis()+"");
                    reference.putFile(imgSelect).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            dialog.dismiss();

                            if (task.isSuccessful()){
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();
                                        Mess mess = new Mess("", sendertUid, "photo", avatar ,filePath );

                                        firebaseDatabase.getReference().child("Chats")
                                                .child(senderRoom).child("Messages").push().setValue(mess).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                firebaseDatabase.getReference().child("Chats")
                                                        .child(reciverRoom).child("Messages").push().setValue(mess).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        edtmess.setText("");
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });


                }
            }
        }
    }
}