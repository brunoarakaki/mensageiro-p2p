package br.com.mobile2you.m2ybase.ui.main;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.remote.models.PollsResponse;
import br.com.mobile2you.m2ybase.data.remote.models.PostsResponse;

/**
 * Created by mobile2you on 28/11/16.
 */

public class MainAdapter extends BaseAdapter{
    private List<PollsResponse> mPollsResponses = new ArrayList<>();
    private List<PostsResponse> mPostsResponses = new ArrayList<>();
    private Context mContext;


    public MainAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return mPostsResponses.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void setPollsResponses(List<PollsResponse> pollsResponses) {
        mPollsResponses = pollsResponses;
        notifyDataSetChanged();
    }

    public void setPosts(List<PostsResponse> posts) {
        mPostsResponses = posts;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = ((Activity)mContext).getLayoutInflater()
                .inflate(R.layout.item_list, viewGroup, false);
        ((TextView) view.findViewById(R.id.contact_name)).setText(mPostsResponses.get(i).getBody());
        ((TextView) view.findViewById(R.id.question)).setText(mPostsResponses.get(i).getBody());
        ((ImageView) view.findViewById(R.id.picture)).setImageResource(R.drawable.ic_smiley_face);
        return view;
    }

}
