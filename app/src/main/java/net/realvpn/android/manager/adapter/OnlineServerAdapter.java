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
import net.realvpn.android.manager.model.OnlineServer;

import java.util.List;

/**
 * Created by nerdywoffy on 9/27/17.
 */

public class OnlineServerAdapter extends RecyclerView.Adapter<OnlineServerAdapter.ViewHolder> {
    List<OnlineServer> onlineServerList;

    public OnlineServerAdapter(List<OnlineServer> servers) {
        super();
        this.onlineServerList = servers;
    }

    @Override
    public OnlineServerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.online_card_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(OnlineServerAdapter.ViewHolder holder, int position) {
        holder.name.setText(onlineServerList.get(position).getName());
        holder.detail.setText(onlineServerList.get(position).getDetail());
        try {
            holder.flag.setImageDrawable(FlagKit.drawableWithFlag(holder.itemView.getContext(), onlineServerList.get(position).getCountry().toLowerCase()));
        } catch (Exception ex) {

        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return onlineServerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView flag;
        TextView name;
        TextView detail;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cvOnline);
            flag = itemView.findViewById(R.id.ivFlag);
            name = itemView.findViewById(R.id.tvServerName);
            detail = itemView.findViewById(R.id.tvServerDetail);
        }
    }
}
