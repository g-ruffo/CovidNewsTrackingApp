package com.veltus.covidnewstracking.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.veltus.covidnewstracking.ObjectClass.NewsArticle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsersSharedPreferences {

    /* The key that the favoritesList will be stored under in SharedPreferences */
    private static final String LIST_KEY = "list_key";

    public UsersSharedPreferences() {
        super();
    }

    public static void saveList(Context context, List<NewsArticle> list) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        /* Create a new JsonArray to store the favoritesList in */
        JSONArray jsonArray = new JSONArray();

        /* Get each item in the favoritesList and convert them into a JsonObject*/
        for (int i = 0; i < list.size(); i++) {
            jsonArray.put(list.get(i).getJSONObject());
        }
        editor.putString(LIST_KEY, jsonArray.toString());
        editor.commit();

    }

    public static List<NewsArticle> loadList(Context mContext) throws JSONException {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        /* Create a new list to load the stored JsonArray into */
        List<NewsArticle> arrayList = new ArrayList<NewsArticle>();

        /* Extract the JSONArray associated with the key called "list_key", which is a list of articles */
        String arrayString = sharedPreferences.getString(LIST_KEY, "");
        JSONArray newsFeedArray = new JSONArray(arrayString);

        /* For each NewsArticle in the NewsFeedArray, create an NewsArticle object */
        for (int i = 0; i < newsFeedArray.length(); i++) {

            /*Get a single news article at position i within the list of articles*/
            JSONObject currentArticle = newsFeedArray.getJSONObject(i);

            /* Extract the value for the key called "title" */
            String title = currentArticle.getString("title");

            /* Extract the value for the key called "author" */
            String author = currentArticle.getString("author");

            /* Extract the value for the key called "date" */
            String date = currentArticle.getString("date");

            /* Extract the value for the key called "website" */
            String website = currentArticle.getString("website");

            /* Extract the value for the key called "imageUrl" */
            String imageUrl = currentArticle.getString("imageUrl");

            /* Create a new NewsArticle object with the title, author, date, website and imageUrl from the JSON response */
            NewsArticle newsArticle = new NewsArticle(title, author, date, website, imageUrl);

            /* Add the new NewsArticle to the arrayList */
            arrayList.add(newsArticle);

        }
        return arrayList;

    }

}
