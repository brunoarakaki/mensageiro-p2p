package br.com.mobile2you.m2ybase.ui.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.mobile2you.m2ybase.R;
import br.com.mobile2you.m2ybase.data.remote.models.MessageResponse;
import br.com.mobile2you.m2ybase.ui.base.BaseRecyclerViewAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Bruno on 11-Aug-17.
 */

public class ChatAdapter extends BaseRecyclerViewAdapter {
    private List<MessageResponse> mMessages = new ArrayList<>();

    public ChatAdapter(View.OnClickListener tryAgainClickListener) {
        super(tryAgainClickListener);
    }

    public void setMessages(List<MessageResponse> messages) {
        mMessages = messages;
        notifyDataChanged();
    }

    public List<MessageResponse> getMessages() {
        return mMessages;
    }

    @Override
    public int getDisplayableItemsCount() {
        return mMessages.size();
    }

    @Override
    public void onBindRecyclerViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MessageViewHolder) {
            ((MessageViewHolder) holder).Bind(mMessages.get(position));
        }

    }

    @Override
    protected RecyclerView.ViewHolder getItemViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(itemView);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.message_sender) TextView mSenderTextView;
        @BindView(R.id.message_content_text) TextView mContentTextView;
        @BindView(R.id.message_timestamp) TextView mTimestampTextView;

        public MessageViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void Bind(final MessageResponse messageResponse){
            mSenderTextView.setText(messageResponse.getSender().getId());
            mContentTextView.setText(messageResponse.getPlainText());
            mTimestampTextView.setText(messageResponse.getSentAt().toString());
        }
    }
}
