package com.example.vinit.vplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class alertbox extends AppCompatDialogFragment {

    private EditText first_name,last_name;
    private FirebaseAuth mAuth;
    private DatabaseReference mref;
    private String first,last;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.alertdialog,null);

        first_name = (EditText)view.findViewById(R.id.first_name);
        last_name = (EditText)view.findViewById(R.id.last_name);

        mAuth = FirebaseAuth.getInstance();
        mref = FirebaseDatabase.getInstance().getReference("User").child(mAuth.getCurrentUser().getUid());

        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                first = dataSnapshot.child("first_name").getValue().toString();
                last = dataSnapshot.child("last_name").getValue().toString();
                first_name.setText(first);
                last_name.setText(last);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        builder.setView(view)
                .setTitle("Update Name")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(validate())
                        {
                            first = first_name.getText().toString();
                            last = last_name.getText().toString();
                            mref.child("first_name").setValue(first);
                            mref.child("last_name").setValue(last);
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    private boolean validate()
    {
        boolean val = true;
        if(TextUtils.isEmpty(first_name.getText().toString()))
        {
            first_name.setError("Invalid first name");
            val=false;
        }
        if(TextUtils.isEmpty(last_name.getText().toString()))
        {
            last_name.setError("Invalid last name");
            val=false;
        }
        return val;
    }
}
