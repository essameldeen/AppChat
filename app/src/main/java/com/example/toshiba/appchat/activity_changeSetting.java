package com.example.toshiba.appchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.toshiba.appchat.Model.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class activity_changeSetting extends AppCompatActivity implements View.OnClickListener {
       private android.support.v7.widget.Toolbar toolbar;
       private CircleImageView iv_profile;
       private EditText et_status;
       private Button bt_change;
       private DatabaseReference firestore;
       private FirebaseAuth auth;
       private UserData userData;
       private ProgressBar progressBar;
       private  String user_id;
       private  String imageDownload;
       private Uri image_uri;
       private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_setting);
        auth=FirebaseAuth.getInstance();
        mStorageRef= FirebaseStorage.getInstance().getReference();
        firestore=FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
        LoadData();
        //
        progressBar=(ProgressBar)findViewById(R.id.progrees_changeSetting);
        iv_profile=(CircleImageView)findViewById(R.id.iv_profileSettingChange);
        et_status=(EditText)findViewById(R.id.et_statusChange);
        bt_change=(Button) findViewById(R.id.bt_change);
        toolbar=(android.support.v7.widget.Toolbar) findViewById(R.id.my_toolbarSettingChange);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //
        iv_profile.setOnClickListener(this);



    }

    private void LoadData() {
         user_id=auth.getCurrentUser().getUid();
         firestore.addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 userData = dataSnapshot.getValue(UserData.class);
                 updateUi(userData);
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {
                 Toast.makeText(activity_changeSetting.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });

    }

    private void updateUi(UserData userData) {

        if(userData.getImage().equals("Default")){

        }else {
            Picasso.get().load(userData.getImage()).placeholder(R.drawable.profile).into(iv_profile);
        }
        et_status.setText(userData.getStatus());
        bt_change.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bt_change){
            progressBar.setVisibility(View.VISIBLE);
            String status = et_status.getText().toString();
            firestore.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){

                    }else {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(activity_changeSetting.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Map mapUpdate  =new HashMap<>();
            mapUpdate.put("image",userData.getImage());
            mapUpdate.put("image_thumb",userData.getImage_thumb());
            firestore.updateChildren(mapUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
                          Toast.makeText(activity_changeSetting.this, "Updated Successful ", Toast.LENGTH_SHORT).show();

                          progressBar.setVisibility(View.INVISIBLE);
                          bacToMainActivity();

                      }else {
                          progressBar.setVisibility(View.INVISIBLE);
                          Toast.makeText(activity_changeSetting.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                      }
                }
            });

        }else if(view.getId()==R.id.iv_profileSettingChange){

                     CropImage.activity()
                    .setAspectRatio(1,1)
                    .setMinCropResultSize(500,500)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);


        }


    }

    private void bacToMainActivity() {
        Intent backToSetting=new Intent(this,MainActivity.class);
        startActivity(backToSetting);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                image_uri = result.getUri();
                File imageThumb = new File(image_uri.getPath());
                try {
                    Bitmap compressedImageBitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(70)
                            .compressToBitmap(imageThumb);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                     byte[] dataa = baos.toByteArray();
                    loadImageUri(image_uri,dataa);
                    iv_profile.setImageURI(image_uri);

                } catch (IOException e) {
                    e.printStackTrace();
                }



                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void loadImageUri(Uri image_uri ,  byte[] dataa ) {
        String user_id=auth.getCurrentUser().getUid();
        StorageReference   pathsImage =mStorageRef.child("Images_profile").child(user_id+".jpg");
        StorageReference   pathsImagethumb =mStorageRef.child("Images_profile").child("thumbs").child(user_id+".jpg");
        pathsImage.putFile(image_uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                       if(task.isSuccessful()){
                           userData.setImage(task.getResult().getDownloadUrl().toString());
                           Toast.makeText(activity_changeSetting.this, "Image Added Successful", Toast.LENGTH_SHORT).show();
                       }else {
                           Toast.makeText(activity_changeSetting.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                       }
            }
        });

        UploadTask uploadTask = pathsImagethumb.putBytes(dataa);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    userData.setImage_thumb(task.getResult().getDownloadUrl().toString());
                }else {
                    Toast.makeText(activity_changeSetting.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
