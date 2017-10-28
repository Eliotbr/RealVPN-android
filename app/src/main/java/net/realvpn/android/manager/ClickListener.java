package net.realvpn.android.manager;

import android.view.View;

/**
 * Created by nerdywoffy on 9/27/17.
 */

public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}