package com.huyntd.superapp.gundamshop_mobilefe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.MessageResponse;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int VIEW_TYPE_SEND = 1;
    static final int VIEW_TYPE_RECEIVE = 2;

    final int currentUserId;
    List<MessageResponse> messageList;

    public MessageAdapter(int currentUserId, List<MessageResponse> messageList) {
        this.currentUserId = currentUserId;
        this.messageList = messageList; // Dữ liệu khởi tạo
    }

    // ----------------------------------------------------
    // PHƯƠNG THỨC QUAN TRỌNG 1: XÁC ĐỊNH LOẠI VIEW
    // ----------------------------------------------------
    @Override
    public int getItemViewType(int position) {
        MessageResponse message = messageList.get(position);

        // So sánh senderId của tin nhắn với currentUserId
        if (message.getSenderId() == currentUserId) {
            return VIEW_TYPE_SEND;
        } else {
            return VIEW_TYPE_RECEIVE;
        }
    }

    // ----------------------------------------------------
    // PHƯƠNG THỨC QUAN TRỌNG 2: TẠO VIEW HOLDER TƯƠNG ỨNG
    // ----------------------------------------------------
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_SEND) {
            view = inflater.inflate(R.layout.item_message_right, parent, false);
            return new SendMessageViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_message_left, parent, false);
            return new ReceiveMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessageResponse message = messageList.get(position);

        // Kiểm tra loại ViewHolder để ép kiểu và bind dữ liệu
        if (holder.getItemViewType() == VIEW_TYPE_SEND) {
            ((SendMessageViewHolder) holder).bind(message);
        } else {
            ((ReceiveMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Phương thức cập nhật dữ liệu (cần dùng trong MVVM khi LiveData thay đổi)
    public void setMessages(List<MessageResponse> newMessageList) {
        this.messageList = newMessageList;
        notifyDataSetChanged();
    }

    private static class SendMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageContent;
        private TextView sentAt;

        public SendMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.messageTv);
            sentAt = itemView.findViewById(R.id.timeTv);
        }

        public void bind(MessageResponse message) {
            messageContent.setText(message.getContent());
            sentAt.setText(message.getSentAt().toString());
        }
    }

    private static class ReceiveMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageContent;
        private TextView sentAt;

        public ReceiveMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.messageTv);
            sentAt = itemView.findViewById(R.id.timeTv);
        }

        public void bind(MessageResponse message) {
            messageContent.setText(message.getContent());
            sentAt.setText(message.getSentAt().toString());
        }
    }

}
