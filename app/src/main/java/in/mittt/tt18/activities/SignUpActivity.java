package in.mittt.tt18.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.concurrent.Executor;

import in.mittt.tt18.R;
import in.mittt.tt18.models.registration.LoginResponse;
import in.mittt.tt18.models.registration.SignupResponse;
import in.mittt.tt18.network.RegistrationClient;
import in.mittt.tt18.utilities.NetworkUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    EditText email, phone, regno, college, name;
    CheckBox outstation;
    String TAG = "SignUpActivity";
    String CAPTCHA_KEY = "6Lfho3IUAAAAAEu6JHZojPoo55KE885x5LJVIIfN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email = (EditText) findViewById(R.id.signup_email_et);
        phone = (EditText) findViewById(R.id.signup_phone_et);
        regno = (EditText) findViewById(R.id.signup_regno_et);
        college = (EditText) findViewById(R.id.signup_college_et);
        name = (EditText) findViewById(R.id.signup_name_et);
        Button b = (Button) findViewById(R.id.signup_button);
        b.setOnClickListener(this);

    }
    public void onClick(View view){
        final SignUpDetails details = new SignUpDetails();
        if (email.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || regno.getText().toString().isEmpty() || college.getText().toString().isEmpty() || name.getText().toString().isEmpty()){
            showAlert("Please fill in all the fields!");
            return;
        }else{
            details.setCollege(college.getText().toString().trim());
            details.setEmail(email.getText().toString().trim());
            details.setRegno(regno.getText().toString().trim());
            details.setName(name.getText().toString().trim());
            details.setPhone(phone.getText().toString().trim());
            details.setCollege(college.getText().toString().trim());
        }

        if (!NetworkUtils.isInternetConnected(this)){
            showAlert("Please connect to the internet and try again!");
            return;
        }
        SafetyNet.getClient(this).verifyWithRecaptcha(CAPTCHA_KEY)
                .addOnSuccessListener( this,
                        new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                                // Indicates communication with reCAPTCHA service was successful.
                                String userResponseToken=response.getTokenResult();
                                if (!userResponseToken.isEmpty()) {
                                    // Validate the user response token using the
                                    // reCAPTCHA siteverify API.
                                    performSignUp(details, userResponseToken);
                                }
                            }
                        })
                .addOnFailureListener( this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            int statusCode = apiException.getStatusCode();
                            Log.d(TAG, "GCError: StatusCode " + statusCode);
                        } else {
                            // A unknown type of error occurred.
                            Log.d(TAG, "GCError: " + e.getMessage());
                        }
                    }
                });
//
    }

    public void showAlert(String message){
        new AlertDialog.Builder(this).setTitle("Alert").setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setCancelable(true).show();

    }
    public void performSignUp(SignUpDetails details, String captchaToken){
        Call<SignupResponse> call = RegistrationClient.getRegistrationInterface(this).attemptRegistration(details.name, details.regno,details.email, details.phone, details.college, captchaToken, "0", "android");
        //TODO: Add a Loading Spinner here
        call.enqueue(new Callback<SignupResponse>() {
            @Override
            public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                showAlert("onResponse");
                String message = "";
                int error = 0;
                showAlert("response Body:"+response.body().toString());
                if (response != null && response.body() != null) {
                    switch(response.body().getStatus()){
                        case 1: message = "Login successful!";
                            break;
                        default: message = "Error!Please try again.";
                    }
                }else{
                    showAlert("Could not connect to server! Please check your internet connect or try again later.");
                }
            }

            @Override
            public void onFailure(Call<SignupResponse> call, Throwable t) {
                showAlert("Could not connect to server! Please check your internet connect or try again later.");
            }
        });
    }
}


class SignUpDetails{
    String email, phone, regno, college, name;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRegno() {
        return regno;
    }

    public void setRegno(String regno) {
        this.regno = regno;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}