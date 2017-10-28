package net.realvpn.android.manager.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by nerdywoffy on 9/27/17.
 */

public interface ApiService {
    /*
  Retrofit get annotation with our URL
  And our method that will return us the List of ContactList
  */
    @GET("allServers")
    Call<List<Server>> getAllServers();

    @GET("serverDetail")
    Call<ServerDetail> getServerDetail(
            @Query("id") int id
    );
}
