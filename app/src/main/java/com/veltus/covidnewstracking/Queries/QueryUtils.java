package com.veltus.covidnewstracking.Queries;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.util.Log;

import com.veltus.covidnewstracking.Fragments.NewsFeedFragment;
import com.veltus.covidnewstracking.ObjectClass.CovidCase;
import com.veltus.covidnewstracking.ObjectClass.NewsArticle;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    private QueryUtils() {

    }


    /* Query the News dataset and return a list of article objects */
    public static List<NewsArticle> fetchNewsFeedData(String requestUrl) {
        /* Create URL object */
        URL url = createUrl(requestUrl);

        /* Perform HTTP request to the URL and receive a JSON response back */
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("QueryUtils", "Problem making the HTTP request.", e);
        }

        /* Extract relevant fields from the JSON response and create a list of articles */
        List<NewsArticle> newsList = extractNewsFeatureFromJson(jsonResponse);


        /* Return the list of NewsArticles */
        return newsList;
    }


    /* Returns new URL object from the given string URL */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("QueryUtils", "Problem building the URL ", e);
        }
        return url;
    }

    /* Make an HTTP request to the given URL and return a String as the response */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        /* If the URL is null, then return early */
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(30000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            /* If the request was successful (response code 200), then read the input stream and parse the response */
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);

            } else if (urlConnection.getResponseCode() == 429) {
                NewsFeedFragment.errorCode = 429;
            } else {
                Log.e("QueryUtils", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("QueryUtils", "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                /* Closing the input stream could throw an IOException */
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static List<NewsArticle> extractNewsFeatureFromJson(String newsFeedJSON) {
        Bitmap bitmap = null;

        /* If the JSON string is empty or null, then return early */
        if (TextUtils.isEmpty(newsFeedJSON)) {
            return null;
        }

        /* Create an empty ArrayList that we can start adding news items to */
        List<NewsArticle> newsList = new ArrayList<>();

        /* Parse the JSON response string. If there's a problem with the way the JSON is formatted, a JSONException exception object will be thrown */
        try {

            /* Create a JSONObject from the JSON response string */
            JSONObject baseJsonResponse = new JSONObject(newsFeedJSON);

            /* Extract the JSONArray associated with the key called "data", which is a list of articles */
            JSONArray newsFeedArray = baseJsonResponse.getJSONArray("data");

            /* For each NewsArticle in the NewsFeedArray, create an NewsArticle object */
            for (int i = 0; i < newsFeedArray.length(); i++) {


                /* Get a single news article at position i within the list of articles */
                JSONObject currentArticle = newsFeedArray.getJSONObject(i);

                /* Extract the value for the key called "image" */
                String imageUrl = currentArticle.getString("image");

                if (!imageUrl.contains("null")) {
                    /* If the image is not null, download and create a new BitmapDrawable resource*/
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(BitmapFactory.decodeStream(new URL(imageUrl).openStream()));
                    bitmap = bitmapDrawable.getBitmap();

                } else bitmap = null;

                /* Extract the value for the key called "title" */
                String title = currentArticle.getString("title");

                /* Extract the value for the key called "source" */
                String author = currentArticle.getString("source");

                /* Extract the value for the key called "published_at" */
                String time = currentArticle.getString("published_at");

                /* Extract the value for the key called "url" */
                String url = currentArticle.getString("url");

                /* Create a new NewsArticle object with the bitmap, title, author, time, url and imageUrl from the JSON response */
                NewsArticle newsArticle = new NewsArticle(bitmap, title, author, time, url, imageUrl);

                /* Add the new NewsArticle to the NewsList */
                newsList.add(newsArticle);
            }

        } catch (JSONException | IOException e) {
            /* If an error is thrown when executing any of the above statements in the "try" block, catch the exception here, so the app doesn't crash */
            Log.e("QueryUtils", "Problem parsing the news article JSON results", e);
        }

        /* Return the list of articles */
        return newsList;

    }

    /* Convert the {@link InputStream} into a String which contains the whole JSON response from the server */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /* Query the Covid Cases dataset and return a list of covid case objects */
    public static List<CovidCase> fetchCovidCaseData(String requestUrl) {
        /* Create URL object */
        URL url = createUrl(requestUrl);

        /* Perform HTTP request to the URL and receive a JSON response back */
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("QueryUtils", "Problem making the HTTP request.", e);
        }

        /* Extract relevant fields from the JSON response and create a list of articles */
        List<CovidCase> covidCaseList = extractCovidFeatureFromJson(jsonResponse);

        /* Return the list of covid cases */
        return covidCaseList;
    }

    private static List<CovidCase> extractCovidFeatureFromJson(String covidCaseJSON) {
        /* If the JSON string is empty or null, then return early */
        if (TextUtils.isEmpty(covidCaseJSON)) {
            return null;
        }
        /* Create an empty ArrayList that we can start adding covid items to */
        List<CovidCase> covidCaseList = new ArrayList<>();

        /* Parse the JSON response string. If there's a problem with the way the JSON is formatted, a JSONException exception object will be thrown */
        try {
            /* Create a JSONObject from the JSON response string */
            JSONObject baseJsonResponse = new JSONObject(covidCaseJSON);

            /* Extract the JSONArray associated with the key called "data", which is a list of covid cases */
            JSONArray covidCaseArray = baseJsonResponse.getJSONArray("summary");

            /* For each covid case in the covidCaseArray, create an CovidCase object */
            for (int i = 0; i < covidCaseArray.length(); i++) {

                /* Get a single news article at position i within the list of covid cases */
                JSONObject currentCovid = covidCaseArray.getJSONObject(i);

                /* Extract the value for the key called "cases" */
                int newCases = currentCovid.getInt("cases");

                /* Extract the value for the key called "province" */
                String location = currentCovid.getString("province");

                /* Extract the value for the key called "date" */
                String date = currentCovid.getString("date");

                /* Extract the value for the key called "active_cases" */
                int totalCases = currentCovid.getInt("active_cases");

                /* Extract the value for the key called "deaths" */
                double deaths = currentCovid.getInt("deaths");

                /* Create a new CovidCase object with the newCases, location, date, totalCases and deaths from the JSON response */
                CovidCase covidCase = new CovidCase(newCases, location, date, totalCases, deaths);

                /* Add the new CovidCase to the list of Covid Cases */
                covidCaseList.add(covidCase);
            }

        } catch (JSONException e) {
            /* If an error is thrown when executing any of the above statements in the "try" block, catch the exception here, so the app doesn't crash */
            Log.e("QueryUtils", "Problem parsing the news article JSON results", e);
        }

        /* Return the list of covid cases */
        return covidCaseList;
    }

}






