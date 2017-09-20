package br.com.mobile2you.m2ybase.ui.main;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.local.Contact;
import br.com.mobile2you.m2ybase.data.remote.models.PostsResponse;
import br.com.mobile2you.m2ybase.ui.base.BaseRecyclerViewAdapter;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mobile2you on 28/11/16.
 */

public class MainAdapter extends BaseRecyclerViewAdapter {
    private List<Contact> mContacts = new ArrayList<>();
    private OnClicked mClickListener;

    public MainAdapter(OnClicked clickListener, View.OnClickListener tryAgainClickListener) {
        super(tryAgainClickListener);
        mClickListener = clickListener;
    }

    public void setContacts(List<Contact> contacts) {
        mContacts = contacts;
        notifyDataChanged();
    }

    @Override
    public int getDisplayableItemsCount() {
        return mContacts.size();
    }

    @Override
    public void onBindRecyclerViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ConctactViewHolder) {
            ((ConctactViewHolder) holder).Bind(mContacts.get(position));
        }
    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ConctactViewHolder(itemView);
    }


    public interface OnClicked {
        void onContactClicked(Contact contact);

    }

    class ConctactViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.contact_name) TextView mNameTextView;
        @BindView(R.id.question) TextView mLastMessageTextView;
        @BindView(R.id.picture) ImageView mPictureImageView;

        public ConctactViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void Bind(final Contact contact){
            mNameTextView.setText(contact.getName());
            mLastMessageTextView.setText("Last message");
            mPictureImageView.setImageResource(R.drawable.ic_smiley_face);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onContactClicked(contact);
                }
            });
        }
    }

}
