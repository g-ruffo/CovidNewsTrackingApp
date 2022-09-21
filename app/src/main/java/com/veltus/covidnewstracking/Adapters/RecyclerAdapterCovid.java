package com.veltus.covidnewstracking.Adapters;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.veltus.covidnewstracking.ObjectClass.CovidCase;
import com.veltus.covidnewstracking.R;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class RecyclerAdapterCovid extends RecyclerView.Adapter<RecyclerAdapterCovid.MyViewHolder> {

    private List<CovidCase> covidCaseList;

    public RecyclerAdapterCovid(List<CovidCase> covidCaseList) {
        this.covidCaseList = covidCaseList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_covid, parent, false);
        return new MyViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        /* Format the new cases number and append + in front of displayed number */
        String newCases = NumberFormat.getNumberInstance(Locale.US).format(covidCaseList.get(position).getNewCases());
        holder.covidNewCases.setText("+" + newCases);

        /* Get the location of the position and set value to holder */
        String location = covidCaseList.get(position).getLocation();
        holder.covidLocation.setText(location);

        /* Convert String date from ISO_OFFSET_DATE_TIME to an OffsetDateTime object */
        String date = covidCaseList.get(position).getDate();
        LocalDate ldt = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String convertedDate = ldt.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"));

        /* Set the converted date to the date TextView*/
        holder.covidDate.setText(convertedDate);

        /* Format the total cases number and set value to holder */
        String total = NumberFormat.getNumberInstance(Locale.US).format(covidCaseList.get(position).getTotalCases());
        holder.covidTotalCases.setText(total);

        /* Format the death number and set value to holder */
        String deaths = String.valueOf(covidCaseList.get(position).getDeaths());
        holder.covidDeaths.setText(deaths);

    }

    /* Add all method that adds the article to the ArrayList */
    public void addAll(List<CovidCase> covidCaseList) {
        this.covidCaseList = covidCaseList;
        notifyDataSetChanged();
    }

    /* Clear method that clears all items in the ArrayList */
    public void clear() {
        this.covidCaseList.clear();
        notifyDataSetChanged();
    }

    /* Find the total size of the covidCaseList */
    @Override
    public int getItemCount() {
        return covidCaseList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView covidNewCases;
        private TextView covidLocation;
        private TextView covidDate;
        private TextView covidTotalCases;
        private TextView covidDeaths;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            /* Find and link the views in the list_view_covid.xml to the values in MyViewHolder */
            covidNewCases = itemView.findViewById(R.id.covid_new_cases_text_view);
            covidLocation = itemView.findViewById(R.id.covid_location_text_view);
            covidDate = itemView.findViewById(R.id.covid_date_text_view);
            covidTotalCases = itemView.findViewById(R.id.covid_total_text_view);
            covidDeaths = itemView.findViewById(R.id.covid_deaths_text_view);
        }
    }


}
