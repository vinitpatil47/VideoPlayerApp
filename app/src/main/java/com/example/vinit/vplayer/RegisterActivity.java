package com.example.vinit.vplayer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String work;
    private LinearLayout register;
    private RelativeLayout reset;

    private EditText first_name,email,password,last_name,reset_email;
    private Button sign_in,sent_email;
    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private DatabaseReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        work = getIntent().getStringExtra("work");
        register = (LinearLayout)findViewById(R.id.register);
        reset = (RelativeLayout)findViewById(R.id.reset);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(work.equals("register")) {
            getSupportActionBar().setTitle("User Registration");
            register.setVisibility(View.VISIBLE);
            reset.setVisibility(View.INVISIBLE);
        }
        else {
            getSupportActionBar().setTitle("Reset Password");
            register.setVisibility(View.INVISIBLE);
            reset.setVisibility(View.VISIBLE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        first_name = (EditText)findViewById(R.id.first_name);
        email = (EditText)findViewById(R.id.emil_id);
        password = (EditText)findViewById(R.id.password);
        last_name = (EditText)findViewById(R.id.last_name);
        sign_in = (Button)findViewById(R.id.sign_in);
        sent_email = (Button)findViewById(R.id.sent_email);
        reset_email = (EditText)findViewById(R.id.reset_email);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In....");
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();

        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate1()) {
                    progressDialog.show();
                    createuser();
                }
            }
        });

        sent_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(validate2()) {
                    progressDialog.setMessage("Sending mail....");
                    progressDialog.show();
                    sendemail();
                }
            }
        });

    }

    private boolean validate1()
    {
        boolean val = true;
        if(TextUtils.isEmpty(first_name.getText().toString()))
        {
            first_name.setError("Enter Valid First Name");
            val=false;
        }
        if(TextUtils.isEmpty(last_name.getText().toString()))
        {
            last_name.setError("Enter Valid Last Name");
            val=false;
        }
        if(TextUtils.isEmpty(email.getText().toString()))
        {
            email.setError("Enter Valid Email ID");
            val=false;
        }
        if(TextUtils.isEmpty(password.getText().toString()))
        {
            password.setError("Enter Valid Password");
            val=false;
        }

        return val;
    }

    private boolean validate2()
    {
        boolean val=true;
        if(TextUtils.isEmpty(reset_email.getText().toString())) {
            reset_email.setError("Enter Valid Email_id");
            val = false;
        }
        return val;
    }

    private void createuser() {

        mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            savedatabase();
                            Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(i);
                            finish();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,"Sign In Failed...Please Try Again.",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void savedatabase() {
        mref = FirebaseDatabase.getInstance().getReference("User").child(mAuth.getCurrentUser().getUid());
        User user = new User(first_name.getText().toString(),last_name.getText().toString(),email.getText().toString(),null);
        mref.setValue(user);
    }

    private void sendemail()
    {
        String emailAddress = reset_email.getText().toString().trim();

        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,"Email Sent",Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,"Something wents Wrong...Please try again.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
