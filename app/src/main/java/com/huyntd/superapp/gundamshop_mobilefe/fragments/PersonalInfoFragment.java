package com.huyntd.superapp.gundamshop_mobilefe.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.util.TypedValue;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiClient;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.UserProfileUpdateRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoFragment extends Fragment {

    TextView tvName, tvMemberTag, tvEmail;
    EditText etFullName, etPhone;
    ImageView ivAvatar;

    ApiService apiService;
    private final Gson gson = new Gson();
    MaterialButton btnUpdate;

    // Keep original values so we can detect changes
    private String originalFullName = "";
    private String originalPhone = "";

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_info, container, false);

        ImageView ivBack = view.findViewById(R.id.iv_back_arrow);

        ivAvatar = view.findViewById(R.id.iv_profile_avatar);
        tvName = view.findViewById(R.id.tv_profile_name);
        tvMemberTag = view.findViewById(R.id.tv_profile_member_tag);
        etFullName = view.findViewById(R.id.et_full_name);
        etPhone = view.findViewById(R.id.et_phone);
        tvEmail = view.findViewById(R.id.et_email);
        btnUpdate = view.findViewById(R.id.btn_update);

        apiService = ApiClient.getApiService();

        // Provide local ctx and activity references so we avoid Fragment helper methods
        final Context ctx = view.getContext();
        final FragmentActivity activity = (FragmentActivity) ctx;

        // Back arrow navigates back to ProfileFragment (original profile UI)
        ivBack.setOnClickListener(v -> {
            // Replace this fragment with the original ProfileFragment using the main activity container id
            FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.replace(com.huyntd.superapp.gundamshop_mobilefe.R.id.fragmentsFL, new ProfileFragment());
            ft.commit();
        });

        // Load user info from API (so we don't rely on fragment lifecycle helpers in static analysis)
        apiService.getInfo().enqueue(new Callback<com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse<com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse>>() {
            @Override
            public void onResponse(@NonNull Call<com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse<com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse>> call, @NonNull Response<com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse<com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess() && response.body().getResult() != null) {
                    com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse userResponse = response.body().getResult();
                    // Avatar: app default to avoid external dependency
                    ivAvatar.setImageResource(R.drawable.ic_launcher_foreground);

                    String fullName = userResponse.getFullName();
                    tvName.setText((fullName == null || fullName.isEmpty()) ? ctx.getString(R.string.app_name) : fullName);

                    String role = SessionManager.getInstance(ctx).getRole();
                    if (role == null || role.trim().isEmpty() || role.toUpperCase().contains("NULL")) {
                        tvMemberTag.setText("");
                    } else {
                        tvMemberTag.setText(role);
                    }

                    String nameToSet = fullName == null ? "" : fullName;
                    String phoneToSet = userResponse.getPhone() == null ? "" : userResponse.getPhone();
                    etFullName.setText(nameToSet);
                    etPhone.setText(phoneToSet);
                    tvEmail.setText(userResponse.getEmail() == null ? "" : userResponse.getEmail());

                    originalFullName = nameToSet.trim();
                    originalPhone = phoneToSet.trim();
                    setupChangeListeners();
                    applyUpdateButtonState(false, ctx);
                } else {
                    // fallback to defaults
                    tvName.setText(ctx.getString(R.string.app_name));
                    tvMemberTag.setText("");
                    ivAvatar.setImageResource(R.drawable.ic_launcher_foreground);
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse<com.huyntd.superapp.gundamshop_mobilefe.models.response.UserResponse>> call, @NonNull Throwable t) {
                tvName.setText(ctx.getString(R.string.app_name));
                tvMemberTag.setText("");
                ivAvatar.setImageResource(R.drawable.ic_launcher_foreground);
            }
        });

        // Wire up update button
        btnUpdate.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            // Basic validation
            if (fullName.isEmpty()) {
                Toast.makeText(ctx, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
                return;
            }

            // Disable button while processing and show processing label
            applyUpdateButtonState(false, ctx);
            btnUpdate.setText(ctx.getString(R.string.processing));

            // Only include changed fields; Gson default will omit null fields
            String fullNameToSend = fullName.equals(originalFullName) ? null : fullName;
            String phoneToSend = phone.equals(originalPhone) ? null : phone;

            // If nothing changed, bail out (shouldn't happen since button is disabled when no changes)
            if (fullNameToSend == null && phoneToSend == null) {
                Toast.makeText(ctx, "Không có thay đổi", Toast.LENGTH_SHORT).show();
                applyUpdateButtonState(false, ctx);
                btnUpdate.setText(ctx.getString(R.string.update));
                return;
            }

            UserProfileUpdateRequest req = UserProfileUpdateRequest.builder()
                    .fullName(fullNameToSend)
                    .phone(phoneToSend)
                    .build();

            // Show confirmation dialog before performing update
            new androidx.appcompat.app.AlertDialog.Builder(ctx)
                    .setTitle(ctx.getString(R.string.confirm_update_title))
                    .setMessage(ctx.getString(R.string.confirm_update_message))
                    .setNegativeButton(ctx.getString(R.string.cancel), (dialog, which) -> {
                        // Restore button state based on whether fields have pending changes
                        boolean changedNow = !etFullName.getText().toString().trim().equals(originalFullName) || !etPhone.getText().toString().trim().equals(originalPhone);
                        applyUpdateButtonState(changedNow, ctx);
                        btnUpdate.setText(ctx.getString(R.string.update));
                        dialog.dismiss();
                    })
                    .setPositiveButton(ctx.getString(R.string.confirm), (dialog, which) -> {
                        performUpdate(req, ctx);
                    })
                    .setCancelable(true)
                    .show();

        });

        // Wire up delete account
        view.findViewById(R.id.tv_delete_account).setOnClickListener(v -> {
            // Inflate bottom sheet
            View bs = inflater.inflate(R.layout.bottom_sheet_delete_account, null);
            com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(ctx);
            dialog.setContentView(bs);

            EditText input = bs.findViewById(R.id.bs_input_phone);
            com.google.android.material.button.MaterialButton btnDelete = bs.findViewById(R.id.bs_btn_delete);
            TextView tvCancel = bs.findViewById(R.id.bs_cancel);

            // Pre-fill hint with user's phone if available
            String userPhone = originalPhone == null ? "" : originalPhone;
            final String normalizedUserPhone = normalizePhone(userPhone);
            if (!userPhone.isEmpty()) {
                // Show the phone in description and highlight using theme color
                TextView desc = bs.findViewById(R.id.bs_description);
                if (desc != null) {
                    String prefix = ctx.getString(R.string.delete_account_message) + "\n\n";
                    String phoneLabel = ctx.getString(R.string.delete_phone_label, userPhone);
                    String full = prefix + phoneLabel;
                    SpannableStringBuilder ssb = new SpannableStringBuilder(full);
                    // Find phone substring and color it with theme error color + bold
                    int phoneStart = full.indexOf(userPhone);
                    if (phoneStart >= 0) {
                        int phoneEnd = phoneStart + userPhone.length();
                        int errColor = getThemeErrorColor(bs, Color.parseColor("#E91E63"));
                        ssb.setSpan(new ForegroundColorSpan(errColor), phoneStart, phoneEnd, 0);
                        ssb.setSpan(new StyleSpan(Typeface.BOLD), phoneStart, phoneEnd, 0);
                    }
                    desc.setText(ssb);
                }
            }

            // Enable delete button only when input equals user's phone (normalized)
            input.addTextChangedListener(new android.text.TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(android.text.Editable s) {
                    String val = s.toString().trim();
                    String normalizedVal = normalizePhone(val);
                    boolean enabled = normalizedVal.equals(normalizedUserPhone);
                    btnDelete.setEnabled(enabled);
                    if (enabled) {
                        int errColor = getThemeErrorColor(btnDelete, Color.parseColor("#E91E63"));
                        btnDelete.setBackgroundTintList(android.content.res.ColorStateList.valueOf(errColor));
                    } else {
                        btnDelete.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#CFD8DC")));
                    }
                }
            });

            btnDelete.setOnClickListener(bb -> {
                // Disable button and show processing
                btnDelete.setEnabled(false);
                btnDelete.setText(ctx.getString(R.string.processing));

                com.huyntd.superapp.gundamshop_mobilefe.models.request.DeleteAccountRequest delReq =
                        com.huyntd.superapp.gundamshop_mobilefe.models.request.DeleteAccountRequest.builder()
                                .phone(userPhone)
                                .build();

                ApiService api = ApiClient.getApiService();
                api.deleteMyAccount(delReq).enqueue(new Callback<com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse<Void>>() {
                    @Override
                    public void onResponse(@NonNull Call<com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse<Void>> call, @NonNull Response<com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse<Void>> response) {
                        btnDelete.setEnabled(true);
                        btnDelete.setText(ctx.getString(R.string.delete_button));

                        if (response.isSuccessful() && response.body() != null) {
                            // success
                            SessionManager.getInstance(ctx).clearSession();
                            dialog.dismiss();
                            ctx.startActivity(new android.content.Intent(ctx, com.huyntd.superapp.gundamshop_mobilefe.activities.MainActivity.class));
                            activity.finish();
                        } else {
                            try (okhttp3.ResponseBody eb = response.errorBody()) {
                                if (eb != null) {
                                    String err = eb.string();
                                    com.google.gson.Gson g = new com.google.gson.Gson();
                                    com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse<?> errResp = g.fromJson(err, com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse.class);
                                    String msg = errResp != null && errResp.getMessage() != null ? errResp.getMessage() : ctx.getString(R.string.delete_account_message);
                                    Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
                                    return;
                                }
                            } catch (Exception ex) {
                                // ignore
                            }

                            Toast.makeText(ctx, ctx.getString(R.string.delete_account_message) + ": HTTP " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse<Void>> call, @NonNull Throwable t) {
                        btnDelete.setEnabled(true);
                        btnDelete.setText(ctx.getString(R.string.delete_button));
                        Toast.makeText(ctx, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });

            tvCancel.setOnClickListener(cc -> dialog.dismiss());

            dialog.show();
        });

        return view;
    }

    // Extracted helper to perform the API call — called after confirmation
    private void performUpdate(UserProfileUpdateRequest req, Context ctx) {
        // Already set button disabled and text before showing dialog; keep it
        apiService.updateMyProfile(req).enqueue(new Callback<ApiResponse<UserResponse>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<UserResponse>> call, @NonNull Response<ApiResponse<UserResponse>> response) {
                btnUpdate.setEnabled(true);
                btnUpdate.setText(ctx.getString(R.string.update));

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponse> body = response.body();
                    if (body.isSuccess() && body.getResult() != null) {
                        UserResponse updated = body.getResult();
                        // Update UI with new values
                        tvName.setText(updated.getFullName() == null ? ctx.getString(R.string.app_name) : updated.getFullName());
                        etFullName.setText(updated.getFullName() == null ? "" : updated.getFullName());
                        etPhone.setText(updated.getPhone() == null ? "" : updated.getPhone());

                        // update originals and disable button until further changes
                        originalFullName = updated.getFullName() == null ? "" : updated.getFullName();
                        originalPhone = updated.getPhone() == null ? "" : updated.getPhone();
                        applyUpdateButtonState(false, ctx);

                        Toast.makeText(ctx, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();

                    } else {
                        String msg = mapProfileErrorCodeToMessage(ctx, body.getCode(), body.getMessage());
                        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Try parse structured error
                    try (okhttp3.ResponseBody eb = response.errorBody()) {
                        if (eb != null) {
                            String err = eb.string();
                            ApiResponse<?> errResp = gson.fromJson(err, ApiResponse.class);
                            String friendly = errResp != null ? mapProfileErrorCodeToMessage(ctx, errResp.getCode(), errResp.getMessage()) : ctx.getString(R.string.update_failed);
                            Toast.makeText(ctx, friendly, Toast.LENGTH_LONG).show();
                            return;
                        }
                    } catch (Exception ex) {
                        // ignore
                    }

                    Toast.makeText(ctx, ctx.getString(R.string.update_failed) + ": HTTP " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<UserResponse>> call, @NonNull Throwable t) {
                btnUpdate.setEnabled(true);
                btnUpdate.setText(ctx.getString(R.string.update));
                Toast.makeText(ctx, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    // Map backend error codes to friendly Vietnamese messages for profile update
    private String mapProfileErrorCodeToMessage(Context ctx, Integer code, String serverMessage) {
        if (code == null) return (serverMessage == null || serverMessage.isEmpty()) ? ctx.getString(R.string.update_failed) : serverMessage;
        return switch (code) {
            case 1016 -> "Tên mới phải khác tên hiện tại.";
            case 1017 -> "Số điện thoại mới phải khác số hiện tại.";
            default -> {
                if (serverMessage != null && !serverMessage.isEmpty()) yield serverMessage;
                yield ctx.getString(R.string.update_failed);
            }
        };
    }

    // Add text watchers to detect changes and enable the update button
    private void setupChangeListeners() {
        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                checkIfChanged();
            }
        };

        etFullName.addTextChangedListener(watcher);
        etPhone.addTextChangedListener(watcher);
    }

    private void checkIfChanged() {
        String currentName = etFullName.getText().toString().trim();
        String currentPhone = etPhone.getText().toString().trim();
        boolean changed = !currentName.equals(originalFullName) || !currentPhone.equals(originalPhone);
        // Visually update the update button based on whether there are changes
        applyUpdateButtonState(changed, btnUpdate.getContext());
    }

    // Apply enabled/disabled visual state to the update button
    private void applyUpdateButtonState(boolean enabled, Context ctx) {
        btnUpdate.setEnabled(enabled);
        if (enabled) {
            int primary = getAttrColor(ctx, androidx.appcompat.R.attr.colorPrimary, Color.parseColor("#E91E63"));
            btnUpdate.setBackgroundTintList(ColorStateList.valueOf(primary));
            btnUpdate.setTextColor(ContextCompat.getColor(ctx, android.R.color.white));
        } else {
            btnUpdate.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CFD8DC")));
            btnUpdate.setTextColor(ContextCompat.getColor(ctx, android.R.color.white));
        }
    }

    // Helper: resolve an attribute color (e.g., R.attr.colorPrimary) with fallback
    private int getAttrColor(Context ctx, int attrResId, int fallback) {
        TypedValue tv = new TypedValue();
        boolean ok = ctx.getTheme().resolveAttribute(attrResId, tv, true);
        if (!ok) return fallback;
        if (tv.resourceId != 0) {
            try { return ContextCompat.getColor(ctx, tv.resourceId); } catch (Exception ignored) {}
        }
        if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) return tv.data;
        return fallback;
    }

    // Normalize phone numbers: remove non-digit chars, handle +84/84/0084 -> leading 0
    private String normalizePhone(String raw) {
        if (raw == null) return "";
        String digits = raw.replaceAll("\\D+", "");
        if (digits.startsWith("0084")) {
            digits = digits.substring(4);
        }
        if (digits.startsWith("84")) {
            // replace leading country code 84 with 0
            digits = "0" + digits.substring(2);
        }
        // If it already starts with 0, keep it
        // For other lengths, just return digits
        return digits;
    }

    // Resolve the theme error color (?attr/colorError). Falls back to fallbackColor if not found.
    private int getThemeErrorColor(View v, int fallbackColor) {
        Context ctx = v.getContext();
        // Try to resolve attribute name 'colorError' from app or material package
        int attrId = ctx.getResources().getIdentifier("colorError", "attr", ctx.getPackageName());
        if (attrId == 0) {
            attrId = ctx.getResources().getIdentifier("colorError", "attr", "com.google.android.material");
        }
        if (attrId == 0) return fallbackColor;

        TypedValue tv = new TypedValue();
        boolean ok = ctx.getTheme().resolveAttribute(attrId, tv, true);
        if (!ok) return fallbackColor;

        if (tv.resourceId != 0) {
            try {
                return ContextCompat.getColor(ctx, tv.resourceId);
            } catch (Exception ex) {
                // fallback
            }
        }
        if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return tv.data;
        }
        return fallbackColor;
    }
}
