package com.bhola.livevideochat4;
public class CountryInfo_Model {
    private String nationality;
    private String flagUrl;
    private String country;
    private boolean isSelected;

    // Constructors
    public CountryInfo_Model() {
    }

    public CountryInfo_Model(String nationality, String flagUrl, String country, boolean isSelected) {
        this.nationality = nationality;
        this.flagUrl = flagUrl;
        this.country = country;
        this.isSelected = isSelected;
    }

    // Getters and setters
    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


}
