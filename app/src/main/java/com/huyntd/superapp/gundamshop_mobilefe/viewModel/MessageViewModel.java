package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.MessageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.MessageRepository;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageViewModel extends ViewModel {

     final MessageRepository repository;
     final LiveData<List<MessageResponse>> messageList;

    public MessageViewModel(MessageRepository repository) {
        this.repository = repository;
        this.messageList = repository.getMessages();
    }

     public LiveData<List<MessageResponse>> getMessageList() {
         return messageList;
     }

}
