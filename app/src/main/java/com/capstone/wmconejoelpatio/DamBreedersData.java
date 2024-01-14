package com.capstone.wmconejoelpatio;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class DamBreedersData {

    /*
     - Breeder Name
     - Breeder Breed
     - Breeder Origin
     - Breeder Accumulated Kits
     - Death Kits (data hided)
     - Breeder Success Rate
     */

    String damName, damBreed, damOrigin, damStatus, damPregnant, damBirthDate, recentStudDate;
    long damAccumulatedKits, damDeathKits, damSuccessRate;

    public DamBreedersData() {
    }

    public String getDamName() {
        return damName;
    }

    public void setDamName(String damName) {
        this.damName = damName;
    }

    public String getDamBreed() {
        return damBreed;
    }

    public void setDamBreed(String damBreed) {
        this.damBreed = damBreed;
    }

    public String getDamOrigin() {
        return damOrigin;
    }

    public void setDamOrigin(String damOrigin) {
        this.damOrigin = damOrigin;
    }

    public String getDamStatus() {
        return damStatus;
    }

    public void setDamStatus(String damStatus) {
        this.damStatus = damStatus;
    }

    public long getDamAccumulatedKits() {
        return damAccumulatedKits;
    }

    public void setDamAccumulatedKits(long damAccumulatedKits) {
        this.damAccumulatedKits = damAccumulatedKits;
    }

    public long getDamDeathKits() {
        return damDeathKits;
    }

    public void setDamDeathKits(long damDeathKits) {
        this.damDeathKits = damDeathKits;
    }

    public long getDamSuccessRate() {
        return damSuccessRate;
    }

    public void setDamSuccessRate(long damSuccessRate) {
        this.damSuccessRate = damSuccessRate;
    }

    public String getDamPregnant() {
        return damPregnant;
    }

    public void setDamPregnant(String damPregnant) {
        this.damPregnant = damPregnant;
    }

    public String getDamBirthDate() {
        return damBirthDate;
    }

    public void setDamBirthDate(String damBirthDate) {
        this.damBirthDate = damBirthDate;
    }

    public String getRecentStudDate() {
        return recentStudDate;
    }

    public void setRecentStudDate(String recentStudDate) {
        this.recentStudDate = recentStudDate;
    }
}
