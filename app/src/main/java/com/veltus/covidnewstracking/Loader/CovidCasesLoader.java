package com.veltus.covidnewstracking.Loader;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.veltus.covidnewstracking.ObjectClass.CovidCase;
import com.veltus.covidnewstracking.Queries.QueryUtils;

import java.util.List;

public class CovidCasesLoader extends AsyncTaskLoader<List<CovidCase>> {

    private String url;

    public CovidCasesLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<CovidCase> loadInBackground() {
        /* Don't perform the request if there are no URLs, or the first URL is null */
        if (url == null) {
            return null;
        }
        List<CovidCase> covidCaseList = QueryUtils.fetchCovidCaseData(url);

        return covidCaseList;
    }
}
