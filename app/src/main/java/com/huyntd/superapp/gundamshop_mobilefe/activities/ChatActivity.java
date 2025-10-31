package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

    // Khai báo hằng số để đồng bộ key (Extra)
    public static final String EXTRA_CUSTOMER_ID = "CUSTOMER_ID";
    public static final String EXTRA_CUSTOMER_NAME = "CUSTOMER_NAME";
    private String customerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // --- Tránh vùng camera (notch) ---
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(0, bars.top, 0, 0);
            return insets;
        });

        Intent intent = getIntent();
        if (intent != null) {
            Log.i(TAG, "customerId: "+EXTRA_CUSTOMER_ID);
            customerId = intent.getStringExtra(EXTRA_CUSTOMER_ID);
            if (SessionManager.getInstance(this).getRole().equals("STAFF"))
                binding.toolbarTitleTv.setText(intent.getStringExtra(EXTRA_CUSTOMER_NAME));
        }

        // 1. Thiết lập Adapter
        RecyclerView recyclerView = binding.chatRV;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(SessionManager.getInstance(this), new ArrayList<>());
        recyclerView.setAdapter(messageAdapter);

        // 2. Quan sát LiveData
        MessageRepository repository = new MessageRepository(apiService, customerId);
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