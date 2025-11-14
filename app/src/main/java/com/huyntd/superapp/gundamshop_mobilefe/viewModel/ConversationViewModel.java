package com.huyntd.superapp.gundamshop_mobilefe.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.huyntd.superapp.gundamshop_mobilefe.models.response.ConversationResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.ConversationRepository;

import java.util.List;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationViewModel extends ViewModel {

    final ConversationRepository repository;
    String TAG = "CONVERSATION_VM_TAG";

    public ConversationViewModel(ConversationRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<ConversationResponse>> getConversationList() {
        return repository.getConversations();
    }

    public LiveData<ConversationResponse> getConversationByCustomerId(String customerId) {
        return repository.getConversationByCustomerId(customerId);
    }

}
