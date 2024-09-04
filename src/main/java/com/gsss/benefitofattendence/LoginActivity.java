package com.gsss.benefitofattendence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText useremail, password;
    Button loginButton;
    RadioGroup radioGroup;

    TextView signupRedirectText;
    FirebaseAuth mAuth;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        useremail = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        radioGroup = findViewById(R.id.radioGroup);
        signupRedirectText = findViewById(R.id.signupRedirectText);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUserEmail() || !validatePassword()) {
                    return;
                } else {
                    checkUser();
                }
            }
        });

        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    public Boolean validateUserEmail() {
        String val = useremail.getText().toString();
        if (val.isEmpty()) {
            useremail.setError("Email cannot be empty");
            return false;
        } else {
            useremail.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = password.getText().toString();
        if (val.isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userEmail = useremail.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            databaseReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        HelperClass user = snapshot.getValue(HelperClass.class);
                                        if (user != null) {
                                            int selectedId = radioGroup.getCheckedRadioButtonId();
                                            RadioButton radioButton = findViewById(selectedId);
                                            if (radioButton != null) {
                                                String selectedRole = radioButton.getText().toString();
                                                if (selectedRole.equals(user.getRole())) {
                                                    Intent intent;
                                                    if (user.getRole().equals("Student")) {
                                                        intent = new Intent(LoginActivity.this, ShortgeFormActivity.class);
                                                    } else if (user.getRole().equals("Teacher")) {
                                                        intent = new Intent(LoginActivity.this, LecturerActivity.class);
                                                        intent.putExtra("lecturerName", user.getName());
                                                    } else {
                                                        Toast.makeText(LoginActivity.this, "Invalid role selected", Toast.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                    startActivity(intent);
                                                    finish(); // Optional: Finish LoginActivity to prevent returning to it on back press
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Role mismatch", Toast.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Please select your role", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, "User not found in database", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(TAG, "Database error: " + databaseError.getMessage());
                                    Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Log.e(TAG, "Authentication failed: " + task.getException().getMessage());
                        password.setError("Invalid Credentials");
                        password.requestFocus();
                    }
                });
    }
}
