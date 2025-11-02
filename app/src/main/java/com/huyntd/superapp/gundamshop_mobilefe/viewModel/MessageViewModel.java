package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.SendMessageRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.MessageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.MessageRepository;
import com.huyntd.superapp.gundamshop_mobilefe.utils.AppStompClient;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageViewModel extends ViewModel {

     final MessageRepository repository;
     final LiveData<List<MessageResponse>> messageList;
     final LiveData<MessageResponse> newMessageResponse;

    public MessageViewModel(MessageRepository repository) {
        this.repository = repository;
        this.messageList = repository.getMessages();

        // startListeningForChat phía repo dùng để subscribe destination topic/conversation/id (Quản lý vòng đời)
        repository.startListeningForChat();

        // Lấy LiveData tin nhắn mới (cho Activity quan sát)
        this.newMessageResponse = repository.getNewIncomingMessage();
    }

    public void sendMessage(int conversationId, String content) {
        try {
            if (content == null || content.trim().isEmpty()) {
                return; // Không gửi tin nhắn rỗng
            }
            repository.sendMessage(conversationId, content.trim());
        } catch (Exception e) {

        }
    }

     public LiveData<List<MessageResponse>> getMessageList() {
         return messageList;
     }

    public LiveData<MessageResponse> getNewIncomingMessage() {
        return newMessageResponse;
    }

    // Dọn dẹp subscription khi ViewModel bị hủy
    @Override
    protected void onCleared() {
        repository.stopListeningForChat();
        super.onCleared();
        // onCleared() xóa biến tham chiếu đến MessageRepository
    }
}
