package com.veltus.covidnewstracking.Fragments;

import android.content.Context;
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

import com.veltus.covidnewstracking.Loader.CovidCasesLoader;
import com.veltus.covidnewstracking.ObjectClass.CovidCase;
import com.veltus.covidnewstracking.R;
import com.veltus.covidnewstracking.Adapters.RecyclerAdapterCovid;
import com.veltus.covidnewstracking.databinding.FragmentCovidCasesBinding;

import java.util.ArrayList;
import java.util.List;

public class CovidCasesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<CovidCase>> {

    private FragmentCovidCasesBinding binding;
    private RecyclerView recyclerView;
    private boolean hasInternetConnection;
    private TextView noList;
    private ArrayList<CovidCase> covidList;
    private RecyclerAdapterCovid covidAdapter;

    /* URL for the covid cases data */
    private static final String COVID_REQUEST_URL = "https://api.opencovid.ca/summary?";

    private static final int COVID_LOADER_ID = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCovidCasesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        noList = binding.noList;

        /* Create a new list of covid cases */
        covidList = new ArrayList<>();

        setAdapter();

        /* Get a reference to the ConnectivityManager to check state of network connectivity */
        ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        /* Get details on the currently active default data network */
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        /* If there is a network connection, fetch data */
        if (networkInfo != null && networkInfo.isConnected()) {
            hasInternetConnection = true;

            /* Get a reference to the LoaderManager, in order to interact with loaders */
            LoaderManager loaderManager = LoaderManager.getInstance(this);

            /* Initialize the loader. Pass in the int ID constant defined above and pass in null for the bundle */
            loaderManager.initLoader(COVID_LOADER_ID, null, this);
        } else {
            /* If no connection display an error, set boolean to false and hide spinner */
            hasInternetConnection = false;
            binding.loadingSpinner.setVisibility(View.GONE);

            /* Update empty state with no connection error message */
            setNoListText(400);
        }

        /* Set an onRefreshListener for the fragment */
        binding.swipeLayout.setOnRefreshListener(() -> {
            /* First check to see if there is an internet connection */
            checkInternetConnection();

            /* If there is a connection hide noList TextView and restart loader */
            if (hasInternetConnection){
                setNoListText(2);
                covidAdapter.clear();
                LoaderManager.getInstance(getActivity()).restartLoader(COVID_LOADER_ID, null, CovidCasesFragment.this);
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
        ConnectivityManager connMgr = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        /* Get details on the currently active default data network */
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        /* If there is a network connection, fetch data and set boolean to true */
        if (networkInfo != null && networkInfo.isConnected()) {
            hasInternetConnection = true;

        } else {
            /* If no connection update the noList TextView and set boolean to false */
            hasInternetConnection = false;
            setNoListText(400);
        }
    }

    private void setAdapter() {
        /* Link the recyclerView to the RecyclerView within the fragment_covid_cases.xml layout */
        recyclerView = binding.covidCasesRecyclerView;

        /* Create a new adapter that takes an empty list of covid cases as input */
        covidAdapter = new RecyclerAdapterCovid(covidList);

        /* Create a layout manager for the News Feed RecyclerView */
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        /* Set an ItemAnimator for the RecyclerView */
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /* Set the adapter on the RecyclerView so the list can be populated in the user interface */
        recyclerView.setAdapter(covidAdapter);

    }

    @NonNull
    @Override
    public Loader<List<CovidCase>> onCreateLoader(int id, @Nullable Bundle args) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        /* getString value from the preferences. The second parameter is the default value for this preference */
        String loc = sharedPreferences.getString(getString(R.string.settings_region_key), getString(R.string.settings_region_default));

        /* parse breaks apart the URI string that's passed into its parameter */
        Uri baseUri = Uri.parse(COVID_REQUEST_URL);

        /* buildUpon prepares the parsed baseUri so we can add query parameters to it */
        Uri.Builder uriBuilder = baseUri.buildUpon();

        /* Append query parameter and its value */
        uriBuilder.appendQueryParameter("loc", loc);

        /* Return the completed uri `https://api.opencovid.ca/summary?loc=prov' */
        return new CovidCasesLoader(getContext(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<CovidCase>> loader, List<CovidCase> data) {
        /* Clear the adapter of previous covid case data */
        covidAdapter.clear();

        /* If there is a valid list of covid data, then add them to the adapter's data set */
        if (data != null && !data.isEmpty()) {
            covidAdapter.addAll(data);
            /* Hide the noList text */
            noList.setVisibility(View.GONE);

            /* If the noList TextView contains HTTP, update the message */
        } else if(noList.getText().toString().contains("HTTP")) {
            setNoListText(0);

        } else {
            /* Hide the noList TextView if no parameters are met */
            setNoListText(1);

        }
        /* Hide the loading spinner after completing */
        binding.loadingSpinner.setVisibility(View.GONE);

    }

    public void setNoListText(int errorNumber){
        /* Change the noList TextView message depending on the error code received */
        if(errorNumber == 429) {
            noList.setVisibility(View.VISIBLE);
            noList.setText(R.string.noList_text_view_error_message_429);
        } else if (errorNumber == 0) {
            noList.setVisibility(View.VISIBLE);
        } else if (errorNumber == 400){
            noList.setVisibility(View.VISIBLE);
            noList.setText(R.string.noList_text_view_error_message_400);
        } else if (errorNumber == 1){
            noList.setVisibility(View.VISIBLE);
            noList.setText(R.string.noList_text_view_error_message_1);
        } else {
            /* If no error message is returned, hide the noList TextView */
            noList.setVisibility(View.GONE);
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<List<CovidCase>> loader) {
        /* Loader reset, so we can clear out the existing data */
        covidAdapter.clear();
    }
}
