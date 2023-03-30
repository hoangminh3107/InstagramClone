package com.example.igclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText edtUsername , edtName , edtEmail , edtPass;
    Button register;
    TextView tvLogin;
    DatabaseReference mReference;
    FirebaseAuth mAuth;
    ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.username);
        edtName = findViewById(R.id.name);
        edtEmail = findViewById(R.id.email);
        edtPass = findViewById(R.id.password);
        tvLogin = findViewById(R.id.login_user);
        register = findViewById(R.id.registerbtn);
        mDialog= new ProgressDialog(this);

        mReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this , LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.show();
                String username = edtUsername.getText().toString();
                String name = edtName.getText().toString();
                String email = edtEmail.getText().toString();
                String password = edtPass.getText().toString();
                if(username.isEmpty()|| name.isEmpty()|| email.isEmpty()|| password.isEmpty())
                {
                    Toast.makeText(RegisterActivity.this, "Not Empty", Toast.LENGTH_SHORT).show();
                }else if (password.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password to short", Toast.LENGTH_SHORT).show();
                }else
                {
                    register(username , name , email , password);

                }
            }
        });



    }

    private void register(String username, String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email , password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String , Object> hashMap = new HashMap<>();
                hashMap.put("name" , name);
                hashMap.put("username" , username);
                hashMap.put("email" , email);
                hashMap.put("bio" , "");
                hashMap.put("imageurl" , "default");
                hashMap.put("id" , mAuth.getCurrentUser().getUid());

                mReference.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(RegisterActivity.this, "Update to profile ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this  , ActivityChinh.class));
                        finish();
                        mDialog.dismiss();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDialog.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}