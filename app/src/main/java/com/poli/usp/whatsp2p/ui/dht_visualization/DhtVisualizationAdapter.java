package com.poli.usp.whatsp2p.ui.dht_visualization;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.poli.tcc.dht.DHT;

import net.tomp2p.peers.PeerAddress;

import java.util.List;

import poli.com.mobile2you.whatsp2p.R;
import com.poli.usp.whatsp2p.ui.base.BaseRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bruno on 02-Nov-17.
 */

public class DhtVisualizationAdapter extends BaseRecyclerViewAdapter {
    private List<PeerAddress> mPeerAddresses;

    public void setPeerAddresses(List<PeerAddress> peerAddresses) {
        mPeerAddresses = peerAddresses;
        notifyDataChanged();
    }

    public DhtVisualizationAdapter(View.OnClickListener tryAgainClickListener) {
        super(tryAgainClickListener);
    }

    @Override
    public int getDisplayableItemsCount() {
        return mPeerAddresses.size();
    }

    @Override
    public void onBindRecyclerViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DhtNodeViewHolder) {
            ((DhtNodeViewHolder) holder).Bind(mPeerAddresses.get(position));
        }
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dht_node, parent, false);
        return new DhtNodeViewHolder(itemView);
    }

    class DhtNodeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.node_id_text)
        TextView mNodeIdTextView;
        @BindView(R.id.node_name_text)
        TextView mNodeNameTextView;
        @BindView(R.id.node_address_text)
        TextView mNodeAddressTextView;

        public DhtNodeViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void Bind(final PeerAddress peerAddress){
            mNodeIdTextView.setText(peerAddress.peerId().toString(true));
            mNodeNameTextView.setText(DHT.getOriginalID(peerAddress.peerId()));
            mNodeAddressTextView.setText(peerAddress.inetAddress().toString() + ":" + peerAddress.tcpPort());
    }

    }
}
