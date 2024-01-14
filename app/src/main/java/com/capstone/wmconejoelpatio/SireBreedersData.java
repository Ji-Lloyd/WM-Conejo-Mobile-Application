package com.capstone.wmconejoelpatio;

public class SireBreedersData {

    /*
     - Breeder Name
     - Breeder Breed
     - Breeder Origin
     - Breeder Accumulated Kits
     - Death Kits (data hided)
     - Breeder Success Rate
     */
    String sireName, sireBreed, sireOrigin, sireStatus,sireBirthDate;
    long sireAccumulatedKits, sireDeathKits, sireSuccessRate;

    public SireBreedersData() {
    }

    public String getSireName() {
        return sireName;
    }

    public void setSireName(String sireName) {
        this.sireName = sireName;
    }

    public String getSireBreed() {
        return sireBreed;
    }

    public void setSireBreed(String sireBreed) {
        this.sireBreed = sireBreed;
    }

    public String getSireOrigin() {
        return sireOrigin;
    }

    public void setSireOrigin(String sireOrigin) {
        this.sireOrigin = sireOrigin;
    }

    public long getSireAccumulatedKits() {
        return sireAccumulatedKits;
    }

    public void setSireAccumulatedKits(long sireAccumulatedKits) {
        this.sireAccumulatedKits = sireAccumulatedKits;
    }

    public long getSireDeathKits() {
        return sireDeathKits;
    }

    public void setSireDeathKits(long sireDeathKits) {
        this.sireDeathKits = sireDeathKits;
    }

    public long getSireSuccessRate() {
        return sireSuccessRate;
    }

    public void setSireSuccessRate(long sireSuccessRate) {
        this.sireSuccessRate = sireSuccessRate;
    }

    public String getSireStatus() {
        return sireStatus;
    }

    public void setSireStatus(String sireStatus) {
        this.sireStatus = sireStatus;
    }

    public String getSireBirthDate() {
        return sireBirthDate;
    }

    public void setSireBirthDate(String sireBirthDate) {
        this.sireBirthDate = sireBirthDate;
    }
}
