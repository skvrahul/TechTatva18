package in.mittt.tt18.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.mittt.tt18.R;
import in.mittt.tt18.models.workshops.WorkshopsModel;

public class WorkshopsAdapter extends RecyclerView.Adapter<WorkshopsAdapter.EventViewHolder> {
    private static final String TAG = WorkshopsAdapter.class.getSimpleName();
    private Activity activity;
    private List<WorkshopsModel> eventScheduleList;

    public WorkshopsAdapter(Activity activity, List<WorkshopsModel> events) {
        this.activity = activity;
        this.eventScheduleList = events;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workshops,
                parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        WorkshopsModel event = eventScheduleList.get(position);
        holder.onBind(event);
    }

    @Override
    public int getItemCount() {
        return eventScheduleList.size();
    }


    class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, eventVenue, eventTime, eventRound;
        ImageView eventIcon, favIcon;
        View container;

        EventViewHolder(View view) {
            super(view);
            eventIcon = view.findViewById(R.id.event_logo_image_view);
            favIcon = view.findViewById(R.id.event_fav_ico);
            eventName = view.findViewById(R.id.event_name_text_view);
            eventVenue = view.findViewById(R.id.event_venue_text_view);
            eventTime = view.findViewById(R.id.event_time_text_view);
            eventRound = view.findViewById(R.id.event_round_text_view);
            container = view.findViewById(R.id.event_item_relative_layout);
        }

        void onBind(final WorkshopsModel event) {
            favIcon.setVisibility(View.GONE);
            eventName.setText(event.getName());
            eventTime.setText(event.getStartTime() + " - " + event.getEndTime());
            eventVenue.setText(event.getVenue());
            eventRound.setText(event.getDate());
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: Event clicked" + event.getName());
                    displayEventDialog(event, view.getContext());
                }
            });
        }

        private void displayEventDialog(WorkshopsModel event, Context context) {
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            LayoutInflater inflater = LayoutInflater.from(context);
            final View workshopView = inflater.inflate(R.layout.dialog_workshop, null, false);
            TextView name = workshopView.findViewById(R.id.workshop_name);
            TextView date = workshopView.findViewById(R.id.workshop_date);
            TextView time = workshopView.findViewById(R.id.workshop_time);
            TextView venue = workshopView.findViewById(R.id.workshop_venue);
            TextView contact_name1 = workshopView.findViewById(R.id.workshop_contact_name);
            TextView contact_number1 = workshopView.findViewById(R.id.workshop_contact);
            TextView contact_name2 = workshopView.findViewById(R.id.workshop_contact_name2);
            TextView contact_number2 = workshopView.findViewById(R.id.workshop_contact2);
            TextView description = workshopView.findViewById(R.id.workshop_description);
            name.setText(event.getName());
            contact_name1.setText(R.string.cnt_wksp_1);
            contact_number1.setText("(".concat(workshopView.getResources().getString(R.string.cntno_wksp_1)).concat(")"));
            contact_name2.setText(R.string.cnt_wksp_2);
            contact_number2.setText("(".concat(workshopView.getResources().getString(R.string.cntno_wksp_2)).concat(")"));

            venue.setText(event.getVenue());
            time.setText(event.getStartTime() + " - " + event.getEndTime());
            date.setText(event.getDate());
            description.setText(event.getDesc());
            dialog.setContentView(workshopView);
            dialog.show();
        }
    }
}
