package com.huyntd.superapp.gundamshop_mobilefe.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompHeader;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatStompClient {
    static final String TAG = "CHAT_STOMP_CLIENT";
    final String jwtToken;
    final String serverUrl = "ws://192.168.137.1q:8080/ws-native";

    // Phải thêm maven { url = uri("https://jitpack.io") } trong settings.gradle.kts (Project Settings)
    // dependencyResolutionManagement {
    //         ....repositories {
    //                   maven { url = uri("https://jitpack.io") }
    // }}
    //Tự động thêm các header, dòng ngắt, và ký tự NULL (^@) vào các frame CONNECT, SUBSCRIBE, SEND, và DISCONNECT
    private StompClient stompClient;

    // Disposable: một Disposable đại diện cho một luồng công việc đang chạy (ví dụ: luồng lắng nghe tin nhắn trên kênh /topic/conversation/123).
    // CompositeDisposable: Là một thùng chứa (Container) tập hợp nhiều Disposable lại.
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ChatStompClient(String jwtToken) {
        this.jwtToken = jwtToken;
        // Chỉ định URL server và loại Provider (ví dụ: JWS - Java WebSocket Standard)
        // nhà máy (Factory) tạo ra đối tượng StompClient
        stompClient = Stomp.over(Stomp.ConnectionProvider.JWS, serverUrl);
    }

    public void connect() {
        // 1. Chuẩn bị Headers cho Frame CONNECT
        List<StompHeader> headers = new ArrayList<>();
        headers.add(new StompHeader("accept-version", "1.1,1.0"));
        headers.add(new StompHeader("Authorization", "Bearer " + jwtToken));

        // 2. Theo dõi trạng thái kết nối
        // mỗi lần client gửi tin nhắn (sendMessage) thường không được tính là một LifecycleEvent
        // LifecycleEvent chỉ báo hiệu những thay đổi ở cấp độ trạng thái toàn cục của kết nối (kết nối có đang hoạt động, đã mở, đã đóng, đã lỗi nghiêm trọng không).
        Disposable lifecycleDisposable = stompClient.lifecycle().subscribe(lifecycleEvent -> {
            switch (lifecycleEvent.getType()) {
                case OPENED:
                    Log.i(TAG, "STOMP Connection Opened!");
                    subscribeToTopics(); // Kết nối STOMP thành công
                    break;
                case ERROR:
                    Log.e(TAG, "STOMP Connection Error: " + lifecycleEvent.getException().getMessage());
                    // Xử lý logic Reconnect ở đây
                    // Lỗi cấp vòng đời LifecycleEvent.Type.ERROR
                    // Mất kết nối mạng, server bị sập, server từ chối Frame CONNECT vì lỗi cú pháp hoặc lỗi xác thực nặng (ví dụ: Interceptor ném lỗi trước khi gán Principal).
                    break;
                case CLOSED:
                    Log.w(TAG, "STOMP Connection Closed.");
                    break;
            }
        });


        Log.i(TAG, "Thêm lifecycle vào compositeDisposable");
        // dùng compositeDisposable để bọc toàn bộ event gọi đến từ client thành 1 luồng
        // Khi disconnect -> thì hủy nguyên 1 luồng tránh memory leak
        compositeDisposable.add(lifecycleDisposable);

        // 3. Bắt đầu kết nối WebSocket/STOMP
        stompClient.connect(headers);
    }

    private void subscribeToTopics() {
        // Subscribe vào kênh lỗi cá nhân
        Disposable errorDisposable = stompClient.topic("/user/queue/errors").subscribe(stompMessage -> {
            Log.e(TAG, "Error received: " + stompMessage.getPayload());
            // Xử lý thông báo lỗi từ WebSocketErrorHandler
        });

        // Subscribe vào kênh Conversation (Thay ID 123 bằng biến thực tế)
        Disposable chatDisposable = stompClient.topic("/topic/conversation/123").subscribe(stompMessage -> {
            Log.d(TAG, "Message received: " + stompMessage.getPayload());
            // Xử lý logic hiển thị tin nhắn mới
        });

        compositeDisposable.add(errorDisposable);
        compositeDisposable.add(chatDisposable);
    }

    public void sendMessage(int conversationId, String content) {
        if (!stompClient.isConnected()) {
            Log.e(TAG, "STOMP Client not connected. Cannot send message.");
            return;
        }

        String messagePayload = String.format("{\"conversationId\": %d, \"content\": \"%s\"}", conversationId, content);

        // Gửi Frame SEND đến /app/send.message
        stompClient.send("/app/send.message", messagePayload).subscribe(
                () -> Log.d(TAG, "Message sent successfully"),
                error -> Log.e(TAG, "Error sending message: " + error.getMessage())
        );
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }

}
