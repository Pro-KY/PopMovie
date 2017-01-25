package com.example.proky.popmovie;


public class Review {
    private String mAuthor;
    private String mContent;

    Review (String author, String content) {
        mAuthor = author;
        mContent = content;
    }


    public void setmAuthor(String author) {
        this.mAuthor = author;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmContent(String content) {
        this.mContent = content;
    }

    public String getmContent() {
        return mContent;
    }
}
