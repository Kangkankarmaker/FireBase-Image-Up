package com.kangkan.example.firebaseimgup.firebaseimgup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.provider.MediaStore;
import java.util.UUID;

import java.io.FileNotFoundException;
import java.io.IOException;
import android.support.annotation.NonNull;


public class MainActivity extends AppCompatActivity {

    Button button1,button2;
    ImageView imageView;

    private Uri filePath;
    private final int PICK_IMAGE_REQUEST=71;

    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1=findViewById(R.id.btn_img_select);
        button2=findViewById(R.id.btn_img_update);
        imageView=findViewById(R.id.img_view);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                choseImage();

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();

            }
        });
    }



        private void uploadImage(){
            if(filePath != null)
            {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
                ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }

                }).addOnFailureListener(new OnFailureListener() {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                    @Override

                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
            }
        }



    private void choseImage() {

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) { super.onActivityResult(requestCode, resultCode, data);
    if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null )
    {
        filePath = data.getData();

            try {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            }catch (IOException e) {
                e.printStackTrace();
            }

    }
    }


}
