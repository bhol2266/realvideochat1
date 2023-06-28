package com.bhola.livevideochat;
import java.util.ArrayList;

public class UserQuestionWithAns {
    private String question;
    private ArrayList<String> answers;
    private String action;

    // Constructor
    public UserQuestionWithAns(String question, ArrayList<String> answers, String action) {
        this.question = question;
        this.answers = answers;
        this.action = action;
    }

    // Getters and Setters
    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public ArrayList<String> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<String> answers) {
        this.answers = answers;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
