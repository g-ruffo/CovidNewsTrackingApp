package com.veltus.covidnewstracking.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.veltus.covidnewstracking.BuildConfig;
import com.veltus.covidnewstracking.Loader.NewsFeedLoader;
import com.veltus.covidnewstracking.ObjectClass.NewsArticle;
import com.veltus.covidnewstracking.R;
import com.veltus.covidnewstracking.Adapters.RecyclerAdapterNews;
import com.veltus.covidnewstracking.databinding.FragmentNewsFeedBinding;

import java.util.ArrayList;
import java.util.List;

public class NewsFeedFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<NewsArticle>>, RecyclerAdapterNews.OnItemClickListener {

    /* URL for the news feed data */
    private static final String NEWSFEED_REQUEST_URL = "http://api.mediastack.com/v1/news?&limit=40&keywords=covid";
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final int NEWSFEED_LOADER_ID = 0;
    public static boolean hasInternetConnection;
    public static int errorCode;
    private static List<NewsArticle> newsList;
    private static RecyclerAdapterNews newsAdapter;
    public TextView noList;
    private FragmentNewsFeedBinding binding;
    private RecyclerView recyclerView;

    /* Give all activities and fragments access to the favoritesAdapter */
    public static RecyclerAdapterNews getNewsFeedAdapter() {
        return newsAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewsFeedBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        noList = binding.noList;

        /* Create a new list of articles */
        newsList = new ArrayList<>();

        setAdapter();

        /* Get a reference to the ConnectivityManager to check state of network connectivity */
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        /* Get details on the currently active default data network */
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        /* If there is a network connection, fetch data */
        if (networkInfo != null && networkInfo.isConnected()) {
            hasInternetConnection = true;

            /* Get a reference to the LoaderManager, in order to interact with loaders */
            LoaderManager loaderManager = LoaderManager.getInstance(this);

            /* Initialize the loader. Pass in the int ID constant defined above and pass in null for the bundle */
            loaderManager.initLoader(NEWSFEED_LOADER_ID, null, this);
        } else {
            /* If no connection display an error, set boolean to false and hide spinner */
            hasInternetConnection = false;
            binding.loadingSpinner.setVisibility(View.GONE);

            /* Update empty state with no connection error message */
            errorCode = 400;
            setNoListText();

        }

        /* Set an onRefreshListener for the fragment */
        binding.swipeLayout.setOnRefreshListener(() -> {
            /* First check to see if there is an internet connection */
            checkInternetConnection();

            /* If there is a connection hide noList TextView and restart loader */
            if (hasInternetConnection) {
                errorCode = 2;
                setNoListText();
                newsAdapter.clear();
                LoaderManager.getInstance(getActivity()).restartLoader(NEWSFEED_LOADER_ID, null, NewsFeedFragment.this);
                binding.swipeLayout.setRefreshing(false);
                binding.loadingSpinner.setVisibility(View.VISIBLE);
            } else {
                /* If there isn't a connection hide spinner */
                binding.swipeLayout.setRefreshing(false);
            }

        });

        return view;

    }

    private void checkInternetConnection() {
        /* Get a reference to the ConnectivityManager to check state of network connectivity */
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        /* Get details on the currently active default data network */
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        /* If there is a network connection, fetch data and set boolean to true */
        if (networkInfo != null && networkInfo.isConnected()) {
            hasInternetConnection = true;

        } else {
            hasInternetConnection = false;

            /* If no connection update the noList TextView and set boolean to false */
            errorCode = 400;
            setNoListText();
        }
    }


    private void setAdapter() {


        /* Link the recyclerView to the RecyclerView within the fragment_news_feed.xml layout */
        recyclerView = binding.newsFeedRecyclerView;

        /* Create a new adapter that takes an empty list of news articles as input */
        newsAdapter = new RecyclerAdapterNews(newsList, this);

        /* Create a layout manager for the News Feed RecyclerView */
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        /* Set an ItemAnimator for the RecyclerView */
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /* Set the adapter on the RecyclerView so the list can be populated in the user interface */
        recyclerView.setAdapter(newsAdapter);

    }

    public void setNoListText() {
        int errorNumber = errorCode;
        /* Change the noList TextView message depending on the error code received */
        if (errorNumber == 429) {
            noList.setVisibility(View.VISIBLE);
            noList.setText(R.string.noList_text_view_error_message_429);
        } else if (errorNumber == 0) {
            noList.setVisibility(View.VISIBLE);
        } else if (errorNumber == 400) {
            noList.setVisibility(View.VISIBLE);
            noList.setText(R.string.noList_text_view_error_message_400);
        } else if (errorNumber == 1) {
            noList.setVisibility(View.VISIBLE);
            noList.setText(R.string.noList_text_view_error_message_1);
        } else {
            /* If no error message is returned, hide the noList TextView */
            noList.setVisibility(View.GONE);
        }
    }

    @NonNull
    @Override
    public Loader<List<NewsArticle>> onCreateLoader(int id, @Nullable Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        /* getString value from the preferences. The second parameter is the default value for this preference */
        String sortBy = sharedPreferences.getString(getString(R.string.settings_sort_by_key), getString(R.string.settings_sort_by_default));

        String language = sharedPreferences.getString(getString(R.string.settings_language_key), getString(R.string.settings_language_default));

        String countries = sharedPreferences.getString(getString(R.string.settings_countries_key), getString(R.string.settings_countries_default));


        /* parse breaks apart the URI string that's passed into its parameter */
        Uri baseUri = Uri.parse(NEWSFEED_REQUEST_URL);

        /* buildUpon prepares the parsed baseUri so we can add query parameters to it */
        Uri.Builder uriBuilder = baseUri.buildUpon();

        /* Append query parameter and its value */

        uriBuilder.appendQueryParameter("access_key", API_KEY);
        uriBuilder.appendQueryParameter("sort", sortBy);
        uriBuilder.appendQueryParameter("languages", language);
        uriBuilder.appendQueryParameter("countries", countries);

        /* Return the completed uri `http://api.mediastack.com/v1/news?&limit=40&keywords=covid&access_key=API_KEY&sort=published_desc&languages=en&countries=ca' */
        return new NewsFeedLoader(getContext(), uriBuilder.toString());

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsArticle>> loader, List<NewsArticle> newsArticles) {
        /* Clear the adapter of previous news feed data */
        newsAdapter.clear();

        /* If there is a valid list of articles, then add them to the adapter's data set */
        if (newsArticles != null && !newsArticles.isEmpty()) {
            newsAdapter.addAll(newsArticles);
            noList.setVisibility(View.GONE);

            /* If the noList TextView contains HTTP, update the message */
        } else if (noList.getText().toString().contains("HTTP")) {
            errorCode = 0;
            setNoListText();

        } else if (errorCode == 429) {
            setNoListText();
        } else {
            /* Hide the noList TextView if no parameters are met */
            errorCode = 1;
            setNoListText();
        }
        /* Hide the loading spinner after completing */
        binding.loadingSpinner.setVisibility(View.GONE);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsArticle>> loader) {
        /* Loader reset, so we can clear out the existing data */
        newsAdapter.clear();

    }

    @Override
    public void onItemClick(NewsArticle newsArticle) {
        /* If item is clicked within the RecyclerView create a new intent and open the URL in a browser */
        String url = newsArticle.getWebsite();
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
        startActivity(intent);
    }

}
