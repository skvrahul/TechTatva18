package in.mittt.tt18.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import in.mittt.tt18.R;
import in.mittt.tt18.models.favourites.FavouritesModel;
import in.mittt.tt18.utilities.IconCollection;

public class FavouritesEventsAdapter extends RecyclerView.Adapter<FavouritesEventsAdapter.EventViewHolder> {
    private List<FavouritesModel> favourites;
    private final EventClickListener eventListener;
    Activity activity;

    public interface EventClickListener {
        void onItemClick(FavouritesModel event);
    }
    public FavouritesEventsAdapter(List<FavouritesModel> favourites, EventClickListener eventListener, Activity activity) {
        this.favourites = favourites;
        this.eventListener = eventListener;
        this.activity = activity;
    }
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favourite_event, parent, false);
        return new EventViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        FavouritesModel event = favourites.get(position);
        holder.onBind(event);
    }
    @Override
    public int getItemCount() {
        return favourites.size();
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public ImageView eventLogo;
        public TextView eventRound;
        public TextView eventName;
        public TextView eventTime;
        public RelativeLayout eventItem;
        public EventViewHolder(View view) {
            super(view);
            eventRound = view.findViewById(R.id.fav_event_round_text_view);
            eventName = view.findViewById(R.id.fav_event_name_text_view);
            eventTime = view.findViewById(R.id.fav_event_time_text_view);
            eventItem = view.findViewById(R.id.fav_event_item);
            eventLogo = view.findViewById(R.id.fav_event_logo_image_view);
        }
        public void onBind(final FavouritesModel event) {
            eventName.setText(event.getEventName());
            eventRound.setText(event.getRound());
            eventTime.setText(event.getStartTime());
            eventItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(eventListener!=null){
                        eventListener.onItemClick(event);
                    }
                }
            });
            IconCollection collection = new IconCollection();
            eventLogo.setImageResource(collection.getIconResource(activity, event.getCatName()));
        }
    }

}
