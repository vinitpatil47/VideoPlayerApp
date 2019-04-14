package com.example.vinit.vplayer;


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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class trendingFragment extends Fragment {
    public trendingFragment() { }

    private RecyclerView video_row;
    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trending, container, false);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        video_row = (RecyclerView)view.findViewById(R.id.video_row);
        video_row.setHasFixedSize(true);
        video_row.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        trending_fun();
    }

    private void trending_fun()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Video").orderByChild("views");

        FirebaseRecyclerAdapter<Video,trendingFragment.videoviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Video, trendingFragment.videoviewholder>(
                Video.class,
                R.layout.video_row,
                trendingFragment.videoviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final trendingFragment.videoviewholder viewHolder, final Video model, int position) {

                viewHolder.set_video_row(model);
                final String video_key = getRef(position).getKey();

                viewHolder.option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu = new PopupMenu(getActivity(),viewHolder.option);

                        popupMenu.inflate(R.menu.option_trending_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item)
                            {
                                switch (item.getItemId())
                                {
                                    case R.id.watch_later:
                                        mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("watch_later").push();
                                        mref.child("video_key").setValue(video_key);
                                        Toast.makeText(getActivity(),"Added to Watch later",Toast.LENGTH_SHORT).show();
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

                        if(model.getValid().equals("yes")) {
                            Intent i = new Intent(getActivity(), VideoPlayActivity.class);
                            i.putExtra("video_key", video_key);
                            startActivity(i);
                        }
                        else
                            Toast.makeText(getActivity(),"Video is not available",Toast.LENGTH_SHORT).show();
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

        public void set_video_row(Video model)
        {
            ImageView thumb = (ImageView)mview.findViewById(R.id.thumb);
            ImageView profile = (ImageView)mview.findViewById(R.id.profileimage);
            TextView title = (TextView)mview.findViewById(R.id.title1);
            final TextView user = (TextView)mview.findViewById(R.id.user_name);

            if(model.getValid().equals("yes"))
                Picasso.get().load(model.getThumb_url()).resize(320,200).centerCrop().into(thumb);
            else
                thumb.setImageResource(R.drawable.ic_screen_lock_landscape_black_24dp);
            profile.setImageResource(R.drawable.ic_account_circle_black_24dp);
            title.setText(model.getTitle());

            DatabaseReference mref = FirebaseDatabase.getInstance().getReference("User").child(model.getUser_uid());
            mref.addValueEventListener(new ValueEventListener() {
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
    }

}
