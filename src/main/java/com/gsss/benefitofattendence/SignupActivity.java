package com.gsss.benefitofattendence;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupPassword, signupEmail;
    TextView loginRedirectText;
    RadioGroup radioGroup1;
    Button signupButton;

    FirebaseAuth mAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupactivity);

        signupName = findViewById(R.id.signup_name);
        signupPassword = findViewById(R.id.password);
        signupEmail = findViewById(R.id.email);
        radioGroup1 = findViewById(R.id.radioGroup1);
        signupButton = findViewById(R.id.signup_button);
        loginRedirectText = findViewById(R.id.loginRedirectText);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = signupName.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();
                int selectedRadioButtonId = radioGroup1.getCheckedRadioButtonId();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || selectedRadioButtonId == -1) {
                    Toast.makeText(SignupActivity.this, "Please fill all fields and select a role", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                    String selectedText = selectedRadioButton.getText().toString();

                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        // Set the display name
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build();
                                        user.updateProfile(profileUpdates);

                                        // Create a user helper class
                                        HelperClass helperClass = new HelperClass(name, email, password, selectedText);
                                        databaseReference.child(user.getUid()).setValue(helperClass);

                                        Toast.makeText(SignupActivity.this, "You have signed up successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent;
                                        if (selectedText.equals("Student")) {
                                            intent = new Intent(SignupActivity.this, ShortgeFormActivity.class);
                                        } else {
                                            intent = new Intent(SignupActivity.this, LecturerActivity.class);
                                        }
                                        startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(SignupActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
