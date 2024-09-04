package com.gsss.benefitofattendence;
import java.io.Serializable;


public class Student implements Serializable {
    public  static String name;

    public String email;
    public  static String usn;
    public String startDate;
    public String endDate;
    public String certificate;
    public String reason;
    public String classCoordinator;

    // Default constructor required for calls to DataSnapshot.getValue(Student.class)
    public Student() {
    }

    // Parameterized constructor to initialize the fields
    public Student(String name, String usn, String startDate, String endDate, String certificate, String reason, String classCoordinator) {
        this.name = name;
        this.usn = usn;
        this.startDate = startDate;
        this.endDate = endDate;
        this.certificate = certificate;
        this.reason = reason;
        this.classCoordinator = classCoordinator;
    }

    // Getter and setter methods for each field

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getClassCoordinator() {
        return classCoordinator;
    }

    public void setClassCoordinator(String classCoordinator) {
        this.classCoordinator = classCoordinator;
    }

    public String getEmail() {

        return email;
    }
}

