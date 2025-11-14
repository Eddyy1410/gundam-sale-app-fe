package com.huyntd.superapp.gundamshop_mobilefe.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiClient;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.CountResponse;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.utils.AppStompClient;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRepository {


    ApiService apiService = ApiClient.getApiService();

    final AppStompClient stompClient;
    final String currentUserId;

    String TAG = "NOTIFICATION_REPO";
    Gson gson = new Gson();

    MutableLiveData<Integer> chatBadgeCount = new MutableLiveData<>(0);
    MutableLiveData<String> generalNotification = new MutableLiveData<>();

    // --- CÁC PHƯƠNG THỨC LẤY DỮ LIỆU BAN ĐẦU (REST API) ---

    public void fetchInitialBadgeCount() {
        // TODO: Gọi API REST để tải số đếm ban đầu
        // Hiện tại: Giả định là 0
        // Sau khi API call thành công, gọi chatBadgeCount.postValue(result);
        apiService.countUnreadMessages(Integer.parseInt(currentUserId)).enqueue(new Callback<ApiResponse<CountResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CountResponse>> call, Response<ApiResponse<CountResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "onResponse: "+response);
                    chatBadgeCount.postValue(response.body().getResult().getCount());
                } else {
                    chatBadgeCount.postValue(-1);
                    Log.e(TAG, "onResponse: "+response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CountResponse>> call, Throwable t) {
                chatBadgeCount.postValue(-1);
                Log.e(TAG, "onFailure: ", t);
            }
        });
    }

    // --- CÁC PHƯƠNG THỨC QUẢN LÝ REAL-TIME (STOMP) ---

    /**
     * Bắt đầu lắng nghe các topic STOMP bền vững (như badge count).
     * Phương thức này sẽ được gọi khi NotificationViewModel được khởi tạo.
     */
    public void startListeningForPersistenQueues() {

        Log.i(TAG, "startListeningForPersistentTopics: Đã vào đây");

        // 1. Theo dõi Badge Count (Chat) từ Topic "/user/queue/unread-messages"
        // Lấy luồng dữ liệu thô từ AppStompClient
        Disposable unreadMessageDisp = stompClient.getUnreadMessagesQueue()
                // RxJava: Chuyển sang luồng background để xử lý JSON
                .subscribeOn(Schedulers.io())
                .subscribe(stompMessage -> {
                    try {
                        Log.i(TAG, "Nhận được message từ /user/queue/unread-message");
                        String payload = stompMessage.getPayload();
                        CountResponse dto = gson.fromJson(payload, CountResponse.class);
                        Log.i(TAG, "unread-message count listener: "+dto);

                        // Chỉ xử lý nếu là loại badge chat
                        if ("UNREAD_MESSAGE".equals(dto.getType())) {
                            // Đẩy dữ liệu lên LiveData (sử dụng postValue vì đang ở background thread)
                            chatBadgeCount.postValue(dto.getCount());
                            Log.i(TAG, "Badge Count Updated via STOMP: " + dto.getType());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Lỗi phân tích JSON UnreadCountDTO: ", e);
                    }
                }, throwable -> {
                    // Xử lý lỗi khi subscribe hoặc nhận dữ liệu từ topic này
                    Log.e(TAG, "Lỗi subscription /user/queue/unread-messages", throwable);
                });

        // Lưu disposable vào chung với persistentDisposable của AppStompClient để quản lý
        stompClient.addPersistentDisposable(unreadMessageDisp);

        // 2. (Tùy chọn) Theo dõi thông báo sự kiện chung của ứng dụng
        Disposable notificationDisp = stompClient.getNotificationsQueue()
                .subscribeOn(Schedulers.io())
                .subscribe(stompMessage -> {
                    // Xử lý thông báo sự kiện (ví dụ: thông báo đơn hàng mới)
                    generalNotification.postValue(stompMessage.getPayload());
                    Log.i(TAG, "General Notification received.");
                }, throwable -> {
                    Log.e(TAG, "Lỗi subscription /user/queue/notifications", throwable);
                });

        stompClient.addPersistentDisposable(notificationDisp);
    }

    // --- CÁC PHƯƠNG THỨC GET LIVE DATA ---

    public LiveData<Integer> getChatBadgeCount() {
        return chatBadgeCount;
    }

    public LiveData<String> getGeneralNotification() {
        return generalNotification;
    }
}
