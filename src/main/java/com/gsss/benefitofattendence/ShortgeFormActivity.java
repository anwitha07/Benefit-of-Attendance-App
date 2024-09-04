package com.gsss.benefitofattendence;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ShortgeFormActivity extends AppCompatActivity {

    EditText nameEditText, usnEditText, startDateEditText, endDateEditText, certificateEditText;
    Spinner reasonSpinner, classCoordinatorSpinner;
    Button submitButton;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortge_form);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            // Initialize Firebase database reference
            databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseUser.getUid());
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        nameEditText = findViewById(R.id.form_name);
        usnEditText = findViewById(R.id.form_USN);
        startDateEditText = findViewById(R.id.form_StartDate);
        endDateEditText = findViewById(R.id.form_EndDate);
        certificateEditText = findViewById(R.id.form_certificate);
        reasonSpinner = findViewById(R.id.form_reason);
        classCoordinatorSpinner = findViewById(R.id.form_Classcoordinator_spinner);
        submitButton = findViewById(R.id.login_submit);

        // Set onClickListener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        // Get values from EditText and Spinner
        String name = nameEditText.getText().toString().trim();
        String usn = usnEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        String certificate = certificateEditText.getText().toString().trim();
        String reason = reasonSpinner.getSelectedItem().toString();
        String classCoordinator = classCoordinatorSpinner.getSelectedItem().toString();

        // Check if any field is empty
        if (name.isEmpty() || usn.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || certificate.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a new Student object with the form data
        Student student = new Student(name, usn, startDate, endDate, certificate, reason, classCoordinator);

        // Store the student data in the database under the user's UID
        databaseReference.child("shortage_forms").push().setValue(student)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Show a success message
                        Toast.makeText(this, "Form submitted successfully", Toast.LENGTH_SHORT).show();

                        // Clear EditText fields after submission
                        nameEditText.setText("");
                        usnEditText.setText("");
                        startDateEditText.setText("");
                        endDateEditText.setText("");
                        certificateEditText.setText("");

                        // Find the class coordinator in the database and send the form data
                        sendToClassCoordinator(classCoordinator, student);
                    } else {
                        Toast.makeText(this, "Failed to submit form", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendToClassCoordinator(String classCoordinatorName, Student student) {
        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference("users");
        usersReference.orderByChild("name").equalTo(classCoordinatorName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String coordinatorId = snapshot.getKey();
                            usersReference.child(coordinatorId).child("shortage_forms").push().setValue(student)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(ShortgeFormActivity.this, "Form sent to class coordinator successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(ShortgeFormActivity.this, "Failed to send form to class coordinator", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override


                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ShortgeFormActivity.this, "Failed to find class coordinator", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
