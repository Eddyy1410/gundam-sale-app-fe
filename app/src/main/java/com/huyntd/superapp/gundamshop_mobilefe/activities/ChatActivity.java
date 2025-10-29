package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.adapter.MessageAdapter;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiClient;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.databinding.ActivityChatBinding;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.MessageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.repository.MessageRepository;
import com.huyntd.superapp.gundamshop_mobilefe.utils.ChatStompClient;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.MessageViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.factory.MessageViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private ChatStompClient stompClient;
    private String TAG = "CHAT_TAG";
    private MessageAdapter messageAdapter;
    private MessageViewModel chatViewModel;
    private ApiService apiService = ApiClient.getApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Thiết lập Adapter
        RecyclerView recyclerView = binding.chatRV;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(Integer.parseInt(SessionManager.getInstance(this).getUserId()), new ArrayList<>());
        recyclerView.setAdapter(messageAdapter);

        // 2. Quan sát LiveData
        MessageRepository repository = new MessageRepository(apiService, SessionManager.getInstance(this));
        MessageViewModelFactory factory = new MessageViewModelFactory(repository);
        chatViewModel = new ViewModelProvider(this, factory).get(MessageViewModel.class);

        chatViewModel.getMessageList().observe(this, new Observer<List<MessageResponse>>() {
            @Override
            public void onChanged(List<MessageResponse> messageList) {
                if (messageList != null) {
                    // Cập nhật adapter khi dữ liệu thay đổi
                    messageAdapter.setMessages(messageList);
                    // Tự động cuộn xuống tin nhắn mới nhất
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }
        });

        stompClient = new ChatStompClient(SessionManager.getInstance(ChatActivity.this).getAuthToken());

        binding.toolbarBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 1. KẾT NỐI: Mở kết nối khi Activity hiển thị
        // Đây là thời điểm thích hợp nhất để mở kết nối real-time
        if (stompClient != null) {
            stompClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 2. NGẮT KẾT NỐI: Đóng kết nối khi Activity bị tạm dừng (người dùng thoát màn hình)
        // Việc ngắt kết nối trong onPause() là cực kỳ quan trọng để tiết kiệm pin và dữ liệu
        if (stompClient != null) {
            stompClient.disconnect();
        }
    }

    // (Tùy chọn) Vẫn giữ onDestroy() để dọn dẹp nếu có tài nguyên khác
    /*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Không cần disconnect() ở đây nếu đã làm trong onPause()
    }
    */

}