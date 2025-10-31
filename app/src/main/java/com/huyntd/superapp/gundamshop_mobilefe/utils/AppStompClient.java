package com.huyntd.superapp.gundamshop_mobilefe.utils;

import android.util.Log;

import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Notification;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.LifecycleEvent;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppStompClient {
    private static AppStompClient instance;
    static final String TAG = "CHAT_STOMP_CLIENT";
    final String jwtToken;
    final String serverUrl = "ws://10.0.2.2:8080/ws-native";

    // Phải thêm maven { url = uri("https://jitpack.io") } trong settings.gradle.kts (Project Settings)
    // dependencyResolutionManagement {
    //         ....repositories {
    //                   maven { url = uri("https://jitpack.io") }
    // }}
    //Tự động thêm các header, dòng ngắt, và ký tự NULL (^@) vào các frame CONNECT, SUBSCRIBE, SEND, và DISCONNECT
    StompClient stompClient;

    // Disposable: một Disposable đại diện cho một luồng công việc đang chạy (ví dụ: luồng lắng nghe tin nhắn trên kênh /topic/conversation/123).
    // CompositeDisposable: Là một thùng chứa (Container) tập hợp nhiều Disposable lại.
    CompositeDisposable persistentDisposable = new CompositeDisposable();

    private AppStompClient(String jwtToken) {
        this.jwtToken = jwtToken;
        // Chỉ định URL server và loại Provider (ví dụ: JWS - Java WebSocket Standard)
        // nhà máy (Factory) tạo ra đối tượng StompClient
        this.stompClient = Stomp.over(Stomp.ConnectionProvider.JWS, serverUrl);
    }

    public static AppStompClient getInstance(String jwtToken) {
            if (instance == null) {
                synchronized (AppStompClient.class) {
                    // Chỉ khởi tạo lần đầu tiên
                    if (instance == null) {
                        instance = new AppStompClient(jwtToken);
                    }
                }
            }
            return instance;
    }

    public static void clearInstance() {
        instance.disconnect(); // Đảm bảo disconnect trước khi hủy
        instance = null;
    }

    /**
     * Trả về luồng trạng thái kết nối để UI (Activity/Fragment) lắng nghe và phản ứng.
     * UI sẽ subscribe luồng này để hiển thị trạng thái "Online", "Connecting", "Error".
     */
    public Flowable<LifecycleEvent> getLifecycle() {
        return stompClient.lifecycle();
    }

    public void connect() {
        // 1. Chuẩn bị Headers cho Frame CONNECT
        List<StompHeader> handshakeHeaders = new ArrayList<>();
        handshakeHeaders.add(new StompHeader("accept-version", "1.1,1.0"));
        handshakeHeaders.add(new StompHeader("Authorization", "Bearer " + jwtToken));

        // 2. Theo dõi trạng thái kết nối
        // mỗi lần client gửi tin nhắn (sendMessage) thường không được tính là một LifecycleEvent
        // LifecycleEvent chỉ báo hiệu những thay đổi ở cấp độ trạng thái toàn cục của kết nối (kết nối có đang hoạt động, đã mở, đã đóng, đã lỗi nghiêm trọng không).
        Disposable lifecycleDisp = stompClient.lifecycle()
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            Log.i(TAG, "STOMP Connection Opened!");
                            subscribePersistentTopics();
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
        persistentDisposable.add(lifecycleDisp);

        // 3. Bắt đầu kết nối WebSocket/STOMP
        stompClient.connect(handshakeHeaders);
    }

    /**
     * SUBSCRIBE các kênh BỀN VỮNG (Persistent Topics) chỉ chạy một lần sau khi connect.
     */
    private void subscribePersistentTopics() {
        Disposable notificationDisp = stompClient.topic("/user/queue/notifications")
                .subscribe(stompMessage -> {
                    System.out.println("Notification received: "+stompMessage.getPayload());
                });
        persistentDisposable.add(notificationDisp);

        Disposable errorDisp = stompClient.topic("/user/queue/errors")
                .subscribe(stompMessage -> {
                    System.out.println("Error frame received: "+stompMessage.getPayload());
                });
        persistentDisposable.add(errorDisp);
    }

    public void sendMessage(String customerId, String jsonPayload) {
        if (!stompClient.isConnected()) {
            Log.e(TAG, "STOMP Client not connected. Cannot send message.");
            return;
        }

        // StompClient của NaikSoftware tự động thêm các header cần thiết (content-length, etc.)
        stompClient.send("/app/chat/" + customerId, jsonPayload)
                .subscribe(() -> {
                    // Hoàn thành gửi (Tùy chọn: Xử lý local message update)
                }, throwable -> {
                    // Xử lý lỗi khi gửi (Rất hiếm, thường do mạng rớt ngay lúc gửi)
                    System.err.println(TAG + " Send Error: " + throwable.getMessage());
                });
    }

    /**
     * Subscribe kênh ĐỘNG (/topic/conversation/{id}).
     * @return Disposable để Fragment/Activity tự quản lý vòng đời của kênh này.
     */
    public Disposable subscribeDynamicTopic(String customerId, Consumer<StompMessage> handler) {
        if (!stompClient.isConnected()) return Disposables.empty();

        String destination = "/topic/conversation/" + customerId;

        // Trả về Disposable để Fragment/Activity TỰ QUẢN LÝ
        return stompClient.topic(destination).subscribe(handler);
    }

    public void disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        if (persistentDisposable != null) {
            persistentDisposable.dispose();
        }
    }

    public boolean isConnected() {
        return stompClient.isConnected();
    }

}
