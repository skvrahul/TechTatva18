package in.mittt.tt18.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import in.mittt.tt18.R;
import in.mittt.tt18.models.registration.LoginResponse;
import in.mittt.tt18.network.RegistrationClient;
import in.mittt.tt18.utilities.NetworkUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText email, password;
    View loadingSpiner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = findViewById(R.id.username_edit_text);
        password = findViewById(R.id.password_edit_text);
        loadingSpiner = findViewById(R.id.loading_spinner);
        LinearLayout loginLayout = (LinearLayout) findViewById(R.id.login_child_linear_layout);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (sp.getBoolean("loggedIn", false)) {
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(intent);
        } else {
            loginLayout.setVisibility(View.VISIBLE);
        }
    }

    public void guestContinue(View view) {
        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void loginClicked(View view) {
        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
            showAlert("Please enter email and password!");
            return;
        }
        if (!NetworkUtils.isInternetConnected(this)) {
            showAlert("Please connect to the internet and try again!");
            return;
        }

        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), "email=" + email.getText().toString() + "&password=" + password.getText().toString());
        Call<LoginResponse> call = RegistrationClient.getRegistrationInterface(LoginActivity.this).attemptLogin(body);
        //done: Add a Loading Spinner here
        loadingSpiner.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                loadingSpiner.setVisibility(View.GONE);
                String message = "";
                int error = 0;
                Log.d("LoginActivity", "onResponse: " + response.body());
                if (response != null && response.body() != null) {
                    switch (response.body().getStatus()) {
                        case 1:
                            message = "Login successful!";
                            error = 2;
                            login(response.body().getQr());
                            break;
                        default:
                            message = "Incorrect email or password! Please try again.";
                            error = 1;
                    }
                    if (error == 2) {

                    }
                    showAlert(message);
                } else {
                    showAlert("Could not connect to server! Please check your internet connect or try again later.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showAlert("Could not connect to server! Please check your internet connect or try again later.");
            }
        });
    }

    public void gotoSignup(View view) {
        Intent i = new Intent(this, SignUpActivity.class);
        startActivity(i);
    }

    public void login(String QR) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
        editor.putBoolean("loggedIn", true);
        editor.putString("QR", QR);
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
        startActivity(intent);
    }

    public void showAlert(String message) {
        new AlertDialog.Builder(LoginActivity.this).setTitle("Alert").setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setCancelable(true).show();
    }

}
