package in.mittt.tt18.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import in.mittt.tt18.R;


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

        String eventName = intent.getStringExtra("eventName");
        String tID = "";
        tID = intent.getStringExtra("teamID");


        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setSubtitle(eventName);
        }

        userCard = (CardView)findViewById(R.id.user_card);
        addTeamMate = (Button)findViewById(R.id.add_team_mate_button);
        teamID = (TextView)findViewById(R.id.team_id);
        maxTeamSize = (TextView)findViewById(R.id.max_team_size_text_view);
        leaveTeam=findViewById(R.id.leave_team_button);

        maxTeamSize.setText(maxSize+"");
        teamID.setText(tID);

        userCard.setVisibility(View.VISIBLE);
        addTeamMate.setVisibility(View.VISIBLE);
        leaveTeam.setVisibility(View.VISIBLE);
        //TODO: Leave Team to be implemented
        addTeamMate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterTeamActivity.this, AddTeamMemberActivity.class);
                intent.putExtra("eventID",evid);
                startActivityForResult(intent, ADD_TEAM_MEMBER);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == ADD_TEAM_MEMBER && data != null){
            if (data.getBooleanExtra("success", false)){

            }
        }
    }
}