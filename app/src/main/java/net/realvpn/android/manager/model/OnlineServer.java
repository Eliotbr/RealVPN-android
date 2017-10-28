package net.realvpn.android.manager.model;

/**
 * Created by nerdywoffy on 9/27/17.
 */

public class OnlineServer {

    String id;
    String country;
    String name;
    String detail;

    public OnlineServer(String id, String country, String name, String detail) {
        this.id = id;
        this.country = country;
        this.name = name;
        this.detail = detail;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
