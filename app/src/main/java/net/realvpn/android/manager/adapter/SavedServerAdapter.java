package net.realvpn.android.manager.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwang123.flagkit.FlagKit;

import net.realvpn.android.manager.R;
import net.realvpn.android.manager.model.SavedServer;

import java.util.List;

/**
 * Created by nerdywoffy on 9/27/17.
 */

public class SavedServerAdapter extends RecyclerView.Adapter<SavedServerAdapter.ViewHolder> {
    List<SavedServer> savedServerList;

    public SavedServerAdapter(List<SavedServer> servers) {
        super();
        this.savedServerList = servers;
    }

    @Override
    public SavedServerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_card_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SavedServerAdapter.ViewHolder holder, int position) {
        holder.name.setText(savedServerList.get(position).getName());
        try {
            holder.flag.setImageDrawable(FlagKit.drawableWithFlag(holder.itemView.getContext(), savedServerList.get(position).getCountry().toLowerCase()));
        } catch (Exception ex) {

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return savedServerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView flag;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvSaved);
            flag = itemView.findViewById(R.id.ivSavedFlag);
            name = itemView.findViewById(R.id.tvSavedName);
        }
    }
}
