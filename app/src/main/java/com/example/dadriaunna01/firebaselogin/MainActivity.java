package com.example.dadriaunna01.firebaselogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference userRef = database.getReference("User");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener;
    private EditText infoText;
    private TextView displayText;
    private ArrayList<String> newList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoText = (EditText) findViewById(R.id.say_hello);
        displayText = (TextView) findViewById(R.id.display_text);



        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null)
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                else {
                    userRef = database.getReference(user.getUid());
                    userRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            newList.add(dataSnapshot.getValue(String.class));
                            displayList();
//                            String text= dataSnapshot.getValue(String.class);
//                            displayText.setText(text);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            Toast.makeText(MainActivity.this, dataSnapshot.getValue(MainActivity.class) + " has changed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            Toast.makeText(MainActivity.this, dataSnapshot.getValue(MainActivity.class) + " is removed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


                }
            }

        };
    }

    private void displayList() {
        String text = "";
        for (String a : newList)
            text += a + "\n";
        displayText.setText(text);

    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        auth.removeAuthStateListener(authListener);
    }

    public void addSomething(View view) {
        FirebaseUser user = auth.getCurrentUser();
        DatabaseReference userRef = database.getReference(user.getUid());
        userRef.push().setValue(infoText.getText().toString());
    }

    public void signOut(View view) {
        auth.signOut();

    }


}
