package in.mittt.tt18.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import in.mittt.tt18.R;
import in.mittt.tt18.models.registration.SignupResponse;
import in.mittt.tt18.network.RegistrationClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterTeamActivity extends AppCompatActivity {
    private static final int ADD_TEAM_MEMBER = 0;
    private CardView userCard;
    private Button addTeamMate;
    private Button leaveTeam;
    private TextView teamID;
    private TextView maxTeamSize;
    private int maxSize;
    private String evid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_team);
        setTitle(R.string.register_team);

        Intent intent = getIntent();

        evid = intent.getStringExtra("eventID");
        maxSize = Integer.parseInt(intent.getStringExtra("maxTeamSize"));

        final String eventName = intent.getStringExtra("eventName");
        String tID = "";
        tID = intent.getStringExtra("teamID");


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(eventName);
        }

        userCard = (CardView) findViewById(R.id.user_card);
        addTeamMate = (Button) findViewById(R.id.add_team_mate_button);
        teamID = (TextView) findViewById(R.id.team_id);
        maxTeamSize = (TextView) findViewById(R.id.max_team_size_text_view);
        leaveTeam = findViewById(R.id.leave_team_button);

        maxTeamSize.setText(maxSize + "");
        teamID.setText(tID);

        userCard.setVisibility(View.VISIBLE);
        addTeamMate.setVisibility(View.VISIBLE);
        leaveTeam.setVisibility(View.VISIBLE);

        addTeamMate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterTeamActivity.this, AddTeamMemberActivity.class);
                intent.putExtra("eventID", evid);
                startActivityForResult(intent, ADD_TEAM_MEMBER);
            }
        });
        leaveTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send request to delete the event here
                new AlertDialog.Builder(RegisterTeamActivity.this).setTitle("Alert").setIcon(R.drawable.ic_error).setMessage("Do you want to leave team for " + eventName + " ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Call<SignupResponse> call = RegistrationClient.getRegistrationInterface(RegisterTeamActivity.this).leaveTeam(RegistrationClient.generateCookie(RegisterTeamActivity.this), evid);
                                call.enqueue(new Callback<SignupResponse>() {
                                    @Override
                                    public void onResponse(Call<SignupResponse> call, Response<SignupResponse> response) {
                                        String message = "";
                                        int error = 0;
                                        if (response != null && response.body() != null) {
                                            showAlert(response.body().getMessage());
                                            if (response.body().getStatus() == 3) {
                                                //Succesfully removed from backend. Remove from frontend and refresh adapter
                                                finish();
                                            }
                                        } else {
                                            showAlert("Null Response");
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<SignupResponse> call, Throwable t) {
                                        showAlert("Error.Failed to delete the event. Please try again!");
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setCancelable(true).show();

            }
        });
    }

    public void showAlert(String message) {
        new AlertDialog.Builder(this).setTitle("Alert").setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).setCancelable(true).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ADD_TEAM_MEMBER && data != null) {
            if (data.getBooleanExtra("success", false)) {

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}