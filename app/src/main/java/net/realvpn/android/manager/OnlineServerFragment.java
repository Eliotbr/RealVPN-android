package net.realvpn.android.manager;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import net.realvpn.android.manager.adapter.OnlineServerAdapter;
import net.realvpn.android.manager.api.ApiService;
import net.realvpn.android.manager.api.RetroClient;
import net.realvpn.android.manager.api.Server;
import net.realvpn.android.manager.model.OnlineServer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineServerFragment extends Fragment {
    List<OnlineServer> serverList = new ArrayList<>();
    MaterialRefreshLayout layoutOnline;
    RecyclerView rvOnline;
    OnlineServerAdapter adapter;
    View v;

    public OnlineServerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_online_server, container, false);

        rvOnline = v.findViewById(R.id.rvOnline);
        LinearLayoutManager lm = new LinearLayoutManager(v.getContext());

        layoutOnline = v.findViewById(R.id.refreshOnline);

        layoutOnline.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                getData();

                //materialRefreshLayout.finishRefresh();
            }
        });

        rvOnline.setHasFixedSize(true);
        rvOnline.setLayoutManager(lm);
        adapter = new OnlineServerAdapter(serverList);
        rvOnline.setAdapter(adapter);
        rvOnline.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rvOnline, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(getContext(), AddServerActivity.class);
                i.putExtra("title", serverList.get(position).getName());
                i.putExtra("id", serverList.get(position).getId());
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        getData();
        return v;
    }

    public void getData() {
        serverList.clear();
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null) {
            ApiService api = RetroClient.getApiService();
            Call<List<Server>> call = api.getAllServers();

            call.enqueue(new Callback<List<Server>>() {
                @Override
                public void onResponse(Call<List<Server>> call, Response<List<Server>> response) {
                    if (response.isSuccessful()) {
                        int size = response.body().size();
                        for (int i = 0; i < size; i++) {
                            serverList.add(new OnlineServer(
                                    response.body().get(i).getId(),
                                    response.body().get(i).getCountry(),
                                    response.body().get(i).getName(),
                                    response.body().get(i).getDescription()
                            ));
                        }

                        adapter.notifyDataSetChanged();
                        layoutOnline.finishRefresh();
                    } else {
                        Snackbar.make(v, "Unable to fetch Server List. Try again later", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Server>> call, Throwable t) {
                    Log.d("APP", t.getMessage());
                    Snackbar.make(v, "Unable to fetch Server List because there's no Internet Connection. Try again later", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                }
            });
        } else {
            Snackbar.make(v, "Unable to fetch Server List because there's no Internet Connection. Try again later", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            }).show();
        }
        adapter.notifyDataSetChanged();
    }

}
