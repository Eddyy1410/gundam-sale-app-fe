package com.huyntd.superapp.gundamshop_mobilefe.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.AuthResult;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.GoogleAuthProvider;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
import com.huyntd.superapp.gundamshop_mobilefe.MyUtils;
import com.huyntd.superapp.gundamshop_mobilefe.R;
import com.huyntd.superapp.gundamshop_mobilefe.SessionManager;
import com.huyntd.superapp.gundamshop_mobilefe.api.ApiService;
import com.huyntd.superapp.gundamshop_mobilefe.databinding.ActivityLoginOptionsBinding;
import com.huyntd.superapp.gundamshop_mobilefe.models.ApiResponse;
import com.huyntd.superapp.gundamshop_mobilefe.models.request.GoogleTokenRequest;
import com.huyntd.superapp.gundamshop_mobilefe.models.response.AuthenticationResponse;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginOptionsActivity extends AppCompatActivity {

    private ActivityLoginOptionsBinding binding;

    private static final String TAG = "LOGIN_OPTIONS_TAG";

    //ProgressDialog to show while google sign in
    private ProgressDialog progressDialog;

//    //Firebase Auth for auth related tasks
//    private FirebaseAuth firebaseAuth;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Init/Setup ProgressDialog to show while sign-in
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

//        firebaseAuth = FirebaseAuth.getInstance();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // default_web_client_id này phải đặt đúng với client-id của web user phía backend
                // setup trong res/values/strings.xml
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.loginEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoginEmailActivity();
            }
        });

        // Handle loginGoogleBtn click, begin google sign in
        binding.loginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginGoogleLogin();
            }
        });

    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void startLoginEmailActivity() {
        startActivity(new Intent(this, LoginEmailActivity.class));
    }

    private void beginGoogleLogin() {
        Log.d(TAG, "beginGoogleLogin: ");
        Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInARL.launch(googleSignInIntent);
    }

    private ActivityResultLauncher<Intent> googleSignInARL = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(TAG, "onActivityResult: "+result.getResultCode());
                    // handle google signIn result here
                    if (result.getResultCode() == LoginOptionsActivity.RESULT_OK) {

                        // get
                        Intent data = result.getData();

                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                        try {
                            // Google signIn was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d(TAG, "onActivityResult: AccountID: "+account.getId());

                            ApiService.apiService.loginGoogle(GoogleTokenRequest.builder()
                                    .idToken(account.getIdToken())
                                    .build()).enqueue(new Callback<ApiResponse<AuthenticationResponse>>() {
                                @Override
                                public void onResponse(Call<ApiResponse<AuthenticationResponse>> call, Response<ApiResponse<AuthenticationResponse>> response) {
                                    Log.i(TAG, "onResponse: "+response.body().toString());
                                    SessionManager.getInstance(LoginOptionsActivity.this).saveAuthToken(response.body().getResult().getToken());
                                    Toast.makeText(LoginOptionsActivity.this, response.toString(), Toast.LENGTH_SHORT).show();
                                    startMainActivity();
                                }

                                @Override
                                public void onFailure(Call<ApiResponse<AuthenticationResponse>> call, Throwable t) {
                                    Toast.makeText(LoginOptionsActivity.this, "error occured!", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "onFailure: ", t);
                                }
                            });

                        } catch (Exception e) {
                            Log.e(TAG, "onActivityResult: ", e);
                        }
                    } else {
                        // cancelled from Google signIn options/confirmation dialog
                        Log.d(TAG, "onActivityResult: Cancelled...!");
                        MyUtils.toast(LoginOptionsActivity.this, "Cancelled...!");
                    }
                }
            }
    );

//    private void firebaseAuthWithGoogleAccount(String idToken) {
//        Log.d(TAG, "firebaseAuthWithGoogleAccount: idToken: "+idToken);
//
//        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
//
//        firebaseAuth.signInWithCredential(credential)
//                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        // SignIn success, let's check if the user is new (New Account Register) or existing (Existing Login)
//                        if (authResult.getAdditionalUserInfo().isNewUser()) {
//                            Log.d(TAG, "onSuccess: Account Created...!");
//                            // New User, Account created. Let's save user info to Firebase realtime database
//                            updateUserInfoDB();
//                        } else {
//                            Log.d(TAG, "onSuccess: Logged In...!");
//                            // Existing User. No need to save user info to Firebase realtime database, Start MainActivity
//                            startActivity(new Intent(LoginOptionsActivity.this, MainActivity.class));
//                            finishAffinity();
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: ", e);
//                    }
//                });
//    }
//
//    private void updateUserInfoDB() {
//        Log.d(TAG, "updateUserInfoDB: ");
//
//        // set message and show progress dialog
//        progressDialog.setMessage("Saving user info...!");
//        progressDialog.show();
//
//        // get current timestamp e.g. to show user registration date/time
//        long timestamp = MyUtils.timestamp();
//        String registeredUserUid = firebaseAuth.getUid();
//        String registeredUserEmail = firebaseAuth.getCurrentUser().getEmail();
//        String name = firebaseAuth.getCurrentUser().getDisplayName();
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("uid", registeredUserUid);
//        hashMap.put("email", registeredUserEmail);
//        hashMap.put("name", name);
//        hashMap.put("timestamp", timestamp);
//        hashMap.put("phoneCode", "");
//        hashMap.put("phoneNumber", "");
//        hashMap.put("profileImageUrl", "");
//        hashMap.put("dob", "");
//        hashMap.put("userType", MyUtils.USER_TYPE_GOOGLE);
//        hashMap.put("token", "");
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
//        ref.child(registeredUserUid)
//                .setValue(hashMap)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        Log.d(TAG, "onSuccess: User info saved...!");
//                        progressDialog.dismiss();
//                        startActivity(new Intent(LoginOptionsActivity.this, MainActivity.class));
//                        finishAffinity();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(TAG, "onFailure: ", e);
//                        progressDialog.dismiss();
//                        MyUtils.toast(LoginOptionsActivity.this, "Failed to save due to "+e.getMessage());
//                    }
//                });
//    }

}