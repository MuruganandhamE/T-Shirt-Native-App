package com.muruga.t_shirt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AdminAddNewProductActivity extends AppCompatActivity
{

    private String categoryName,productName,productDescription,productPrice,currentDate,currentTime,productRandomKey,downloadImageUrl;
    private ImageView productImage;
    private EditText inputproductName, inputProductDescription, inputProductPrice;
    private Button addProduct;
    public static final int galleryPick=1;
    private Uri imageUri;
    private StorageReference productImageRef;
    private DatabaseReference productRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new_product);
        productImage=findViewById(R.id.select_product_image);
        inputproductName =findViewById(R.id.product_name);
        inputProductDescription =findViewById(R.id.product_description);
        inputProductPrice =findViewById(R.id.product_price);
        addProduct=findViewById(R.id.add_product);

        productImageRef=FirebaseStorage.getInstance().getReference().child("Product Images");
        productRef= FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar=new ProgressDialog(this);


        productImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openGallery();
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                validateProductData();
            }
        });

        categoryName=getIntent().getExtras().get("category").toString();

        Toast.makeText(this, categoryName, Toast.LENGTH_SHORT).show();
    }

    private void validateProductData()
    {
        productName=inputproductName.getText().toString();
        productDescription=inputProductDescription.getText().toString();
        productPrice=inputProductPrice.getText().toString();

        if(imageUri==null){
            Toast.makeText(this, "Product image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(productName==null)
        {
            Toast.makeText(this, "Product image is mandatory", Toast.LENGTH_SHORT).show();
        }else if(productDescription==null)
        {
            Toast.makeText(this, "Product description is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if(productPrice==null)
        {
            Toast.makeText(this, "Product price is mandatory", Toast.LENGTH_SHORT).show();
        }
        else
        {
            saveProductInformation();
        }
    }

    private void saveProductInformation()
    {
        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage("Dear Admin, Please Wait While We are Adding the new Product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat dateFormat=new SimpleDateFormat("MMM dd, yyyy");
        currentDate=dateFormat.format(calendar.getTime());

        SimpleDateFormat timeFormat=new SimpleDateFormat("HH:mm:ss a");
        currentTime=timeFormat.format(calendar.getTime());

        productRandomKey= currentDate + currentTime;

        final StorageReference filePath=productImageRef.child(imageUri.getLastPathSegment() + productRandomKey +".jpg");
        final UploadTask uploadTask=filePath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                loadingBar.dismiss();
                String message=e.toString();
                Toast.makeText(AdminAddNewProductActivity.this, "Error : "+message, Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                Toast.makeText(AdminAddNewProductActivity.this, "Product Image uploaded Sucessfully", Toast.LENGTH_SHORT).show();
                Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
                {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception
                    {
                        if(!task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            throw task.getException();
                        }
                        downloadImageUrl=filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task)
                    {
                        if(task.isSuccessful())
                        {
                            downloadImageUrl=task.getResult().toString();

                            Toast.makeText(AdminAddNewProductActivity.this, "got the Product image Url  Sucessfully", Toast.LENGTH_SHORT).show();

                             saveProductInfoToDatabase();
                        }

                    }
                });
            }
        });
    }

    private void saveProductInfoToDatabase()
    {
        HashMap<String,Object> productHashMap=new HashMap<>();
        productHashMap.put("pid",productRandomKey);
        productHashMap.put("date",currentDate);
        productHashMap.put("time",currentTime);
        productHashMap.put("image",downloadImageUrl);
        productHashMap.put("name",productName);
        productHashMap.put("description",productDescription);
        productHashMap.put("price",productPrice);

        productRef.child(productRandomKey).updateChildren(productHashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Intent intent=new Intent(AdminAddNewProductActivity.this,AdminCategoryActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added Sucessfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            loadingBar.dismiss();
                            String errMsg=task.getException().toString();
                            Toast.makeText(AdminAddNewProductActivity.this, "Error : "+errMsg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void openGallery()
    {
        Intent galleryIntent=new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,galleryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galleryPick && resultCode==RESULT_OK && data!=null)
        {
            imageUri=data.getData();
            productImage.setImageURI(imageUri);
        }
    }
}