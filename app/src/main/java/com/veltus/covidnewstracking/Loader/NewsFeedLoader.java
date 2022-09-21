package com.veltus.covidnewstracking.Loader;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.veltus.covidnewstracking.ObjectClass.NewsArticle;
import com.veltus.covidnewstracking.Queries.QueryUtils;

import java.util.List;

public class NewsFeedLoader extends AsyncTaskLoader<List<NewsArticle>> {

    private String url;

    public NewsFeedLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<NewsArticle> loadInBackground() {
        /* Don't perform the request if there are no URLs, or the first URL is null */
        if (url == null) {
            return null;
        }

        List<NewsArticle> newsList = QueryUtils.fetchNewsFeedData(url);

        return newsList;
    }
}
