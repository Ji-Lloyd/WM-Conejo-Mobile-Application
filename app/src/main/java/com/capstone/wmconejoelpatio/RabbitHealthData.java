package com.capstone.wmconejoelpatio;

//import com.google.firebase.firestore.ServerTimestamp;
import com.google.firebase.firestore.ServerTimestamp;

import com.google.firebase.Timestamp;

import java.util.Date;

public class RabbitHealthData {

    String name,notes,healthAdministration,disease,vaccination,date;
    Long id;


    public RabbitHealthData() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getHealthAdministration() {
        return healthAdministration;
    }

    public void setHealthAdministration(String healthAdministration) {
        this.healthAdministration = healthAdministration;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public String getVaccination() {
        return vaccination;
    }

    public void setVaccination(String vaccination) {
        this.vaccination = vaccination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
