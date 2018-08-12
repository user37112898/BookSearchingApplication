package com.android.application.booklisting;

        import java.util.ArrayList;



public class Book {
    private ArrayList<String> mAuthors;
    // title of the book
    private String mTitle;

    public Book(ArrayList<String> author, String title) {
        mAuthors = author;
        mTitle = title;
    }

    public ArrayList<String> getAuthors() {
        return (ArrayList<String>) mAuthors.clone();
    }

    public String getTitle() {
        return mTitle;
    }
}
