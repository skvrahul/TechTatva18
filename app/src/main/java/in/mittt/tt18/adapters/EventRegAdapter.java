package in.mittt.tt18.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import in.mittt.tt18.R;
import in.mittt.tt18.models.registration.RegEvent;

/**
 * Created by Saptarshi on 9/28/2018.
 */
public class EventRegAdapter extends RecyclerView.Adapter<EventRegAdapter.EventRegViewHolder>{
    private List<RegEvent> regEventsList;
    private Context context;
    private DeleteClickListener deleteClickListener;
    public interface DeleteClickListener{
        void onClick(RegEvent event);
    }
    public EventRegAdapter(List<RegEvent> regEventsList, Context context, DeleteClickListener deleteClickListener) {
        this.regEventsList = regEventsList;
        this.context = context;
        this.deleteClickListener = deleteClickListener;
    }

    @Override
    public EventRegViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new EventRegViewHolder(LayoutInflater.from(context).inflate(R.layout.item_event_reg, parent, false));
    }

    @Override
    public void onBindViewHolder(EventRegViewHolder holder, int position) {
        RegEvent regEvent = regEventsList.get(position);
        holder.onBind(regEvent);
    }

    @Override
    public int getItemCount() {
        return regEventsList.size();
    }

    class EventRegViewHolder extends RecyclerView.ViewHolder {
        TextView eventName;
        TextView teamID;
        View deleteEvent;
        public void onBind(final RegEvent regEvent){
            eventName.setText(regEvent.getEventName());
            teamID.setText(regEvent.getTeamID());
            deleteEvent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(deleteClickListener!=null){
                        deleteClickListener.onClick(regEvent);
                    }
                }
            });
        }
        public EventRegViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView)itemView.findViewById(R.id.event_name);
            teamID = (TextView)itemView.findViewById(R.id.team_id);
            deleteEvent = itemView.findViewById(R.id.event_modify);
        }
    }
}
