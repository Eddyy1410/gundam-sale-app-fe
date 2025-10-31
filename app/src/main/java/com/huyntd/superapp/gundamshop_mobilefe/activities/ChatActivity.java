package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.huyntd.superapp.gundamshop_mobilefe.utils.AppStompClient;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.MessageViewModel;
import com.huyntd.superapp.gundamshop_mobilefe.viewModel.factory.MessageViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.dto.StompMessage;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private String TAG = "CHAT_TAG";
    private MessageAdapter messageAdapter;
    private MessageViewModel chatViewModel;
    private ApiService apiService = ApiClient.getApiService();

    // Khai báo hằng số để đồng bộ key (Extra)
    public static final String EXTRA_CUSTOMER_ID = "CUSTOMER_ID";
    public static final String EXTRA_CUSTOMER_NAME = "CUSTOMER_NAME";
    private String customerId;

    private AppStompClient stompClient = AppStompClient.getInstance(SessionManager.getInstance(this).getAuthToken());
    private Disposable chatTopicDisp;

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

        if (stompClient.isConnected()) {
            chatTopicDisp = stompClient.subscribeDynamicTopic(
                    getIntent().getStringExtra(EXTRA_CUSTOMER_ID),
                    //Chỗ này xử lý nhận message
                    new Consumer<StompMessage>() {
                        @Override
                        public void accept(StompMessage stompMessage) throws Exception {
                            // XỬ LÝ TIN NHẮN REAL-TIME Ở ĐÂY
                            // 1. Chuyển đổi stompMessage.getPayload() thành MessageResponse
                            // 2. Cập nhật LiveData/Adapter (LƯU Ý: Phải chạy trên UI Thread)
                            runOnUiThread(() -> {
                                // Ví dụ: messageAdapter.addMessage(newMessage);
                                // Cập nhật LiveData: chatViewModel.addMessage(newMessage);
                            });
                        }
                    }
            );
        } else {
            Log.e(TAG, "onResume: StompClient is not connected!");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 1. HỦY SUBSCRIPTION ĐỘNG
        if (chatTopicDisp != null && !chatTopicDisp.isDisposed()) {
            chatTopicDisp.dispose(); // Gửi Frame UNSUBSCRIBE
        }
    }

}