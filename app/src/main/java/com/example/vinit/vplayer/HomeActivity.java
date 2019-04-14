package com.example.vinit.vplayer;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity
{

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private VideoFragment videoFragment;
    private trendingFragment trendingFrag;
    private UploadFragment uploadFragment;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("VPlayer");

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigationbar);
        videoFragment = new VideoFragment();
        trendingFrag = new trendingFragment();
        uploadFragment = new UploadFragment();

        setnavigationbar(videoFragment);

        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.home:
                        setnavigationbar(videoFragment);
                        return true;

                    case R.id.treding:
                        setnavigationbar(trendingFrag);
                        return true;

                    case R.id.upload:
                        setnavigationbar(uploadFragment);
                        return true;

                    default:
                        return false;
                }
            }
        });

    }

    private void setnavigationbar(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu1,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_profile)
        {
            Intent i2 = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(i2);
            overridePendingTransition( R.anim.slide_in_up, R.anim.slide_out_up );
        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
