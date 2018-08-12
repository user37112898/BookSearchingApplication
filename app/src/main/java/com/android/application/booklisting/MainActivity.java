package com.android.application.booklisting;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private BookAdapter adapter;
    // last URL we used to fetch data
    private String uri;
    // last keywords usr written to search for books
    String bookKeywords;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save last URL to the instance state
        outState.putString("uri", uri);
        outState.putString("bookKeywords", bookKeywords);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new BookAdapter(this, R.layout.book, new ArrayList<Book>());
        ListView booksListView = (ListView) findViewById(R.id.list);
        booksListView.setAdapter(adapter);
        booksListView.setEmptyView(findViewById(R.id.empty_list));
        // try to get last URL from the instance state
        if (savedInstanceState != null) {
            uri = savedInstanceState.getString("uri");
            bookKeywords = savedInstanceState.getString("bookKeywords");
        }
        // if there is a URL in Activity's instance state, fetch data using this URL
        if (TextUtils.isEmpty(bookKeywords)) {
            setWriteKeywords();
        } else {
            if (!TextUtils.isEmpty(uri)) {
                new BookAsyncTask().execute(uri);
            }
        }
        ((Button) findViewById(R.id.do_filter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookKeywords = ((EditText) findViewById(R.id.q_filter)).getText().toString();
                if (TextUtils.isEmpty(bookKeywords)) {
                    setWriteKeywords();
                    refreshUI(new ArrayList<Book>());
                } else {
                    // build URL to fetch data
                    Uri baseUri = Uri.parse(getString(R.string.url_base));
                    Uri.Builder uriBuilder = baseUri.buildUpon();
                    uriBuilder.appendQueryParameter("langRestrict", "en");
                    uriBuilder.appendQueryParameter("maxResults", "20");
                    uriBuilder.appendQueryParameter("q", bookKeywords);
                    uri = uriBuilder.toString();
                    // fetch data
                    new BookAsyncTask().execute(uri);
                }
            }
        });
    }

    /**
     * refresh user interface
     *
     * @param books
     */
    public void refreshUI(ArrayList<Book> books) {
        if (books == null || books.size() == 0) {
            if (TextUtils.isEmpty(bookKeywords)) {
                setWriteKeywords();
            } else if (gotConnection()) {
                // show that there is no books in the result
                setNoBooks();
            } else {
                // say user that se see no internet
                setNoConnection();
            }
            // clear all views from ListView
            adapter.setBooks(new ArrayList<Book>());
            return;
        }
        // put list of books that we got into the ListView
        adapter.setBooks(books);
    }

    /**
     * Check if the user got connection
     *
     * @return true - has got connection / false - no connection
     */
    private boolean gotConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private class BookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected ArrayList<Book> doInBackground(String... params) {
            if (params != null && params.length > 0) {
                return QueryUtils.loadBooks(params[0]);
            } else {
                // if no url was passed in return null
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            super.onPostExecute(books);
            hideProgress();
            refreshUI(books);
        }
    }

    /**
     * show progressbar for ListView
     */
    private void showProgress() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
    }

    /**
     * hide progressbar for ListView
     */
    private void hideProgress() {
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    /**
     * in empty_list TextView set "no books" message
     */
    private void setNoBooks() {
        ((TextView) findViewById(R.id.empty_list)).setText(getString(R.string.no_books));
    }

    /**
     * in empty_list TextView set "no connection" message
     */
    private void setNoConnection() {
        ((TextView) findViewById(R.id.empty_list)).setText(getString(R.string.no_connection));

    }

    /**
     * in empty_list TextView set "enter keywords" message
     */
    private void setWriteKeywords() {
        ((TextView) findViewById(R.id.empty_list)).setText(getString(R.string.enter_into_the_search_what_book_you_want));
    }
}
