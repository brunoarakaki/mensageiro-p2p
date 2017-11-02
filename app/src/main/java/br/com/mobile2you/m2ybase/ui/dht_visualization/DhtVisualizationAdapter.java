package br.com.mobile2you.m2ybase.ui.dht_visualization;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import br.com.mobile2you.m2ybase.ui.base.BaseRecyclerViewAdapter;

/**
 * Created by Bruno on 02-Nov-17.
 */

public class DhtVisualizationAdapter extends BaseRecyclerViewAdapter {

    public DhtVisualizationAdapter(View.OnClickListener tryAgainClickListener) {
        super(tryAgainClickListener);
    }

    @Override
    public int getDisplayableItemsCount() {
        return 0;
    }

    @Override
    public void onBindRecyclerViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        return null;
    }
}
