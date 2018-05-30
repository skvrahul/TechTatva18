package in.mittt.tt18.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.mittt.tt18.R;
import in.mittt.tt18.models.results.EventResultModel;

public class  ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder>{

    private List<EventResultModel> resultsList;
    private Context context;
    Activity activity;

    public ResultsAdapter(List<EventResultModel> resultsList, Context context, Activity activity){
        this.resultsList=resultsList;
        this.context=context;
        this.activity=activity;
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResultsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_results, parent, false));
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder holder, int position) {
        EventResultModel result = resultsList.get(position);
        holder.eventName.setText(result.eventName);
        holder.eventRound.setText(result.eventRound);

    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView eventLogo;
        TextView eventName;
        TextView eventRound;
        public ResultsViewHolder(View itemView) {
            super(itemView);


            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view) {

        }

    }
}