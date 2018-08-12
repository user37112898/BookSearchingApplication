package com.android.application.booklisting;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;



public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static ArrayList<Book> loadBooks(String baseUrl) {
        URL url = createUrl(baseUrl);
        String jsonString = "";
        try {
            jsonString = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return extractBooks(jsonString);
    }

    private static URL createUrl(String requestUrl) {
        if (TextUtils.isEmpty(requestUrl)) {
            return null;
        }
        URL url = null;
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return url;
    }

    private static String readFromStream(InputStream inputStream) {
        StringBuilder jsonResult = new StringBuilder();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        try {
            String line = reader.readLine();
            while (line != null) {
                jsonResult.append(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return jsonResult.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String result = "";
        HttpURLConnection urlConnection;
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
        urlConnection.connect();
        if (urlConnection.getResponseCode() == 200) {
            InputStream inputStream = urlConnection.getInputStream();
            result = readFromStream(inputStream);
        }
        return result;
    }

    private static ArrayList<Book> extractBooks(String jsonString) {
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        ArrayList<Book> books = new ArrayList<>();
        try {
            JSONObject rootJSON = new JSONObject(jsonString);
            JSONArray itemsJsonArray = rootJSON.getJSONArray("items");
            int length = itemsJsonArray.length();
            for (int i = 0; i < length; i++) {
                try {
                    JSONObject volumeJSON = itemsJsonArray.getJSONObject(i);
                    JSONObject volumeInfoJSON = volumeJSON.getJSONObject("volumeInfo");
                    String bookTitle = volumeInfoJSON.getString("title");
                    JSONArray authorsJSON = volumeInfoJSON.getJSONArray("authors");
                    int authorsLength = authorsJSON.length();
                    ArrayList<String> authors = new ArrayList<>();
                    for (int j = 0; j < authorsLength; j++) {
                        String author = authorsJSON.getString(j);
                        authors.add(author);
                    }
                    books.add(new Book(authors, bookTitle));
                } catch (JSONException e) {
                    Log.e(QueryUtils.class.getSimpleName(), "JSON error while extracting book info", e);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON error while extracting books");
        }
        return books;
    }
}
