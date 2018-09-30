package in.mittt.tt18.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.zxing.Result;

import in.mittt.tt18.R;
import in.mittt.tt18.models.registration.EventRegistrationResponse;
import in.mittt.tt18.network.RegistrationClient;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTeamMemberActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private static final int REQUEST_CAMERA = 316;
    private ZXingScannerView scannerView;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_team_member);
        setTitle(R.string.add_team_member);

        Intent intent=getIntent();
        eventID=intent.getStringExtra("eventID");

        if (getSupportActionBar() != null) getSupportActionBar().setSubtitle(R.string.scan_qr_code_2);

        scannerView = (ZXingScannerView)findViewById(R.id.event_scanner);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
        }

        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scannerView.setResultHandler(this);
    }

    @Override
    public void handleResult(Result result) {
        if (result.getText().isEmpty()) return;

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Adding member... please wait!");
        dialog.setCancelable(false);
        dialog.show();

        String cookie = RegistrationClient.generateCookie(this);
        Call<EventRegistrationResponse> call = RegistrationClient.getRegistrationInterface(this).addToTeam(cookie,eventID,result.getText());
        call.enqueue(new Callback<EventRegistrationResponse>() {
            @Override
            public void onResponse(Call<EventRegistrationResponse> call, Response<EventRegistrationResponse> response) {
                dialog.dismiss();
                if (response != null && response.body() != null){
                    showAlert(response.body());
                }else{
                    noConnectionAlert();
                }
            }

            @Override
            public void onFailure(Call<EventRegistrationResponse> call, Throwable t) {
                dialog.dismiss();
                noConnectionAlert();
            }
        });
    }

    private void showAlert(final EventRegistrationResponse response){
        final int status = response.getStatus();
        Log.d("Status", response.getStatus()+"");
        Log.d("Message", response.getMessage());

        new AlertDialog.Builder(this).setTitle(status<3? "Error":"Success").setMessage(response.getMessage())
                .setIcon(status<4 ? R.drawable.ic_error:R.drawable.ic_success)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (status == -1){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AddTeamMemberActivity.this).edit();
                            editor.remove("loggedIn");
                            editor.remove("session_cookie");
                            editor.remove("cloudflare_cookie");
                            editor.apply();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else if (status == 1){
                            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else if (status == 2){
                            scannerView.stopCamera();
                            scannerView.startCamera();
                            scannerView.setResultHandler(AddTeamMemberActivity.this);
                        }
                        else if(status==3){
                            Intent intent = new Intent();
                            intent.putExtra("success", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                }).setCancelable(false).show();
    }

    public void noConnectionAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setIcon(R.drawable.ic_error)
                .setMessage("Could not connect to server! Please check your internet connect or try again later.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_reg, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                finish();
                break;
            }
            case R.id.event_reg_flash:
                scannerView.toggleFlash();
                break;
            case R.id.event_reg_reload:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                }
                scannerView.stopCamera();
                scannerView.startCamera();
                scannerView.setResultHandler(this);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
