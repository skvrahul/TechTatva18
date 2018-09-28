package in.mittt.tt18.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.username_edit_text);
        password = (EditText) findViewById(R.id.password_edit_text);
    }
    public void loginClicked(View view){
        if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
            showAlert("Please enter email and password!");
            return;
        }
        if (!NetworkUtils.isInternetConnected(LoginActivity.this)){
            showAlert("Please connect to the internet and try again!");
            return;
        }

        RequestBody body =  RequestBody.create(MediaType.parse("text/plain"), "email="+email.getText().toString()+"&password="+password.getText().toString());
        Call<LoginResponse> call = RegistrationClient.getRegistrationInterface(LoginActivity.this).attemptLogin(body);
        //TODO: Add a Loading Spinner here
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                String message = "";
                int error = 0;
                if (response != null && response.body() != null) {
                    switch(response.body().getStatus()){
                        case 1: message = "Login successful!";
                            error = 2;
                            login();
                            break;
                        default: message = "Incorrect email or password! Please try again.";
                            error = 1;
                    }
                    if (error == 2){

                    }
                    showAlert(message);
                }else{
                    showAlert("Could not connect to server! Please check your internet connect or try again later.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showAlert("Could not connect to server! Please check your internet connect or try again later.");
            }
        });
    }
    public void login(){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit();
        editor.putBoolean("loggedIn", true);
        editor.apply();
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public void showAlert(String message){
        new AlertDialog.Builder(LoginActivity.this).setTitle("Alert").setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setCancelable(true).show();
    }

}
