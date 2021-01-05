package com.muruga.t_shirt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muruga.t_shirt.model.User;
import com.muruga.t_shirt.prevalent.Prevalent;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity
{
    private ProgressDialog loadingBar;
    String number,password;
    Button sigup, login;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadingBar=new ProgressDialog(this);

        Paper.init(this);
        number= Paper.book().read(Prevalent.userPhoneKey);
        password=Paper.book().read(Prevalent.userPasswordKey);

        if(number!="" && !TextUtils.isEmpty(number) && password!="" && !TextUtils.isEmpty(password))
        {
            allowAccess(number,password);

            loadingBar.setTitle("Already Login");
            loadingBar.setMessage("Please Wait");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
        }

        sigup = findViewById(R.id.main_signup);
        login = findViewById(R.id.main_login);

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        sigup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent loginIntent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    private void allowAccess(final String number, final String password)
    {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child("Users").child(number).exists())
                {
                    User userData = snapshot.child("Users").child(number).getValue(User.class);
                    if (userData.getPhonenumber().equals(number))
                    {
                        if (userData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Logged in Sucessfully...", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                            Intent intent = new Intent(MainActivity.this, AdminCategoryActivity.class);
                            startActivity(intent);
                        }
                    }
                } else
                {
                    Toast.makeText(MainActivity.this, "Account With this " + number + " number do not exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

}