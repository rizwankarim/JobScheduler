package com.example.jobscheduler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    TextView gotoSignUp;
    ProgressDialog progressDialog;
    EditText email,pass;
    Button signIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        gotoSignUp=findViewById(R.id.gotoSignUp);
        email=findViewById(R.id.email);
        pass=findViewById(R.id.password);
        signIn=findViewById(R.id.signIn);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_email=email.getText().toString();
                String user_password=pass.getText().toString();

                if(user_email.isEmpty()){
                    email.setError("Email is required !");
                    email.requestFocus();
                    return;
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(user_email).matches())
                {
                    email.setError("Enter a social email address !");
                    email.requestFocus();
                    return;
                }
                else if(user_password.isEmpty()){
                    pass.setError("Password is required !");
                    pass.requestFocus();
                    return;
                }
                else if(user_password.length()<6){
                    pass.setError("Minimum length of password should be 6 !");
                    pass.requestFocus();
                    return;
                }
                else {
                    progressDialog = ProgressDialog.show(login.this
                            , "", "Please Wait...", true);
                    mAuth.signInWithEmailAndPassword(user_email, user_password)
                            .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(login.this, "Authentication Success.",Toast.LENGTH_SHORT).show();
                                        Intent main=new Intent(login.this,MainActivity.class);
                                        finish();
                                        startActivity(main);

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(login.this, "Authentication failed.",Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });
                }
            }
        });
        gotoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUp=new Intent(login.this,createAccount.class);
                finish();
                startActivity(signUp);
            }
        });
    }
}