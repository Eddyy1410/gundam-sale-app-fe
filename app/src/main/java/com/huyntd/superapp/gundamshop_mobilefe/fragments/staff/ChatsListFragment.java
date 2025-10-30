package com.huyntd.superapp.gundamshop_mobilefe.fragments.staff;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.ConversationAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiClient;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.databinding.FragmentChatsListBinding;
import com.huyntd.superapp.gundamshop_mobilefe.repository.ConversationRepository;
import com.huyntd.superapp.gundamshop_mobilefe.utils.ChatStompClient;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.ConversationViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.factory.ConversationViewModelFactory;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatsListFragment extends Fragment {
    FragmentChatsListBinding binding;
    ChatStompClient stompClient;
    final String TAG = "CHATS_LIST_FRAGMENT_TAG";
    ApiService apiService = ApiClient.getApiService();
    ConversationAdapter conversationAdapter;
    ConversationViewModel conversationViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 1. Inflate layout bằng View Binding
        binding = FragmentChatsListBinding.inflate(inflater, container, false);

        // 2. TRẢ VỀ VIEW GỐC CỦA BINDING
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tất cả logic khởi tạo View (RecyclerView, Adapter, ViewModel, Listener) nên ở đây
        setupViewModel();
        setupRecyclerView();

        // ... Lắng nghe LiveData
        observeViewModel();
    }

    private void setupRecyclerView() {
        // Lấy User ID
        int currentUserId = Integer.parseInt(SessionManager.getInstance(getActivity()).getUserId());
        conversationAdapter = new ConversationAdapter(currentUserId, new ArrayList<>());

        // Sử dụng binding.tên_id_của_recyclerview
        binding.chatsListRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.chatsListRV.setAdapter(conversationAdapter);
    }

    private void setupViewModel() {
        ConversationRepository repository = new ConversationRepository(apiService, SessionManager.getInstance(getActivity()));
        ConversationViewModelFactory factory = new ConversationViewModelFactory(repository);

        // ViewModelProvider(this, ...) sử dụng Fragment làm LifecycleOwner
        conversationViewModel = new ViewModelProvider(this, factory).get(ConversationViewModel.class);
    }

    private void observeViewModel() {
        conversationViewModel.getConversationList().observe(getViewLifecycleOwner(), conversations -> {
            if (conversations != null) {
                conversationAdapter.setConversations(conversations);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cần dọn dẹp binding để tránh memory leak
        binding = null;
    }

}