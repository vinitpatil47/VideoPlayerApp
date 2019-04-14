package com.example.vinit.vplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.EventListener;

public class VideosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String work;
    private RecyclerView video_row;
    private ScrollView edit;
    private TextView first_name,last_name;
    private ImageView edit_first,edit_last;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private StorageReference storageReference;
    private LinearLayoutManager linearLayoutManager;
    private ValueEventListener event;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        work = getIntent().getStringExtra("work");

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(work);

        video_row = (RecyclerView)findViewById(R.id.video_row);
        video_row.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        video_row.setLayoutManager(linearLayoutManager);

        first_name = (TextView)findViewById(R.id.first_name);
        last_name = (TextView)findViewById(R.id.last_name);
        edit_first = (ImageView)findViewById(R.id.edit_first);
        edit_last = (ImageView)findViewById(R.id.edit_last);
        edit = (ScrollView)findViewById(R.id.edit_profile);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Deleting Video...");
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference("User").child(mAuth.getCurrentUser().getUid());

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                first_name.setText(dataSnapshot.child("first_name").getValue().toString());
                last_name.setText(dataSnapshot.child("last_name").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(work.equals("Setting"))
        {
            edit.setVisibility(View.VISIBLE);
            video_row.setVisibility(View.GONE);
        }
        else {
            edit.setVisibility(View.GONE);
            video_row.setVisibility(View.VISIBLE);
        }

        edit_first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog();
            }
        });

        edit_last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                opendialog();
            }
        });


    }

    private void opendialog()
    {
        alertbox alert = new alertbox();
        alert.show(getSupportFragmentManager(),"alert dialog");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!work.equals("Setting"))
            fun();
    }

    private void fun()
    {
        Query query;
        if(work.equals("History"))
            query = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("history");
        else if(work.equals("Watch Later"))
            query = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("watch_later");
        else if(work.equals("Liked Videos"))
            query = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("liked_video");
        else
            query = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("uploaded_video");

        FirebaseRecyclerAdapter<videokey,videoviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<videokey, videoviewholder>(
                videokey.class,
                R.layout.video_single_row,
                videoviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final videoviewholder viewHolder, final videokey model, final int position)
            {
                viewHolder.set_video_row(model);
                final String video_key1 = getRef(position).getKey();

                if(work.equals("History")) {

                    viewHolder.option.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            PopupMenu popupMenu = new PopupMenu(VideosActivity.this, viewHolder.option);

                            popupMenu.inflate(R.menu.history_option_menu);

                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.watch_later:
                                            mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("watch_later").push();
                                            mref.child("video_key").setValue(model.getVideo_key());
                                            Toast.makeText(VideosActivity.this, "Added to Watch later", Toast.LENGTH_SHORT).show();
                                            return true;

                                        case R.id.remove:
                                            mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("history").child(video_key1);
                                            mref.removeValue();
                                            return true;

                                        default:
                                            return false;
                                    }

                                }
                            });

                            popupMenu.show();

                        }
                    });
                }
                else if(work.equals("Watch Later"))
                {
                    viewHolder.option.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            PopupMenu popupMenu = new PopupMenu(VideosActivity.this, viewHolder.option);

                            popupMenu.inflate(R.menu.watch_later_option_menu);

                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.remove:
                                            mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("watch_later").child(video_key1);
                                            mref.removeValue();
                                            return true;

                                        default:
                                            return false;
                                    }

                                }
                            });

                            popupMenu.show();

                        }
                    });
                }
                else if(work.equals("Liked Videos"))
                {
                    viewHolder.option.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            PopupMenu popupMenu = new PopupMenu(VideosActivity.this, viewHolder.option);

                            popupMenu.inflate(R.menu.liked_video_option_list);

                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.watch_later:
                                            mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("watch_later").push();
                                            mref.child("video_key").setValue(model.getVideo_key());
                                            Toast.makeText(VideosActivity.this, "Added to Watch later", Toast.LENGTH_SHORT).show();
                                            return true;

                                        case R.id.remove:
                                            mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("liked_video").child(video_key1);
                                            mref.removeValue();
                                            return true;

                                        default:
                                            return false;
                                    }

                                }
                            });

                            popupMenu.show();

                        }
                    });
                }
                else
                {
                    viewHolder.option.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            PopupMenu popupMenu = new PopupMenu(VideosActivity.this, viewHolder.option);

                            popupMenu.inflate(R.menu.upload_option_menu);

                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.delete:
                                            progressDialog.show();
                                            mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("uploaded_video");
                                            mref.child(getRef(position).getKey()).child("valid").setValue("no");

                                            mref = FirebaseDatabase.getInstance().getReference("Video").child(model.getVideo_key());
                                            mref.child("valid").setValue("no");
                                            mref.child("title").setValue("These video is no longer available");

                                            storageReference = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid()).child("Uploaded Video").child(model.getVideo_key() + ".mp4");
                                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid)
                                                {
                                                    storageReference = FirebaseStorage.getInstance().getReference(mAuth.getCurrentUser().getUid()).child("Thumbnail").child(model.getVideo_key() + ".jpg");
                                                    storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid)
                                                        {
                                                            Toast.makeText(VideosActivity.this,"Video Deleted",Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e)
                                                        {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(VideosActivity.this,"Something wents wrong...Please try again.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e)
                                                {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(VideosActivity.this,"Something wents wrong...Please try again.",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            return true;

                                        default:
                                            return false;
                                    }

                                }
                            });

                            popupMenu.show();

                        }
                    });
                }

                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Video").child(model.getVideo_key());
                        event = ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.child("valid").getValue().equals("yes")) {

                                    Intent i = new Intent(VideosActivity.this, VideoPlayActivity.class);
                                    i.putExtra("video_key", model.getVideo_key());
                                    startActivity(i);
                                }
                                else
                                    Toast.makeText(VideosActivity.this,"Video is not available",Toast.LENGTH_SHORT).show();
                                ref.removeEventListener(event);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }


        };
        video_row.setAdapter(firebaseRecyclerAdapter);
    }

    private static class videoviewholder extends RecyclerView.ViewHolder
    {

        View mview;
        TextView option;

        public videoviewholder(@NonNull View itemView) {
            super(itemView);
            mview = itemView;
            option = (TextView)mview.findViewById(R.id.option);
        }

        public void set_video_row(videokey model)
        {
            DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Video").child(model.getVideo_key());

            mref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Video video = dataSnapshot.getValue(Video.class);
                    ImageView thumb = (ImageView)mview.findViewById(R.id.thumb);
                    TextView view1 = (TextView) mview.findViewById(R.id.view);
                    TextView title = (TextView)mview.findViewById(R.id.title1);
                    final TextView user = (TextView)mview.findViewById(R.id.user_name);

                    if(video.getValid().equals("yes"))
                        Picasso.get().load(video.getThumb_url()).resize(150,100).centerCrop().into(thumb);
                    else
                        thumb.setImageResource(R.drawable.ic_screen_lock_landscape_black_24dp);
                    title.setText(video.getTitle());
                    view1.setText(Integer.toString(video.getViews()));

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(video.getUser_uid());
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            user.setText(dataSnapshot.child("first_name").getValue().toString() + " " +  dataSnapshot.child("last_name").getValue().toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
