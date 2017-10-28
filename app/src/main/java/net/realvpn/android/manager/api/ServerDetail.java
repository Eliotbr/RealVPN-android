package net.realvpn.android.manager.api;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by nerdywoffy on 9/27/17.
 */

public class ServerDetail {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("ip")
    @Expose
    private String ip;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("tcp_config")
    @Expose
    private String tcpConfig;
    @SerializedName("udp_config")
    @Expose
    private String udpConfig;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTcpConfig() {
        return tcpConfig;
    }

    public void setTcpConfig(String tcpConfig) {
        this.tcpConfig = tcpConfig;
    }

    public String getUdpConfig() {
        return udpConfig;
    }

    public void setUdpConfig(String udpConfig) {
        this.udpConfig = udpConfig;
    }
}
