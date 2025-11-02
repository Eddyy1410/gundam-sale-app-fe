package com.huyntd.superapp.gundamshop_mobilefe.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiClient;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.SendMessageRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.MessageResponse;
import com.huyntd.superapp.gundamshop_mobilefe.utils.AppStompClient;

import java.util.List;

import io.reactivex.disposables.Disposable;
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
public class MessageRepository {

    ApiService apiService = ApiClient.getApiService();
    Disposable chatTopicDisp;

    final AppStompClient stompClient;
    final String customerId;

    String TAG = "MESSAGE_REPO_TAG";

    MutableLiveData<MessageResponse> newIncomingMessage = new MutableLiveData<>();

    public LiveData<List<MessageResponse>> getMessages() {
        final MutableLiveData<List<MessageResponse>> data = new MutableLiveData<>();

        apiService.getMessages(Integer.parseInt(customerId)).enqueue(new Callback<ApiResponse<List<MessageResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<MessageResponse>>> call, Response<ApiResponse<List<MessageResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body().getResult());
                } else {
                    data.postValue(null);
                    Log.e(TAG, "onResponse: "+response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<MessageResponse>>> call, Throwable t) {
                data.postValue(null);
                Log.e(TAG, "onFailure: ", t);
            }
        });

        return data;
    }

    public void sendMessage(int conversationId, String content) {
        try {
            Gson gson = new Gson();
            SendMessageRequest request = SendMessageRequest.builder()
                    .conversationId(conversationId)
                    .content(content)
                    .build();
            stompClient.sendMessage(getCustomerId(), gson.toJson(request));

        } catch (Exception e) {
            Log.e(TAG, "onFailure: "+e);
        }
    }

    public void startListeningForChat() {
        if (!stompClient.isConnected()) {
            Log.e(TAG, "STOMP Client not connected. Cannot subscribe.");
            return;
        }

        // Hủy subscription cũ nếu có
        if (chatTopicDisp != null && !chatTopicDisp.isDisposed()) {
            chatTopicDisp.dispose();
        }

        chatTopicDisp = stompClient.subscribeDynamicTopic(customerId, stompMessage -> {
            String payload = stompMessage.getPayload();
            try {
                Gson gson = new Gson();
                // Chuyển đổi JSON Payload thành MessageResponse
                MessageResponse newMessage = gson.fromJson(payload, MessageResponse.class);

                // Đẩy tin nhắn mới vào LiveData real-time
                newIncomingMessage.postValue(newMessage);

            } catch (Exception e) {
                Log.e(TAG, "Lỗi phân tích JSON STOMP: " + payload, e);
            }
        });
    }

    public void stopListeningForChat() {
        if (chatTopicDisp != null && !chatTopicDisp.isDisposed()) {
            chatTopicDisp.dispose();
            Log.i(TAG, "Chat topic subscription disposed.");
        }
    }

    // Phương thức cung cấp LiveData cho tin nhắn mới
    public LiveData<MessageResponse> getNewIncomingMessage() {
        return newIncomingMessage;
    }

}
