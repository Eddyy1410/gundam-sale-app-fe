package com.huyntd.superapp.gundamshop_mobilefe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ConversationResponse;
import com.huyntd.superapp.gundamshop_mobilefe.utils.DateUtils;

import java.util.List;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    final int staffId;
    List<ConversationResponse> conversationList;

    public ConversationAdapter(int staffId, List<ConversationResponse> list) {
        this.staffId = staffId;
        this.conversationList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_chat, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ConversationResponse conversation = conversationList.get(position);
        ((ConversationViewHolder) holder).bind(conversation);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public void setConversations(List<ConversationResponse> newConversationList) {
        this.conversationList = newConversationList;
        notifyDataSetChanged();
    }

    class ConversationViewHolder extends RecyclerView.ViewHolder {
        private TextView customerName;
        private TextView lastestMessage;
        private TextView timeStamp;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            customerName = itemView.findViewById(R.id.customerNameTv);
            lastestMessage = itemView.findViewById(R.id.lastMessageTv);
            timeStamp = itemView.findViewById(R.id.chatTimestampTv);
        }

        public void bind(ConversationResponse conversation) {
            customerName.setText(conversation.getCustomerName());
            if (conversation.getLastestSenderId() == staffId)
                lastestMessage.setText("Bạn: "+conversation.getLatestMessageContent());
            else lastestMessage.setText("Khách: "+conversation.getLatestMessageContent());
            timeStamp.setText(DateUtils.formatChatTimestamp(conversation.getLatestMessageSentAt()));
        }
    }
}
