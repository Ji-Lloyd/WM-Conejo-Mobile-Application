package com.capstone.wmconejoelpatio;

public class RabbitFeedingData {

    String feedFor,name,feedType,feedQuantity,feedSchedule,feedDate,feedTime;
    Integer id;
    Double currentWeight,goalWeight;

    public RabbitFeedingData() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFeedFor() {
        return feedFor;
    }

    public void setFeedFor(String feedFor) {
        this.feedFor = feedFor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFeedType() {
        return feedType;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public String getFeedQuantity() {
        return feedQuantity;
    }

    public void setFeedQuantity(String feedQuantity) {
        this.feedQuantity = feedQuantity;
    }

    public String getFeedSchedule() {
        return feedSchedule;
    }

    public void setFeedSchedule(String feedSchedule) {
        this.feedSchedule = feedSchedule;
    }

    public String getFeedDate() {
        return feedDate;
    }

    public void setFeedDate(String feedDate) {
        this.feedDate = feedDate;
    }

    public String getFeedTime() {
        return feedTime;
    }

    public void setFeedTime(String feedTime) {
        this.feedTime = feedTime;
    }

    public Double getCurrentWeight() {
        return currentWeight;
    }

    public void setCurrentWeight(Double currentWeight) {
        this.currentWeight = currentWeight;
    }

    public Double getGoalWeight() {
        return goalWeight;
    }

    public void setGoalWeight(Double goalWeight) {
        this.goalWeight = goalWeight;
    }
}
