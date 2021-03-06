package in.savitar.smjewelsuser.mvp.ui.Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import in.aabhasjindal.otptextview.OtpTextView;
import in.savitar.smjewelsuser.R;
import in.savitar.smjewelsuser.databinding.FragmentLoginBinding;
import in.savitar.smjewelsuser.mvp.ui.splash.SplashContract;
import in.savitar.smjewelsuser.mvp.ui.splash.SplashPresenter;
import in.savitar.smjewelsuser.mvp.utils.NavigationUtil;


public class LoginFragment extends Fragment implements SplashContract.View {

    SplashPresenter mPresenter;
    FragmentLoginBinding mBinding;

    //Bottom Sheet References
    OtpTextView otpTextView;
    Button verifyOtp;
    TextView otpTextMessage;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;
    private String mVerificationId;
    FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private String userUniqueID;
    private String planName;
    private String setName;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        mPresenter = new SplashPresenter(this);
        init();
        return mBinding.getRoot();
    }

    private void init() {
        //Initialize Ad Banner
        AdRequest adRequest = new AdRequest.Builder().build();
        mBinding.loginAdBanner.loadAd(adRequest);


        mAuth = FirebaseAuth.getInstance();
        //Bottom Sheet
        sheetBehavior = BottomSheetBehavior.from(mBinding.bottomSheet.bottomSheetOtp);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBinding.getOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(mBinding.userIdEt.getText())) {
                    mBinding.userIdEt.setError("Invalid ID");
                } else {
                    getPhoneNumber(mBinding.userIdEt.getText().toString());
                }
            }
        });

        //Verify OTP Button
        mBinding.bottomSheet.verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(TextUtils.isEmpty(mBinding.bottomSheet.otpView.getText()))
                {
                  mBinding.bottomSheet.otpView.setError("Enter OTP");
                }
                else if (mBinding.bottomSheet.otpView.getText().toString().length() < 5) {
                    Toast.makeText(getContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                } else {
                    verifyVerificationCode(mBinding.bottomSheet.otpView.getText().toString());
                }
            }
        });

        displayImageSlider();

        //Create Account
      /*  mBinding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationUtil.INSTANCE.toCreateAccount();
            }
        });*/


    }

    //To Load and display images into image slider
    private void displayImageSlider() {
        List<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel("https://smjewels.in/wp-content/uploads/2019/04/web-banner01.jpg"));
        slideModels.add(new SlideModel("https://smjewels.in/wp-content/uploads/2019/04/web-banner02.jpg"));
        slideModels.add(new SlideModel("https://smjewels.in/wp-content/uploads/2019/04/web-banner03.jpg"));
        slideModels.add(new SlideModel("https://smjewels.in/wp-content/uploads/2019/04/g-5.jpg"));
        slideModels.add(new SlideModel("https://smjewels.in/wp-content/uploads/2019/04/g-3.jpg"));
        mBinding.loginSlider.setImageList(slideModels, true);
    }
    public void progress_visiblity()
    {
        mBinding.progressBar.setVisibility(View.VISIBLE);
        mBinding.getOtp.setVisibility(View.GONE);
        mBinding.noAccount.setVisibility(View.GONE);
        // mBinding.createAccount.setVisibility(View.GONE);
    }
    public void progress_hidden()
    {
        mBinding.progressBar.setVisibility(View.GONE);
        mBinding.getOtp.setVisibility(View.VISIBLE);
        mBinding.noAccount.setVisibility(View.VISIBLE);
        // mBinding.createAccount.setVisibility(View.VISIBLE);
    }
    public void getPhoneNumber(final String userID) {
        progress_visiblity();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("UsersList");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(userID)) {
                    String phoneNumber = snapshot.child(userID).child("Phone").getValue(String.class);
                    //Toast.makeText(context,"Phone Number=>"+phoneNumber,Toast.LENGTH_LONG).show();
                    planName = snapshot.child(userID).child("PlanName").getValue(String.class);
                    userUniqueID = snapshot.child(userID).child("UserID").getValue(String.class);

                    if (planName.compareToIgnoreCase("PlanA") == 0){
                        setName = snapshot.child(userID).child("SetName").getValue(String.class);
                    }
                    sendOtp(phoneNumber); // Sending OTP to the number
                } else {
                    Toast.makeText(getContext(), "User Does not exist. Please Register", Toast.LENGTH_LONG).show();
                    progress_hidden();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sendOtp(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,   // Activity (for callback binding)
                mCallbacks);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //progress.setVisibility(View.GONE);
            progress_hidden();
            Toast.makeText(getContext(), "verification completed", Toast.LENGTH_SHORT).show();
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                mBinding.bottomSheet.otpView.setText(code);
                //progress.setVisibility(View.GONE);
                //verifyOtp.setEnabled(true);
                //verifyButton.setBackgroundColor(getColor(R.color.primaryTrans));
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Log.v("OTPS", String.valueOf(e));
            //progress.setVisibility(View.GONE);
            progress_hidden();
            Toast.makeText(getContext(), "verification failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            progress_visiblity();
            super.onCodeSent(s, forceResendingToken);
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mVerificationId = s;
            progress_hidden();
            Toast.makeText(getContext(), "OTP sent", Toast.LENGTH_LONG).show();
        }
    };

    private void verifyVerificationCode(String code) {
        //creating the credential
        mBinding.bottomSheet.progressBarOtp.setVisibility(View.VISIBLE);
        mBinding.bottomSheet.verifyOtp.setVisibility(View.GONE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        //signing the user
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try {
                    if (task.isSuccessful()) {
                        //Code to set user session
                        saveUserID();
                        //Saves user ID and plan Name in shared preferences
                            NavigationUtil.INSTANCE.toMainActivity();

                    }
                    else
                        {
                        String message = "Somthing is wrong, we will fix it soon...";

                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                        {
                            message = "Incorrect OTP";
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();

                            mBinding.bottomSheet.progressBarOtp.setVisibility(View.GONE);
                            mBinding.bottomSheet.verifyOtp.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    Log.v("TAG", "OTP Exception" + e);
                    Toast.makeText(getContext(), "Incorrect OTP", Toast.LENGTH_LONG).show();

                }
            }
        });

    }

    private void saveUserID() { // Save user ID and Plan Name in shared Preferences for future use
        SharedPreferences preferences = getActivity().getSharedPreferences("MyPrefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("UserKey",userUniqueID);
        editor.putString("UserID",mBinding.userIdEt.getText().toString());
        editor.putString("Plan",planName);

        if (planName.compareToIgnoreCase("PlanA") == 0){
            editor.putString("SetName",setName);
        }

        editor.commit();
    }

    @Override
    public void onSuccess(String message) {

    }

    @Override
    public void onFailure(String message) {

    }
}