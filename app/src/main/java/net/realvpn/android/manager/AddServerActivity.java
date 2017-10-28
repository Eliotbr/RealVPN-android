package net.realvpn.android.manager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import net.realvpn.android.manager.api.ApiService;
import net.realvpn.android.manager.api.RetroClient;
import net.realvpn.android.manager.api.ServerDetail;
import net.realvpn.android.manager.database.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConfigParser;
import de.blinkt.openvpn.core.ProfileManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddServerActivity extends AppCompatActivity {
    ServerDetail svr = null;
    MaterialSpinner sp;
    EditText connName, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_server);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        sp = (MaterialSpinner) findViewById(R.id.spinnerMode);
        connName = (EditText) findViewById(R.id.etConnName);
        username = (EditText) findViewById(R.id.etUsername);
        password = (EditText) findViewById(R.id.etPassword);
        setSupportActionBar(toolbar);

        //Enable Strict Mode for Permitting URL Download
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setTitle("Add new '" + getIntent().getStringExtra("title") + "'");
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveServer();
            }
        });

        getDetail();
    }

    private void saveServer() {
        try {
            String dlLink = getDownloadLink();
            String connUUID = "";
            String title = "";

            try {
                URL url = new URL(getDownloadLink());
                Log.d("URL", getDownloadLink());
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                ConfigParser cp = new ConfigParser();
                cp.parseConfig(in);
                VpnProfile vp = cp.convertProfile();
                ProfileManager vpl = ProfileManager.getInstance(this);

                final String alphabet = "0123456789ABCDE";
                final int N = alphabet.length();

                Random r = new Random();
                for (int i = 0; i < 64; i++) {
                    title = title + alphabet.charAt(r.nextInt(N));
                }


                vp.mName = title;
                vp.mUsername = username.getText().toString();
                vp.mPassword = password.getText().toString();
                Log.d("CONFIG", vp.toString());
                vpl.addProfile(vp);
                vpl.saveProfile(this, vp);
                vpl.saveProfileList(this);
                Log.d("TITLE", title);
                if (vp.getUUID().toString() != null) {
                    connUUID = vp.getUUID().toString();
                    Log.d("UUID", vp.getUUIDString());
                } else {
                    throw new Exception("Cannot Download Configuration!, Aborted");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();

            }
            Connection c = new Connection(connName.getText().toString(), svr.getCountry(), username.getText().toString(), password.getText().toString(), title);
            c.save();
            Toast.makeText(AddServerActivity.this, "Connection Saved!", Toast.LENGTH_SHORT).show();
            setResult(1);
            finish();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;

        }
    }

    private void getDetail() {
        final ProgressDialog pd = ProgressDialog.show(AddServerActivity.this, "Fetching Manifest", "Please Wait..", true);
        ApiService api = RetroClient.getApiService();
        Call<ServerDetail> call = api.getServerDetail(Integer.parseInt(getIntent().getStringExtra("id")));
        call.enqueue(new Callback<ServerDetail>() {
            @Override
            public void onResponse(Call<ServerDetail> call, Response<ServerDetail> response) {
                List<String> s = new ArrayList<String>();


                if (response.isSuccessful()) {
                    svr = response.body();
                    if (!svr.getTcpConfig().equals("")) {
                        s.add("TCP");
                    }

                    if (!svr.getTcpConfig().equals("")) {
                        s.add("UDP");
                    }

                    if (s.size() == 0) {
                        pd.cancel();
                        Toast.makeText(AddServerActivity.this, "No config that can be used for this device, try again later.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    sp.setItems(s);
                    pd.cancel();
                } else {
                    pd.cancel();
                    Toast.makeText(AddServerActivity.this, "Unable to fetch Server, Check your connection and try again.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ServerDetail> call, Throwable t) {
                pd.cancel();
                Toast.makeText(AddServerActivity.this, "Unable to fetch Server, Check your connection and try again.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    private String getDownloadLink() {
        String selectedMode = sp.getItems().get(sp.getSelectedIndex()).toString();
        if (selectedMode.equals("TCP")) {
            return svr.getTcpConfig();
        } else if (selectedMode.equals("UDP")) {
            return svr.getUdpConfig();
        }
        return svr.getUdpConfig();
    }

}
