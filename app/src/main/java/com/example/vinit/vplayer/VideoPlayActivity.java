package com.example.vinit.vplayer;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class VideoPlayActivity extends AppCompatActivity {

    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView exoPlayerView;
    private MediaSource mediaSource;
    private String url,liked = "no";
    private String video_key,video_push_key=null;
    private TextView title,view,name,description;
    private ImageView like,dislike;
    private RecyclerView video_row;
    private ValueEventListener event = null;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        video_key = getIntent().getStringExtra("video_key");

        exoPlayerView = (SimpleExoPlayerView)findViewById(R.id.exoplayerview);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this,trackSelector);

        title = (TextView)findViewById(R.id.title1);
        view = (TextView)findViewById(R.id.view);
        name = (TextView)findViewById(R.id.user_name);
        description = (TextView)findViewById(R.id.description);
        like = (ImageView)findViewById(R.id.like);
        dislike = (ImageView)findViewById(R.id.dislike);

        video_row = (RecyclerView)findViewById(R.id.video_row);
        video_row.setLayoutManager(new LinearLayoutManager(this));
        video_row.setHasFixedSize(true);
        video_row.setNestedScrollingEnabled(false);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference("Video").child(video_key);

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Video video = dataSnapshot.getValue(Video.class);
                title.setText(video.getTitle());
                view.setText(Integer.toString(video.getViews()));
                description.setText(video.getDescription());

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(mAuth.getCurrentUser().getUid());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        name.setText(dataSnapshot.child("first_name").getValue().toString() + " " + dataSnapshot.child("last_name").getValue().toString());
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

        function();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Video").child(video_key);

        event = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                int i = dataSnapshot.child("views").getValue(Integer.class);
                i++;
                mref = FirebaseDatabase.getInstance().getReference("Video").child(video_key);
                mref.child("views").setValue(i);
                url = dataSnapshot.child("video_url").getValue(String.class);
                //url = "https://firebasestorage.googleapis.com/v0/b/vplayer-40454.appspot.com/o/VID-20190324-WA0001%5B1%5D.mp4?alt=media&token=7f7a64f7-21ca-4794-9d98-c9b8008e8f60";
                uri = Uri.parse(url);

                DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory("xyz");
                ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

                mediaSource = new ExtractorMediaSource(uri,defaultHttpDataSourceFactory,extractorsFactory,null,null);

                exoPlayerView.setPlayer(exoPlayer);

                exoPlayer.addListener(new ExoPlayer.EventListener() {
                    @Override
                    public void onTimelineChanged(Timeline timeline, Object manifest) {

                    }

                    @Override
                    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                    }

                    @Override
                    public void onLoadingChanged(boolean isLoading) {

                        if(isLoading) {
                            progressBar.setVisibility(View.VISIBLE);
                            //Toast.makeText(VideoPlayActivity.this, "Loading", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                        if (playWhenReady && playbackState == ExoPlayer.STATE_READY)
                        {
                            progressBar.setVisibility(View.GONE);
                            //Toast.makeText(VideoPlayActivity.this,"STATE_READY",Toast.LENGTH_SHORT).show();
                        }
                        else if(playWhenReady && playbackState == ExoPlayer.STATE_BUFFERING)
                            progressBar.setVisibility(View.VISIBLE);
                       if (playbackState == ExoPlayer.STATE_IDLE || playbackState == ExoPlayer.STATE_ENDED || !playWhenReady)
                            exoPlayerView.setKeepScreenOn(false);
                       else
                           exoPlayerView.setKeepScreenOn(true);
                    }

                    @Override
                    public void onPlayerError(ExoPlaybackException error) {

                    }

                    @Override
                    public void onPositionDiscontinuity() {

                    }

                    @Override
                    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                    }
                });

                exoPlayer.prepare(mediaSource);
                exoPlayer.setPlayWhenReady(true);

                reference.removeEventListener(event);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("history").push();
        mref.child("video_key").setValue(video_key);

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function();
                if(liked.equals("no"))
                    setlike();
                /*else
                    setdislike();*/

            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                function();
                if(liked.equals("yes"))
                    setdislike();
                /*else
                    setdislike();*/
            }
        });

    }

    private void setdislike()
    {
        //like.setImageResource(R.drawable.ic_thumb_up_black_24dp);
        liked = "no";
        mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("liked_video");
        mref.child(video_push_key).removeValue();

    }

    private void setlike()
    {
        //like.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
        mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("liked_video").push();
        mref.child("video_key").setValue(video_key);
    }

    private void function()
    {
        mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("liked_video");
        event = mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    if(snapshot.child("video_key").getValue().equals(video_key))
                    {
                        video_push_key = snapshot.getKey();
                        liked = "yes";
                        break;
                    }
                }
                mref.removeEventListener(event);
                if(liked.equals("yes")) {
                    like.setImageResource(R.drawable.ic_thumb_up_blue_24dp);
                    dislike.setImageResource(R.drawable.ic_thumb_down_black_24dp);
                }
                else {
                    like.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                    dislike.setImageResource(R.drawable.ic_thumb_down_blue_24dp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fun();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fun();
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.stop();
    }

    private void fun()
    {
        Query query = FirebaseDatabase.getInstance().getReference("Video").orderByChild("valid").equalTo("yes");

        FirebaseRecyclerAdapter<Video,videoviewholder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Video, videoviewholder>(
                Video.class,
                R.layout.video_single_row,
                videoviewholder.class,
                query
        ) {
            @Override
            protected void populateViewHolder(final videoviewholder viewHolder, final Video model, int position) {
                viewHolder.set_video_row(model);

                final String video_key1 = getRef(position).getKey();

                viewHolder.option.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        PopupMenu popupMenu = new PopupMenu(VideoPlayActivity.this,viewHolder.option);

                        popupMenu.inflate(R.menu.option_menu);

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item)
                            {
                                switch (item.getItemId())
                                {
                                    case R.id.watch_later:
                                        mref = FirebaseDatabase.getInstance().getReference("Record").child(mAuth.getCurrentUser().getUid()).child("watch_later").push();
                                        mref.child("video_key").setValue(video_key1);
                                        Toast.makeText(VideoPlayActivity.this,"Added to Watch later",Toast.LENGTH_SHORT).show();
                                        return true;

                                    case R.id.not_interest:
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

                            Intent i = new Intent(VideoPlayActivity.this, VideoPlayActivity.class);
                            i.putExtra("video_key", video_key1);
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

        public void set_video_row(Video model)
        {
            ImageView thumb = (ImageView)mview.findViewById(R.id.thumb);
            TextView view1 = (TextView) mview.findViewById(R.id.view);
            TextView title = (TextView)mview.findViewById(R.id.title1);
            final TextView user = (TextView)mview.findViewById(R.id.user_name);

            Picasso.get().load(model.getThumb_url()).resize(320,200).centerCrop().into(thumb);

            title.setText(model.getTitle());
            view1.setText(Integer.toString(model.getViews()));

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
