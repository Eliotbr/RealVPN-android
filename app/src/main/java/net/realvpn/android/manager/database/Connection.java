package net.realvpn.android.manager.database;

import com.orm.SugarRecord;

/**
 * Created by nerdywoffy on 9/27/17.
 */

public class Connection extends SugarRecord {

    String name;
    String country;
    String username;
    String password;
    String configUUID;

    public Connection() {

    }

    public Connection(String name, String country, String username, String password, String configUUID) {
        this.name = name;
        this.country = country;
        this.username = username;
        this.password = password;
        this.configUUID = configUUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfigPath() {
        return configUUID;
    }

    public void setConfigPath(String configPath) {
        this.configUUID = configPath;
    }
}
