package io.emqtt.emqandroidtoolkit.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.emqtt.emqandroidtoolkit.R;
import io.emqtt.emqandroidtoolkit.model.Connection;
import io.emqtt.emqandroidtoolkit.model.Subscription;
import io.emqtt.emqandroidtoolkit.net.MQTTManager;
import io.emqtt.emqandroidtoolkit.ui.activity.ConnectionActivity;
import io.emqtt.emqandroidtoolkit.ui.activity.DashboardActivity;
import io.emqtt.emqandroidtoolkit.util.RealmHelper;

/**
 * ClassName: ConnectionAdapter
 * Desc:
 * Created by zhiw on 2017/3/22.
 */

public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ConnectionViewHolder> {

    private List<Connection> mConnectionList;
    private Context mContext;

    public ConnectionAdapter(List<Connection> connectionList) {
        mConnectionList = connectionList;
    }


    @Override
    public ConnectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_connection, parent, false);
        mContext = parent.getContext();
        return new ConnectionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ConnectionViewHolder holder, int position) {
        final Connection connection = mConnectionList.get(position);
        final int pos = holder.getAdapterPosition();
        holder.clientIdText.setText(connection.getClientId());
        holder.serverText.setText(connection.getHost() + ":" + connection.getPort());

        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v, pos, connection);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DashboardActivity.openActivity(mContext,connection);

            }
        });


    }

    private void showMenu(View v, final int position, final Connection connection) {
        final PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.inflate(R.menu.menu_connection_item);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        ConnectionActivity.openActivity(mContext, connection);

                        return true;
                    case R.id.action_delete:
                        MQTTManager.getInstance().removeClient(connection.getId());
                        RealmHelper.getInstance().deleteTopic(Subscription.class,connection.getId());
                        RealmHelper.getInstance().delete(connection);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, getItemCount());
                        return true;
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return mConnectionList.size();
    }


    static class ConnectionViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.client_id) TextView clientIdText;
        @BindView(R.id.server) TextView serverText;
        @BindView(R.id.connection_state) ImageView connectionStateImage;
        @BindView(R.id.more) ImageView more;

        ConnectionViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
