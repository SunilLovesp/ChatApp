package com.example.chat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.databinding.ActivityPhoneNumberBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    private FirebaseAuth auth;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    private String mVerificationId;
    private static final String TAG = "MAIN_TAG";

    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cardView2.setVisibility(View.VISIBLE);
        binding.cardView3.setVisibility(View.GONE);


        auth = FirebaseAuth.getInstance();



        pd= new  ProgressDialog(this);
        pd.setTitle("Please Wait ....");
        pd.setCanceledOnTouchOutside(false);


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneCredential(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                pd.dismiss();
                Toast.makeText(PhoneNumberActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String VerificationId, @NonNull PhoneAuthProvider.ForceResendingToken  token) {
                super.onCodeSent(VerificationId, forceResendingToken);
                Log.d(TAG, "onCodeSent: " +VerificationId);

                mVerificationId = VerificationId;
                forceResendingToken  = token;
                pd.dismiss();


                binding.cardView2.setVisibility(View.GONE);
                binding.cardView3.setVisibility(View.VISIBLE);

                Toast.makeText(PhoneNumberActivity.this, "Verification Code sent", Toast.LENGTH_SHORT).show();





            }
        };

        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone =binding.phoneBox.getText().toString().trim();
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(PhoneNumberActivity.this, "Please Enter Phone No with CountryCode", Toast.LENGTH_SHORT).show();
                }else {
                    startPhoneNumberVerification(phone);
                }
            }
        });


        binding.resendCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone =binding.phoneBox.getText().toString().trim();
                if (TextUtils.isEmpty(phone)){
                    Toast.makeText(PhoneNumberActivity.this, "Please Enter Phone No", Toast.LENGTH_SHORT).show();
                }else {
                    resendVerificationCode(phone ,forceResendingToken);
                }

            }
        });




        binding.codesubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code =binding.phoneBox.getText().toString().trim();
                if (TextUtils.isEmpty(code)){
                    Toast.makeText(PhoneNumberActivity.this, "Please Enter verification Code", Toast.LENGTH_SHORT).show();
                }else {
                    verifyPhoneNumberWithCode(mVerificationId,code);
                }

            }
        });

    }



    private void startPhoneNumberVerification(String phone) {
        pd.setMessage("Verify Phone Number ");
        pd.show();


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();


        PhoneAuthProvider.verifyPhoneNumber(options);


    }
    private void resendVerificationCode(String phone , PhoneAuthProvider.ForceResendingToken token){

        pd.setMessage("Resending code ...");
        pd.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phone)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(token)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }





    private void verifyPhoneNumberWithCode(String VerificationId, String code) {

        pd.setMessage("Verifying code ...");
        pd.show();

        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(VerificationId ,code);
        signInWithPhoneCredential(credential);




    }

    private void signInWithPhoneCredential (PhoneAuthCredential credential) {

        pd.setMessage("Logging In");
        auth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pd.dismiss();
                        String phone =auth.getCurrentUser().getPhoneNumber();
                        Toast.makeText(PhoneNumberActivity.this, "Logged In as "+ phone, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PhoneNumberActivity.this,SetupProfileActivity.class));

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(PhoneNumberActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


}

























//
//        auth = FirebaseAuth.getInstance();
//
//        if(auth.getCurrentUser() != null) {
//            Intent intent = new Intent(PhoneNumberActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
//        getSupportActionBar().hide();
//
//        binding.phoneBox.requestFocus();
//
//        binding.continueBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(PhoneNumberActivity.this, OTPActivity.class);
//                intent.putExtra("phoneNumber", binding.phoneBox.getText().toString());
//                startActivity(intent);
//            }
//        });
//
//    }
//}