package net.realvpn.android.manager.model;

/**
 * Created by nerdywoffy on 9/27/17.
 */

public class SavedServer {
    String country;
    String name;

    public SavedServer(String country, String name) {
        this.country = country;
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
