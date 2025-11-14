package com.huyntd.superapp.gundamshop_mobilefe.viewModel.factory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.huyntd.superapp.gundamshop_mobilefe.repository.MessageRepository;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.MessageViewModel;

import lombok.NonNull;

public class MessageViewModelFactory implements ViewModelProvider.Factory {

    private final MessageRepository repository;

    // Factory nhận các tham số cần thiết
    public MessageViewModelFactory(MessageRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MessageViewModel.class)) {
            // Đây là nơi tạo instance ViewModel bằng cách truyền Repository vào
            return (T) new MessageViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
