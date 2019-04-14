package com.example.vinit.vplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText email,password;
    private Button login;
    private TextView new_user,forget_password;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("User Login");

        mAuth = FirebaseAuth.getInstance();

        email = (EditText)findViewById(R.id.emil_id);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.login);
        new_user = (TextView)findViewById(R.id.new_user);
        forget_password = (TextView)findViewById(R.id.forget_password);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In....");
        progressDialog.setCanceledOnTouchOutside(false);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    progressDialog.show();
                    loginuser();
                }
            }
        });

        new_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this,RegisterActivity.class);
                i.putExtra("work","register");
                startActivity(i);
            }
        });

        forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                i.putExtra("work","forget");
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });

    }

    private boolean validate()
    {
        boolean val = true;
        if(TextUtils.isEmpty(email.getText().toString()))
        {
            email.setError("Enter Email Address");
            val=false;
        }
        if(TextUtils.isEmpty(password.getText().toString()))
        {
            password.setError("Enter Password");
            val=false;
        }
        return val;
    }

    private void loginuser()
    {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this,"Login Failed...Please Try Again.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
