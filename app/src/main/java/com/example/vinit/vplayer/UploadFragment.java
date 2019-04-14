package com.example.vinit.vplayer;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
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

public class UploadFragment extends Fragment {
    public UploadFragment() { }

    private RecyclerView video_row;
    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_upload, container, false);

        video_row = (RecyclerView)view.findViewById(R.id.video_row);
        video_row.setHasFixedSize(true);
        video_row.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Deleting Video...");
        progressDialog.setCanceledOnTouchOutside(false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        fun();
    }

    private void fun()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("uploaded_video").orderByChild("valid").equalTo("yes");

        FirebaseRecyclerAdapter<videokey,videoviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<videokey, videoviewholder>(
                videokey.class,
                R.layout.video_row,
                videoviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final videoviewholder viewHolder, final videokey model, final int position)
            {

                    viewHolder.set_video_row(model);

                    final String video_key = getRef(position).getKey();

                    viewHolder.option.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            PopupMenu popupMenu = new PopupMenu(getActivity(), viewHolder.option);

                            popupMenu.inflate(R.menu.upload_option_menu);

                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.delete:
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
                                                            Toast.makeText(getActivity(),"Video Deleted",Toast.LENGTH_SHORT).show();
                                                            progressDialog.dismiss();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e)
                                                        {
                                                            progressDialog.dismiss();
                                                            Toast.makeText(getActivity(),"Something wents wrong...Please try again.",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e)
                                                {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getActivity(),"Something wents wrong...Please try again.",Toast.LENGTH_SHORT).show();
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

                    viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(getActivity(), VideoPlayActivity.class);
                            i.putExtra("video_key", model.getVideo_key());
                            startActivity(i);
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

        public void set_video_row(final videokey model)
        {
            DatabaseReference mref = FirebaseDatabase.getInstance().getReference("Video").child(model.getVideo_key());

            mref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Video video = dataSnapshot.getValue(Video.class);
                    //Video video = new Video(dataSnapshot.child("title").getValue().toString(),dataSnapshot.child("description").getValue().toString(),dataSnapshot.child("user_id").getValue().toString(),Integer.valueOf(dataSnapshot.child("views").getValue().toString()),dataSnapshot.child("thumb_url").getValue().toString(),dataSnapshot.child("video_url").getValue().toString());
                    ImageView thumb = (ImageView)mview.findViewById(R.id.thumb);
                    TextView title = (TextView)mview.findViewById(R.id.title1);
                    final TextView user = (TextView)mview.findViewById(R.id.user_name);

                    Picasso.get().load(video.getThumb_url()).resize(320,200).centerCrop().into(thumb);
                    title.setText(video.getTitle());

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