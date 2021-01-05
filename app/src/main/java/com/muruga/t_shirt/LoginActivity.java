package com.muruga.t_shirt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muruga.t_shirt.model.User;
import com.muruga.t_shirt.prevalent.Prevalent;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity
{

    private EditText inputNumber, inputPassword;
    private Button login;
    private String number, password;
    private ProgressDialog loadingBar;
    private String parentDbName="Users";
    private CheckBox rememberMe;
    private TextView admin,notAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputNumber = findViewById(R.id.login_phoneNum);
        inputPassword = findViewById(R.id.login_password);
        login = findViewById(R.id.login_login_btn);
        rememberMe=findViewById(R.id.login_checkbox);
        admin=findViewById(R.id.admin_panel_link);
        notAdmin=findViewById(R.id.not_admin_panel_link);

        admin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login.setText("Admin Login");
                admin.setVisibility(View.INVISIBLE);
                notAdmin.setVisibility(View.VISIBLE);
                parentDbName="Admins";
            }
        });

        notAdmin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                login.setText("Login");
                admin.setVisibility(View.VISIBLE);
                notAdmin.setVisibility(View.INVISIBLE);
                parentDbName="Users";
            }
        });
        loadingBar=new ProgressDialog(this);

        login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                loginUser();
            }
        });
    }

    private void loginUser()
    {
        number = inputNumber.getText().toString();
        password = inputPassword.getText().toString();
        if(rememberMe.isChecked())
        {
            Paper.init(this);
            Paper.book().write(Prevalent.userPhoneKey,number);
            Paper.book().write(Prevalent.userPasswordKey,password);
        }
        if (TextUtils.isEmpty(number))
        {
            Toast.makeText(this, "Please Enter Your Number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Enter Your password", Toast.LENGTH_SHORT).show();
        } else
        {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please Wait,While We are checking the credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(number, password);
        }
    }

    private void allowAccessToAccount(final String numberInput, final String passwordInput)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                    if (snapshot.child(parentDbName).child(numberInput).exists())
                    {
                        User userData = snapshot.child(parentDbName).child(numberInput).getValue(User.class);
                        if (userData.getPhonenumber().equals(numberInput))
                        {
                            if (userData.getPassword().equals(passwordInput))
                            {
                                if(parentDbName.equalsIgnoreCase("Admins"))
                                {
                                    Toast.makeText(LoginActivity.this, "Logged in Sucessfully...", Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                    startActivity(intent);
                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, "Logged in Sucessfully...", Toast.LENGTH_LONG).show();
                                    loadingBar.dismiss();
                                    Intent intent = new Intent(LoginActivity.this, AdminAddNewProductActivity.class);
                                    startActivity(intent);
                                }
                            }
                            else
                            {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Password is incorrect", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else
                    {
                        Toast.makeText(LoginActivity.this, "Account With this " + numberInput + " number do not exists", Toast.LENGTH_SHORT).show();
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