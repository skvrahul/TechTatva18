package in.mittt.tt18.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.mittt.tt18.R;
import in.mittt.tt18.models.results.EventResultModel;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>{

    private List<EventResultModel> resultsList;
    private Context context;
    Activity activity;

    public ResultsAdapter(List<EventResultModel> resultsList, Context context, Activity activity){
        this.resultsList=resultsList;
        this.context=context;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ResultsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_results, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder holder, int position) {
        EventResultModel result = resultsList.get(position);
        holder.eventName.setText(result.eventName);
        holder.eventRound.setText(result.eventRound);

    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eventName;
        TextView eventRound;
        public ResultsViewHolder(View itemView) {
            super(itemView);
            eventName=itemView.findViewById(R.id.res_event_name_text_view);
            eventRound=itemView.findViewById(R.id.res_event_round_text_view);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {
            displayBottomSheet(resultsList.get(getAdapterPosition()));
        }
        public void displayBottomSheet(EventResultModel result)
        {
            View bottomSheetView =View.inflate(context,R.layout.dialog_results,null);
            final BottomSheetDialog dialog =new BottomSheetDialog(context);

            dialog.setContentView(bottomSheetView);

            BottomSheetBehavior bottomSheetBehavior=BottomSheetBehavior.from((View)bottomSheetView.getParent());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            TextView eventName=bottomSheetView.findViewById(R.id.result_dialog_event_name_text_view);
            eventName.setText(result.eventName);

            TextView eventRound=bottomSheetView.findViewById(R.id.result_dialog_round_text_view);
            eventRound.setText(result.eventRound);

            RecyclerView teamsRecyclerView=bottomSheetView.findViewById(R.id.result_dialog_teams_recycler_view);
            teamsRecyclerView.setAdapter(new QualifiedTeamsAdapter(result.eventResultsList,context));
            teamsRecyclerView.setLayoutManager(new GridLayoutManager(context,2));

            dialog.show();
        }

    }
}