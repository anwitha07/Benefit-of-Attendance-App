package com.gsss.benefitofattendence;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LecturerActivity extends AppCompatActivity {

    private static final String TAG = "LecturerActivity";

    private ListView studentsListView;
    private ArrayList<String> studentsList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference usersReference;
    private String lecturerName;
    private ArrayList<DataSnapshot> shortageFormsList;
    private TextView studentDetailsTextView;
    private TextView lecturerNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lecturer);

        studentsListView = findViewById(R.id.students_listview);
        lecturerNameTextView = findViewById(R.id.lecturer_name);
        studentDetailsTextView = findViewById(R.id.student_details);

        lecturerName = getIntent().getStringExtra("lecturerName");
        Log.d(TAG, "Lecturer Name: " + lecturerName);
        lecturerNameTextView.setText("Lecturer: " + lecturerName);

        studentsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, studentsList);
        studentsListView.setAdapter(adapter);

        usersReference = FirebaseDatabase.getInstance().getReference("users");
        shortageFormsList = new ArrayList<>();

        fetchShortageForms();

        studentsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showStudentDetails(position);
            }
        });
    }

    private void fetchShortageForms() {
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "DataSnapshot received");
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String role = userSnapshot.child("role").getValue(String.class);
                    Log.d(TAG, "User Role: " + role);
                    if ("Student".equals(role)) {
                        DataSnapshot shortageFormsSnapshot = userSnapshot.child("shortage_forms");
                        if (shortageFormsSnapshot.exists()) {
                            for (DataSnapshot formSnapshot : shortageFormsSnapshot.getChildren()) {
                                String coordinatorName = formSnapshot.child("classCoordinator").getValue(String.class);
                                Log.d(TAG, "Coordinator Name: " + coordinatorName);
                                if (lecturerName.equals(coordinatorName)) {
                                    String studentName = userSnapshot.child("name").getValue(String.class);
                                    Log.d(TAG, "Student Name: " + studentName);
                                    if (studentName != null && !studentsList.contains(studentName)) {
                                        studentsList.add(studentName);
                                        shortageFormsList.add(formSnapshot);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                Log.d(TAG, "Students List: " + studentsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LecturerActivity.this, "Failed to load shortage forms", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Database Error: " + databaseError.getMessage());
            }
        });
    }

    private void showStudentDetails(int position) {
        DataSnapshot formSnapshot = shortageFormsList.get(position);
        String studentName = studentsList.get(position);

        if (formSnapshot != null) {
            String name = formSnapshot.child("name").getValue(String.class);
            String certificate = formSnapshot.child("certificate").getValue(String.class);
            String classCoordinator = formSnapshot.child("classCoordinator").getValue(String.class);
            String endDate = formSnapshot.child("endDate").getValue(String.class);
            String reason = formSnapshot.child("reason").getValue(String.class);
            String startDate = formSnapshot.child("startDate").getValue(String.class);
            String usn = formSnapshot.child("usn").getValue(String.class);

            String studentDetails = "Name: " + name + "\n"
                    + "USN: " + usn + "\n"
                    + "Certificate: " + certificate + "\n"
                    + "Class Coordinator: " + classCoordinator + "\n"
                    + "End Date: " + endDate + "\n"
                    + "Reason: " + reason + "\n"
                    + "Start Date: " + startDate + "\n";

            studentDetailsTextView.setText(studentDetails);
            Log.d(TAG, "Student Details: " + studentDetails);
        } else {
            studentDetailsTextView.setText("No details available for " + studentName);
            Log.d(TAG, "No details available for " + studentName);
        }
    }
}
