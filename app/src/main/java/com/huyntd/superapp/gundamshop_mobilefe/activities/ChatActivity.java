package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.databinding.ActivityChatBinding;
import com.huyntd.superapp.gundamshop_mobilefe.utils.ChatStompClient;

public class ChatActivity extends AppCompatActivity {
    private ActivityChatBinding binding;
    private ChatStompClient stompClient;
    private String TAG = "CHAT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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