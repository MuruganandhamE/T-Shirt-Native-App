package com.muruga.t_shirt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity
{

    private Button createBtn;
    private EditText inputName, inputNumber, inputPassword;
    private String name, number, password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        inputName = findViewById(R.id.register_name);
        inputNumber = findViewById(R.id.register_phoneNum);
        inputPassword = findViewById(R.id.register_password);
        createBtn = findViewById(R.id.register_create_btn);
        progressDialog=new ProgressDialog(this);

        createBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                createAccount();
            }
        });
    }

    private void createAccount()
    {
        name = inputName.getText().toString();
        number = inputNumber.getText().toString();
        password = inputPassword.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please Enter your name", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(number)){
            Toast.makeText(this, "Please Enter your number", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter your password", Toast.LENGTH_SHORT).show();
        }else{
            progressDialog.setTitle("Create Account");
            progressDialog.setMessage("Please Wait,Creating Your Account");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            validatePhoneNumber(name,number,password);
        }


//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference userRef=database.getReference();
//        long id=System.currentTimeMillis();
//        User user=new User(id,name,number,password);
//        userRef=database.getReference().child("User");
//        userRef.child(number).setValue(user);
    }

    private void validatePhoneNumber(final String name, final String number, final String password)
    {
        final DatabaseReference rootRef;
        rootRef=FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(!(snapshot.child("Users").child(number).exists()))
                {
                    HashMap<String,Object> userHaspMap=new HashMap<>();
                    userHaspMap.put("phonenumber",number);
                    userHaspMap.put("password",password);
                    userHaspMap.put("name",name);

                    rootRef.child("Users").child(number).updateChildren(userHaspMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                   if(task.isSuccessful())
                                    {
                                        Toast.makeText(RegisterActivity.this, "Congratulations, Your Account has been Created.", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                    }
                                   else
                                   {
                                       progressDialog.dismiss();
                                       Toast.makeText(RegisterActivity.this, "Network Error:Please Try Again after some time...", Toast.LENGTH_SHORT).show();
                                   }
                                }
                            });
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "This "+number+ " already exists", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

    }
}