package com.zawisza.guitar_app.fragments.Songbook;

public class Songbook {

    private String question;
    private String answer;

    public Songbook() {
    }

    public Songbook(String title, String answer) {
        this.question = title;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}


