package net.realvpn.android.manager;


import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;

import net.realvpn.android.manager.adapter.SavedServerAdapter;
import net.realvpn.android.manager.database.Connection;
import net.realvpn.android.manager.model.SavedServer;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedServerFragment extends Fragment {
    List<SavedServer> serverList = new ArrayList<>();
    List<Connection> connectionList = new ArrayList<>();
    RecyclerView rvSaved;
    SavedServerAdapter adapter;
    MaterialRefreshLayout layoutSaved;

    public SavedServerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_saved_server, container, false);
        layoutSaved = v.findViewById(R.id.refreshSaved);
        rvSaved = v.findViewById(R.id.rvSaved);
        LinearLayoutManager lm = new LinearLayoutManager(v.getContext());

        layoutSaved.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                //Do Anything
                getData();
                //materialRefreshLayout.finishRefresh();
            }
        });

        rvSaved.setHasFixedSize(true);
        rvSaved.setLayoutManager(lm);
        adapter = new SavedServerAdapter(serverList);
        rvSaved.setAdapter(adapter);
        rvSaved.addOnItemTouchListener(new RecyclerTouchListener(getContext(), rvSaved, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, final int position) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(v.getContext());
                } else {
                    builder = new AlertDialog.Builder(v.getContext());
                }
                builder.setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Connection.findById(Connection.class, connectionList.get(position).getId()).delete();
                                    Snackbar.make(v, "Connection deleted successfully", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                                    getData();
                                } catch (Exception ex) {
                                    Snackbar.make(v, "Unable to Delete connection", Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                        }
                                    }).show();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
        }));

        getData();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        connectionList.clear();
        serverList.clear();
        connectionList = Connection.listAll(Connection.class);
        for (int x = 0; x < connectionList.size(); x++) {
            serverList.add(new SavedServer(connectionList.get(x).getCountry(), connectionList.get(x).getName()));
        }
        adapter.notifyDataSetChanged();
        layoutSaved.finishRefresh();
    }

}
