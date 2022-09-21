package com.veltus.covidnewstracking.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;

import com.veltus.covidnewstracking.Fragments.FavoritesFragment;
import com.veltus.covidnewstracking.ObjectClass.NewsArticle;
import com.veltus.covidnewstracking.SharedPreferences.UsersSharedPreferences;
import com.veltus.covidnewstracking.R;
import com.veltus.covidnewstracking.Adapters.ViewPagerAdapter;
import com.veltus.covidnewstracking.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Vibrator vibrator;
    private static List<NewsArticle> favoritesList;
    private static NavigationView navigationView;
    private ActivityMainBinding binding;
    private DrawerLayout drawerLayout;
    private ViewPager2 viewPager;

    /* Give all activities and fragments access to the saved favoritesList */
    public static List<NewsArticle> getFavoritesList() {
        return favoritesList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        navigationView = findViewById(R.id.drawer_view);

        /* Set the action bar title for the main page */
        setTitle(R.string.news_fragment_action_bar_title);

        /* Load the users favorites list from SharedPreferences */
        try {
            favoritesList = UsersSharedPreferences.loadList(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /* If the favorites list is empty then create a new Arraylist */
        if (favoritesList == null) {
            favoritesList = new ArrayList<>();
        }

        /* Set custom toolbar as ActionBar */
        setSupportActionBar(binding.toolbar);

        drawerLayout = binding.drawerLayout;

        /* Create an OnNavigationItemSelectedListener for the nav_view NavigationView */
        binding.drawerView.setNavigationItemSelectedListener(this);

        /* Create rotating hamburger menu icon on ToolBar */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        /* Hide the fragment container used by the Settings Activity */
        binding.fragmentContainer.setVisibility(View.GONE);

        /* Display the ViewPager2 for the NewsFeedFragment */
        binding.viewPager.setVisibility(View.VISIBLE);

        /* When app loads create new ViewPagerAdapter to display news fragments */
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager = binding.viewPager;
        viewPager.setAdapter(viewPagerAdapter);

        /* Set the first fragment selected in the navigation drawer */
        binding.drawerView.setCheckedItem(R.id.news_feed_menu);

        /* Listen for ViewPager changes and update the DrawerView selected item */
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setViewPagerMenuSelection(position);
            }
        });

    }

    /* Change the drawerViews selection and action bar title based on the ViewPagerAdapters position */
    public void setViewPagerMenuSelection(int position) {
        if (position == 1) {
            binding.drawerView.setCheckedItem(R.id.covid_cases_menu);
            setTitle(R.string.covid_cases_fragment_action_bar_title);

        } else {
            binding.drawerView.setCheckedItem(R.id.news_feed_menu);
            setTitle(R.string.news_fragment_action_bar_title);

        }
    }


    @Override
    public void onBackPressed() {
        /* Hide the settings fragment container and display the ViewPager2 view*/
        binding.fragmentContainer.setVisibility(View.GONE);
        binding.viewPager.setVisibility(View.VISIBLE);

        /* Update the DrawerView menu selection */
        int position = viewPager.getCurrentItem();
        setViewPagerMenuSelection(position);

        /* Check to see if Drawer is open and if true close drawer on back press */
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            /* If Drawer is not open and the back button is pressed, close activity as normal */
            super.onBackPressed();
        }
    }

    /* Set method for the OnNavigationItemSelectedListener */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        /* Check the selected resource item ID and replace the current fragment_container with the correct corresponding page fragment */
        switch (item.getItemId()) {
            case R.id.news_feed_menu:
                viewPager.setCurrentItem(0);
                binding.fragmentContainer.setVisibility(View.GONE);
                binding.viewPager.setVisibility(View.VISIBLE);
                break;
            case R.id.covid_cases_menu:
                viewPager.setCurrentItem(1);
                binding.fragmentContainer.setVisibility(View.GONE);
                binding.viewPager.setVisibility(View.VISIBLE);
                break;
            case R.id.favorites_menu:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FavoritesFragment()).addToBackStack(null).commit();
                binding.fragmentContainer.setVisibility(View.VISIBLE);
                binding.viewPager.setVisibility(View.GONE);
                setTitle(R.string.favorite_fragment_action_bar_title);
                break;
            case R.id.preferences_menu:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
        }
        /* Close drawer when new fragment/ activity is opened*/
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}