package com.example.vinit.vplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Button record,upload;
    private EditText title,description;
    private final static int GALLERY = 2;
    private Uri uri1,uri2;
    private String video_key,k;
    private ImageView thumb;
    private ImageButton select;
    private TextView test;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Details");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference("Video");
        video_key = mref.push().getKey();

        upload = (Button)findViewById(R.id.upload);
        record = (Button)findViewById(R.id.tap);
        title = (EditText)findViewById(R.id.title1);
        description = (EditText)findViewById(R.id.description);
        test = (TextView)findViewById(R.id.test);
        thumb = (ImageView)findViewById(R.id.thumb);
        select = (ImageButton)findViewById(R.id.thumbutton);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading Video...");
        progressDialog.setCanceledOnTouchOutside(false);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(i,GALLERY);
                k = "record";
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/");
                startActivityForResult(i,GALLERY);
                k = "thumb";
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                upload_video();
            }
        });

    }

    private void upload_video() {
        if (uri1!=null && uri2!=null && validate()) {
            final StorageReference storage = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid()).child("Uploaded Video").child(video_key + ".mp4");
            final UploadTask uploadTask = storage.putFile(uri1);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storage.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        final Uri downloadUri1 = task.getResult();

                        final StorageReference storage1 = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid()).child("Thumbnail").child(video_key + ".jpg");
                        UploadTask uploadTask1 = storage1.putFile(uri2);

                        uploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return storage1.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        final Uri downloadUri2 = task.getResult();

                                        Video video = new Video(title.getText().toString(),description.getText().toString(),mAuth.getCurrentUser().getUid(),0,downloadUri2.toString(),downloadUri1.toString(),"yes");
                                        mref.child(video_key).setValue(video);

                                        mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("uploaded_video").push();
                                        mref.child("video_key").setValue(video_key);
                                        mref.child("valid").setValue("yes");

                                        mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("liked_video").push();
                                        mref.child("video_key").setValue(video_key);

                                        progressDialog.dismiss();
                                        Toast.makeText(UploadActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(UploadActivity.this, ProfileActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(i);
                                        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                                    }
                                    else {
                                        progressDialog.dismiss();
                                        Toast.makeText(UploadActivity.this, "Unable to upload video...Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                            }
                        });

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(UploadActivity.this, "Unable to upload video...Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            progressDialog.dismiss();
        }
    }

    private boolean validate()
    {
        boolean val = true;
        if(TextUtils.isEmpty(title.getText().toString()))
        {
            title.setError("Invalid Title");
            val = false;
        }
        if(TextUtils.isEmpty(description.getText().toString()))
        {
            description.setError("Invalid description");
            val = false;
        }
        return val;
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY && resultCode == RESULT_OK && data!=null)
        {
            if(k.equals("record"))
                uri1 = data.getData();
            else {
                uri2 = data.getData();
                Picasso.get().load(uri2).resize(300,200).centerCrop().into(thumb);
                test.setVisibility(View.GONE);
            }
        }

    }
}
