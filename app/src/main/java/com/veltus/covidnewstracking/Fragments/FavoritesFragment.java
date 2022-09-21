package com.veltus.covidnewstracking.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.veltus.covidnewstracking.Activities.MainActivity;
import com.veltus.covidnewstracking.ObjectClass.NewsArticle;
import com.veltus.covidnewstracking.Adapters.RecyclerAdapterFavorites;
import com.veltus.covidnewstracking.databinding.FragmentFavoritesFeedBinding;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment implements RecyclerAdapterFavorites.OnItemClickListener {

    private static RecyclerAdapterFavorites favoritesAdapter;
    private static ArrayList<NewsArticle> favoritesArrayList;
    private FragmentFavoritesFeedBinding binding;
    private RecyclerView recyclerView;
    private List<NewsArticle> favoritesList;

    /* Give all activities and fragments access to the new favoritesArrayList */
    public static List<NewsArticle> getArticleArrayList() {
        return favoritesArrayList;
    }

    /* Give all activities and fragments access to the favoritesAdapter */
    public static RecyclerAdapterFavorites getFavoritesAdapter() {
        return favoritesAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesFeedBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        /* Create a new arraylist and load all items from the favoritesList */
        favoritesArrayList = new ArrayList<>();
        favoritesList = MainActivity.getFavoritesList();
        favoritesArrayList.addAll(favoritesList);

        setAdapter();

        return view;

    }

    public void setAdapter() {
        /* Link the RecyclerView to the RecyclerView within the fragment_favorite_feed.xml layout */
        recyclerView = binding.favoritesRecyclerView;

        /* Create a new adapter that takes an empty list of news articles as input */
        favoritesAdapter = new RecyclerAdapterFavorites(favoritesArrayList, this);

        /* Create a layout manager for the Favorites RecyclerView */
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        /* Set an ItemAnimator for the RecyclerView */
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /* Set the adapter on the RecyclerView so the list can be populated in the user interface */
        recyclerView.setAdapter(favoritesAdapter);

    }

    @Override
    public void onItemClick(NewsArticle newsArticle) {
        /* If item is clicked within the RecyclerView create a new intent and open the URL in a browser */
        String url = newsArticle.getWebsite();
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(url));
        startActivity(intent);

    }

}