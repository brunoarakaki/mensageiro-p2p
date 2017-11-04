package br.com.mobile2you.m2ybase.ui.dht_visualization;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.ui.base.BaseRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bruno on 02-Nov-17.
 */

public class DhtVisualizationAdapter extends BaseRecyclerViewAdapter {

    public DhtVisualizationAdapter(View.OnClickListener tryAgainClickListener) {
        super(tryAgainClickListener);
    }

    @Override
    public int getDisplayableItemsCount() {
        return 5;
    }

    @Override
    public void onBindRecyclerViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof DhtNodeViewHolder) {
            ((DhtNodeViewHolder) holder).Bind("Nome do nó", "Endereço do nó");
        }
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dht_node, parent, false);
        return new DhtNodeViewHolder(itemView);
    }

    class DhtNodeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.node_name_text)
        TextView mNodeNameTextView;
        @BindView(R.id.node_address_text)
        TextView mNodeAddressTextView;

        public DhtNodeViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void Bind(final String name, final String address){
            mNodeNameTextView.setText(name);
            mNodeAddressTextView.setText(address);
        }

    }
}
