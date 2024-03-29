package in.mittt.tt18.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.mittt.tt18.R;
import in.mittt.tt18.models.results.ResultModel;

class QualifiedTeamsAdapter extends RecyclerView.Adapter<QualifiedTeamsAdapter.QualifiedTeamViewHolder> {
    private List<ResultModel> resultModelList;
    private Context context;


    public QualifiedTeamsAdapter(List<ResultModel> eventResultsList, Context context) {
        this.resultModelList=eventResultsList;
        this.context=context;
    }

    @NonNull
    @Override
    public QualifiedTeamsAdapter.QualifiedTeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QualifiedTeamViewHolder(LayoutInflater.from(context).inflate(R.layout.item_qualified_teams,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull QualifiedTeamsAdapter.QualifiedTeamViewHolder holder, int position) {
        ResultModel result=resultModelList.get(position);
        holder.teamID.setText(result.getTeamID());
        if(result.getRound().toLowerCase().contains("f")||result.getRound().toLowerCase().contains("final")){
            holder.teamPosition.setVisibility(View.VISIBLE);
            holder.teamPosition.setText(result.getPosition()+".");
        }
    }

    @Override
    public int getItemCount() {
        return resultModelList.size();
    }

    class QualifiedTeamViewHolder extends RecyclerView.ViewHolder {
        TextView teamID;
        TextView teamPosition;
        public QualifiedTeamViewHolder(View itemView)
        {
            super(itemView);
            teamID = itemView.findViewById(R.id.qualified_teams_id);
            teamPosition = itemView.findViewById(R.id.qualified_team_position);
        }
    }
}
