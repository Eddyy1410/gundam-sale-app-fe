package com.huyntd.superapp.gundamshop_mobilefe.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.MessageResponse;

import java.util.List;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MessageRepository {

    ApiService apiService;
//    SessionManager sessionManager;
    String customerId;

    String TAG = "MESSAGE_REPO_TAG";

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

}
