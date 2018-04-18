package be.eaict.stretchalyzer1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class CreateAccountActivity extends AppCompatActivity {
    private static final String TAG = "CreateAccountActivity";
    private EditText txtCreateEmail;
    private EditText txtCreatePassword;
    private Button btnCreate;
    private Button btnCancel;
    private FirebaseAuth mAuth;
    //private FirebaseDatabase database;
    //private DatabaseReference userdatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createaccount);

        txtCreateEmail = (EditText) findViewById(R.id.txtCreateEmail);
        txtCreatePassword = (EditText) findViewById(R.id.txtCreatePassword);
        btnCreate = (Button) findViewById(R.id.btnCreateOk);
        btnCancel = (Button) findViewById(R.id.btnCreateCancel);
        //database = FirebaseDatabase.getInstance();
        //userdatabase = database.getReference("Users");

        mAuth = FirebaseAuth.getInstance();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelCreate();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }

    public void createAccount(){
        final String email = txtCreateEmail.getText().toString();
        final String password = txtCreatePassword.getText().toString();
        if(email != null && !email.equals("") && password != null && !password.equals("")){
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                //String id = user.getUid();


                                Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
                                startActivity(intent);

                                // updateUI(user);
                            }
                        /*else if(usernameExists){
                            Log.w(TAG, "Username Exists");
                            Toast.makeText(CreateAccountActivity.this, "Username Exists already", Toast.LENGTH_LONG).show();
                            usernameExists = false;
                        }*/
                            else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                String errorMessage = task.getException().toString();
                                Toast.makeText(CreateAccountActivity.this, errorMessage,
                                        Toast.LENGTH_LONG).show();
                                // updateUI(null);
                            }

                            // ...
                        }
                    });
        }

        else{
            Toast.makeText(CreateAccountActivity.this, "Empty field",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelCreate(){
        txtCreateEmail.setText("");
        txtCreatePassword.setText("");
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
    }



}
