package com.android.application.booklisting;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class BookAdapter extends ArrayAdapter<Book> {
    // link to the list of books
    private ArrayList<Book> mBooks;

    public BookAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Book> objects) {
        super(context, resource, objects);
        mBooks = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book, null);
        }
        Book book = getItem(position);
        ((TextView) convertView.findViewById(R.id.title)).setText(book.getTitle());
        ((TextView) convertView.findViewById(R.id.authors)).setText(book.getAuthors().toString());
        return convertView;
    }

    @Nullable
    @Override
    public Book getItem(int position) {
        return super.getItem(position);
    }


    public void setBooks(ArrayList<Book> books) {
        mBooks.clear();
        mBooks.addAll(books);
        notifyDataSetChanged();
    }
}
