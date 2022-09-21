package com.veltus.covidnewstracking.ObjectClass;

public class CovidCase {

    private final int newCases;

    private final String location;

    private final String date;

    private final int totalCases;

    private final double deaths;

    public CovidCase(int newCases, String location, String date, int totalCases, double deaths) {
        this.newCases = newCases;
        this.location = location;
        this.date = date;
        this.totalCases = totalCases;
        this.deaths = deaths;
    }

    public int getNewCases() {
        return newCases;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public double getDeaths() {
        return deaths;
    }
}
