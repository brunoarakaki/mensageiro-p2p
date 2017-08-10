package br.com.mobile2you.m2ybase.ui.main;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.remote.models.PostsResponse;
import br.com.mobile2you.m2ybase.ui.base.BaseRecyclerViewAdapter;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mobile2you on 28/11/16.
 */

public class MainAdapter extends BaseRecyclerViewAdapter {
    private List<PostsResponse> mPostsResponses = new ArrayList<>();

    public MainAdapter(View.OnClickListener tryAgainClickListener) {
        super(tryAgainClickListener);
    }

    public void setPosts(List<PostsResponse> posts) {
        mPostsResponses = posts;
        notifyDataChanged();
    }

    @Override
    public int getDisplayableItemsCount() {
        return mPostsResponses.size();
    }

    @Override
    public void onBindRecyclerViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ConctactViewHolder) {
            ((ConctactViewHolder) holder).SetContactView(mPostsResponses.get(position));
        }
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ConctactViewHolder(itemView);
    }

    class ConctactViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.contact_name) TextView mNameTextView;
        @BindView(R.id.question) TextView mLastMessageTextView;
        @BindView(R.id.picture) ImageView mPictureImageView;

        public ConctactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void SetContactView(PostsResponse postsResponse){
            mNameTextView.setText(postsResponse.getBody());
            mLastMessageTextView.setText(postsResponse.getBody());
            mPictureImageView.setImageResource(R.drawable.ic_smiley_face);
        }
    }

}
