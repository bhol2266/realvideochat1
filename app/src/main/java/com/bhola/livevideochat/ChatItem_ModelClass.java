package com.bhola.livevideochat;

import java.util.ArrayList;

public class ChatItem_ModelClass {
    private int id;
    private String userName;
    private String userProfile;
    private boolean containsQuestion;
    private String recommendationType;
    private ArrayList<UserBotMsg> userBotMsg;
    private UserQuestionWithAns questionWithAns;

    // Constructor
    public ChatItem_ModelClass(int id, String userName, String userProfile, boolean containsQuestion, String recommendationType, ArrayList<UserBotMsg> userBotMsg, UserQuestionWithAns questionWithAns) {
        this.id = id;
        this.userName = userName;
        this.userProfile = userProfile;
        this.containsQuestion = containsQuestion;
        this.recommendationType = recommendationType;
        this.userBotMsg = userBotMsg;
        this.questionWithAns = questionWithAns;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public boolean isContainsQuestion() {
        return containsQuestion;
    }

    public void setContainsQuestion(boolean containsQuestion) {
        this.containsQuestion = containsQuestion;
    }

    public String getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(String recommendationType) {
        this.recommendationType = recommendationType;
    }

    public ArrayList<UserBotMsg> getUserBotMsg() {
        return userBotMsg;
    }

    public void setUserBotMsg(ArrayList<UserBotMsg> userBotMsg) {
        this.userBotMsg = userBotMsg;
    }

    public UserQuestionWithAns getQuestionWithAns() {
        return questionWithAns;
    }

    public void setQuestionWithAns(UserQuestionWithAns questionWithAns) {
        this.questionWithAns = questionWithAns;
    }
}

