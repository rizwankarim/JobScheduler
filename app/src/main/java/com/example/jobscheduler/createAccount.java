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

public class createAccount extends AppCompatActivity {
    private FirebaseAuth mAuth;
    EditText email,pass;
    Button signUp;
    ProgressDialog progressDialog;
    TextView gotoSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        mAuth= FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        email=findViewById(R.id.email);
        pass=findViewById(R.id.password);
        gotoSignIn=findViewById(R.id.gotoSignIn);
        signUp=findViewById(R.id.signUp);
        signUp.setOnClickListener(new View.OnClickListener() {
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
                else
                    {
                        progressDialog = ProgressDialog.show(createAccount.this
                                , "","Please Wait...", true);
                        mAuth.createUserWithEmailAndPassword(user_email, user_password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(createAccount.this, "Account Created !",
                                                Toast.LENGTH_SHORT).show();
                                            // Sign in success, update UI with the signed-in user's information
                                        } else {

                                            // If sign in fails, display a message to the user.
                                            Toast.makeText(createAccount.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                    }
            }
        });

        gotoSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signIn=new Intent(createAccount.this, login.class);
                finish();
                startActivity(signIn);
            }
        });
    }
}