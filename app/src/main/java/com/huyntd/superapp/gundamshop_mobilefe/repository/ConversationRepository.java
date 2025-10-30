package com.huyntd.superapp.gundamshop_mobilefe.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.ConversationResponse;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationRepository {

    final ApiService apiService;
    final SessionManager sessionManager;
    String TAG = "CONVERSATION_REPO_TAG";

    public LiveData<List<ConversationResponse>> getConversations() {
        final MutableLiveData<List<ConversationResponse>> data = new MutableLiveData<>();

        apiService.getConversations(Integer.parseInt(sessionManager.getUserId())).enqueue(new Callback<ApiResponse<List<ConversationResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ConversationResponse>>> call, Response<ApiResponse<List<ConversationResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body().getResult());
                } else {
                    data.postValue(null);
                    Log.e(TAG, "onResponse: "+response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ConversationResponse>>> call, Throwable t) {
                data.postValue(null);
                Log.e(TAG, "onFailure: ", t);
            }
        });

        return data;
    }

}
