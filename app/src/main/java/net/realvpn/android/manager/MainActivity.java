package net.realvpn.android.manager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import net.realvpn.android.manager.database.Connection;

import java.util.ArrayList;
import java.util.List;

import de.blinkt.openvpn.LaunchVPN;
import de.blinkt.openvpn.VpnProfile;
import de.blinkt.openvpn.core.ConnectionStatus;
import de.blinkt.openvpn.core.IOpenVPNServiceInternal;
import de.blinkt.openvpn.core.LogItem;
import de.blinkt.openvpn.core.OpenVPNManagement;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.ProfileManager;
import de.blinkt.openvpn.core.StatusListener;
import de.blinkt.openvpn.core.VpnStatus;

import static de.blinkt.openvpn.core.OpenVPNService.humanReadableByteCount;

public class MainActivity extends AppCompatActivity implements VpnStatus.ByteCountListener, VpnStatus.StateListener, VpnStatus.LogListener {
    static final int REQ_PERMISSION_STORAGE = 9;
    MaterialSpinner spn;
    ActionProcessButton btnConnect;
    Boolean isStart = false;
    boolean mBound = false;
    List<Connection> connectionList = new ArrayList<>();
    List<String> connectionName = new ArrayList<>();
    LinearLayout layoutSpeedMeter;
    TextView textUpload, textDownload;
    private IOpenVPNServiceInternal mService;
    private ServiceConnection mConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            mService = IOpenVPNServiceInternal.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
        }

    };
    private StatusListener mStatus;

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    boolean checkIsOpenVPNReady() {
        try {
            getPackageManager().getPackageInfo("net.realvpn.android.manager", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    void checkPermissions() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_PERMISSION_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERMISSION_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getApplicationContext(), "Thanks for Granting. Welcome to RealVPN Manager", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Read Write External Storage Required to Save Configuration. Please Try Again.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        VpnStatus.removeStateListener(this);
        VpnStatus.removeByteCountListener(this);
        VpnStatus.removeLogListener(this);
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        regenerateSpinner();
        VpnStatus.addStateListener(this);
        VpnStatus.addByteCountListener(this);
        VpnStatus.addLogListener(this);

        Intent intent = new Intent(this, OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void regenerateSpinner() {
        connectionName.clear();
        connectionList = Connection.listAll(Connection.class);

        for (int x = 0; x < connectionList.size(); x++) {
            connectionName.add(connectionList.get(x).getName());
        }
        if (connectionName.size() == 0) {
            spn.setItems("No Server Connection Available");
            spn.setEnabled(false);
            btnConnect.setEnabled(false);
        } else {
            spn.setItems(connectionName);
            spn.setEnabled(true);
            btnConnect.setEnabled(true);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        VpnStatus.addStateListener(this);
        VpnStatus.addByteCountListener(this);
        VpnStatus.addLogListener(this);
        unbindService(mConnection);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent(getBaseContext(), OpenVPNService.class);
        intent.setAction(OpenVPNService.START_SERVICE);

        //registerReceiver(broadcastReceiver);
        spn = (MaterialSpinner) findViewById(R.id.spinnerServer);
        spn.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                Log.d("SPINNER", item.toString());
            }
        });

        layoutSpeedMeter = (LinearLayout) findViewById(R.id.speedMeterLayout);
        textUpload = (TextView) findViewById(R.id.textUpload);
        textDownload = (TextView) findViewById(R.id.textDownload);
        btnConnect = (ActionProcessButton) findViewById(R.id.buttonConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        if (!isStart) {
                         /*btnConnect.setMode(ActionProcessButton.Mode.ENDLESS);
                         btnConnect.setProgress(1);*/
                            spn.setEnabled(false);
                            startVPN();


                            isStart = true;
                        } else {
                         /*btnConnect.setMode(ActionProcessButton.Mode.ENDLESS);
                         btnConnect.setProgress(0);*/

                            stopVPN();
                            spn.setEnabled(true);
                            isStart = false;
                        }
                    }
                };
                r.run();
            }
        });


        regenerateSpinner();
    }

    void startVPN() {
        isStart = true;
        btnConnect.setMode(ActionProcessButton.Mode.ENDLESS);
        btnConnect.setProgress(1);
        spn.setEnabled(false);
        try {
            ProfileManager pm = ProfileManager.getInstance(this);
            Log.d("LoadedTitle", connectionList.get(spn.getSelectedIndex()).getConfigPath());
            VpnProfile profile = pm.getProfileByName(connectionList.get(spn.getSelectedIndex()).getConfigPath());
            Log.d("UUID", profile.getUUID().toString());
            startVPNConnection(profile, connectionList.get(spn.getSelectedIndex()).getConfigPath());
            spn.setEnabled(false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    void stopVPN() {
        stopVPNConnection();
        btnConnect.setMode(ActionProcessButton.Mode.ENDLESS);
        btnConnect.setProgress(0);
        btnConnect.setText("Connect");
        spn.setEnabled(true);

        layoutSpeedMeter.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.server_list:
                Intent slA = new Intent(MainActivity.this, ServerListActivity.class);
                startActivity(slA);
                break;
            case R.id.action_settings:
                Intent vsA = new Intent(MainActivity.this, VPNSettingsActivity.class);
                startActivity(vsA);
            default:
                break;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(broadcastReceiver);
    }

    // ------------- Functions Related to OpenVPN-------------
    public void startVPNConnection(VpnProfile vp, String configName) {
        Intent intent = new Intent(getApplicationContext(), LaunchVPN.class);
        intent.putExtra(LaunchVPN.EXTRA_KEY, vp.getUUID().toString());
        intent.setAction(Intent.ACTION_MAIN);
        startActivity(intent);
    }

    public void stopVPNConnection() {
        ProfileManager.setConntectedVpnProfileDisconnected(this);
        if (mService != null) {
            try {
                mService.stopVPN(false);
            } catch (RemoteException e) {
                VpnStatus.logException(e);
            }
        }

    }

    @Override
    public void updateByteCount(long ins, long outs, long diffIns, long diffOuts) {
        final long diffIn = diffIns;
        final long diffOut = diffOuts;
        Log.d("APP", String.valueOf(diffIn) + " - " + String.valueOf(diffOut));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textDownload.setText("Download : \n" + humanReadableByteCount(diffIn / OpenVPNManagement.mBytecountInterval, true, getResources()));
                textUpload.setText("Upload : \n" + humanReadableByteCount(diffOut / OpenVPNManagement.mBytecountInterval, true, getResources()));

            }
        });
    }

    @Override
    public void newLog(LogItem logItem) {
        Log.d("APP-LOG", logItem.getString(this));
    }

    void setConnected() {
        btnConnect.setMode(ActionProcessButton.Mode.ENDLESS);
        btnConnect.setProgress(0);
        btnConnect.setText("Connected");
        spn.setEnabled(false);

        layoutSpeedMeter.setVisibility(View.VISIBLE);
    }

    void changeStateButton(Boolean state) {
        if (state) {
            btnConnect.setMode(ActionProcessButton.Mode.ENDLESS);
            btnConnect.setProgress(0);
            btnConnect.setText("Connected");
            spn.setEnabled(false);
            layoutSpeedMeter.setVisibility(View.VISIBLE);
        } else {
            btnConnect.setMode(ActionProcessButton.Mode.ENDLESS);
            btnConnect.setProgress(0);
            btnConnect.setText("Connect");
            spn.setEnabled(true);
            layoutSpeedMeter.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level) {
        final String finalState = state;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("APP-STATE", finalState);
                if (finalState.equals("CONNECTED")) {
                    isStart = true;
                    setConnected();

                    layoutSpeedMeter.setVisibility(View.VISIBLE);
                } else {

                    layoutSpeedMeter.setVisibility(View.INVISIBLE);
                }

                if (finalState.equals("AUTH_FAILED")) {
                    if (finalState.equals("AUTH_FAILED")) {
                        Toast.makeText(getApplicationContext(), "Wrong Username or Password!", Toast.LENGTH_SHORT).show();
                    }

                    changeStateButton(false);
                }


            }
        });
    }

    @Override
    public void setConnectedVPN(String uuid) {

    }

    // ------------- EoF ------------------
}
