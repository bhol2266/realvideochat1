package com.bhola.livevideochat;
import java.util.ArrayList;

public class UserQuestionWithAns {
    private String question;
    private ArrayList<String> answers;
    private String action;
    private String dateTime;
    private int read,sent;

    public UserQuestionWithAns() {
    }

    public UserQuestionWithAns(String question, ArrayList<String> answers, String action, String dateTime, int read, int sent) {
        this.question = question;
        this.answers = answers;
        this.action = action;
        this.dateTime = dateTime;
        this.read = read;
        this.sent = sent;
    }

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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }
}
