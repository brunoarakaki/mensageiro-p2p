package br.com.mobile2you.m2ybase.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.remote.models.PostsResponse;
import br.com.mobile2you.m2ybase.ui.base.BaseRecyclerViewAdapter;


import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by mobile2you on 28/11/16.
 */

public class MainAdapter extends BaseRecyclerViewAdapter {
    private List<PostsResponse> mPostsResponses = new ArrayList<>();
    private OnClicked mClickListener;

    public MainAdapter(OnClicked clickListener, View.OnClickListener tryAgainClickListener) {
        super(tryAgainClickListener);
        mClickListener = clickListener;
    }

    public void setPosts(List<PostsResponse> posts) {
        mPostsResponses = posts;
        notifyDataChanged();
    }

    private PublishSubject<View> mViewClickSubject = PublishSubject.create();

    public Observable<View> getViewClickedObservable() {
        return mViewClickSubject.asObservable();
    }

    @Override
    public int getDisplayableItemsCount() {
        return mPostsResponses.size();
    }

    @Override
    public void onBindRecyclerViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ConctactViewHolder) {
            ((ConctactViewHolder) holder).Bind(mPostsResponses.get(position));
        }
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ConctactViewHolder(itemView);
    }


    public interface OnClicked {
        void onContactClicked(PostsResponse postsResponse);

    }

    class ConctactViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.contact_name) TextView mNameTextView;
        @BindView(R.id.question) TextView mLastMessageTextView;
        @BindView(R.id.picture) ImageView mPictureImageView;

        public ConctactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void Bind(final PostsResponse postsResponse){
            mNameTextView.setText(postsResponse.getBody());
            mLastMessageTextView.setText(postsResponse.getBody());
            mPictureImageView.setImageResource(R.drawable.ic_smiley_face);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onContactClicked(postsResponse);
                }
            });
        }
    }

}
