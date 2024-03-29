package in.mittt.tt18.adapters;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.mittt.tt18.R;
import in.mittt.tt18.models.events.EventDetailsModel;
import in.mittt.tt18.models.events.ScheduleModel;
import in.mittt.tt18.models.favourites.FavouritesModel;
import in.mittt.tt18.receivers.NotificationReceiver;
import in.mittt.tt18.utilities.IconCollection;
import in.mittt.tt18.views.TabbedDialog;
import io.realm.Realm;


public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {
    private final int PRE_REVELS_DAY_ZERO = 18;
    private final int EVENT_DAY_ZERO = 2;
    private final int PRE_REVELS_EVENT_MONTH = Calendar.FEBRUARY;
    private final int EVENT_MONTH = Calendar.OCTOBER;
    String TAG = "EventsAdapter";
    Realm realm = Realm.getDefaultInstance();
    private PendingIntent pendingIntent1 = null;
    private final EventClickListener eventListener;
    private final FavouriteClickListener favouriteListener;
    private final EventLongPressListener eventLongPressListener;
    private PendingIntent pendingIntent2 = null;
    private FragmentActivity activity;
    private List<ScheduleModel> eventScheduleList;

    public EventsAdapter(FragmentActivity activity, List<ScheduleModel> events,
                         EventClickListener eventListener,
                         EventLongPressListener eventLongPressListener, FavouriteClickListener favouriteListener) {
        this.eventScheduleList = events;
        this.eventListener = eventListener;
        this.favouriteListener = favouriteListener;
        this.eventLongPressListener = eventLongPressListener;
        this.activity = activity;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(itemView);
    }

    public void updateList(List<ScheduleModel> eventScheduleList) {
        this.eventScheduleList.clear();
        this.eventScheduleList.addAll(eventScheduleList);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        ScheduleModel event = eventScheduleList.get(position);
        holder.onBind(event, eventListener, eventLongPressListener, favouriteListener);
    }

    @Override
    public int getItemCount() {
        return eventScheduleList.size();
    }

    public boolean isFavourite(ScheduleModel event) {
        FavouritesModel favourite = realm.where(FavouritesModel.class).equalTo("id",
                event.getEventId()).equalTo("day", event.getDay()).equalTo("round", event.getRound()).findFirst();
        return favourite != null;

    }

    private void addFavourite(ScheduleModel eventSchedule) {
        FavouritesModel favourite = new FavouritesModel();
        Log.i(TAG, "addFavourite: " + eventSchedule.getEventId());
        //Get Corresponding EventDetailsModel from Realm
        EventDetailsModel eventDetails = realm.where(EventDetailsModel.class)
                .equalTo("eventId", eventSchedule.getEventId()).findFirst();
        //Create and Set Values for FavouritesModel
        favourite.setId(eventSchedule.getEventId());
        favourite.setCatID(eventSchedule.getCatId());
        favourite.setEventName(eventSchedule.getEventName());
        favourite.setRound(eventSchedule.getRound());
        favourite.setVenue(eventSchedule.getVenue());
        favourite.setDate(eventSchedule.getDate());
        favourite.setDay(eventSchedule.getDay());
        favourite.setStartTime(eventSchedule.getStartTime());
        favourite.setEndTime(eventSchedule.getEndTime());
//        favourite.setIsRevels(eventSchedule.getIsRevels());
        if (eventDetails != null) {
            favourite.setParticipants(eventDetails.getMaxTeamSize());
            favourite.setContactName(eventDetails.getContactName());
            favourite.setContactNumber(eventDetails.getContactNo());
            favourite.setCatName(eventDetails.getCatName());
            favourite.setDescription(eventDetails.getShortDesc());
        }
        //Commit to Realm
        if (realm != null) {
            realm.beginTransaction();
            realm.copyToRealm(favourite);
            realm.commitTransaction();
        }
        addNotification(eventSchedule/*, eventSchedule.getIsRevels()*/);

    }

    public void removeFavourite(ScheduleModel event) {
        realm.beginTransaction();
        realm.where(FavouritesModel.class).equalTo("id", event.getEventId())
                .equalTo("day", event.getDay()).equalTo("round", event.getRound()).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        removeNotification(event);
    }

    private void addNotification(ScheduleModel event/*, String isRevelsSTR */) {
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventId());
        intent.putExtra("catName", event.getCatName());
        Log.i("CategoryEventsAdapter", "addNotification: " + event.getStartTime());
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatId() + event.getEventId() + "0");
        int RC2 = Integer.parseInt(event.getCatId() + event.getEventId() + "1");
        pendingIntent1 = PendingIntent.getBroadcast(activity, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent2 = PendingIntent.getBroadcast(activity, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.US);
        Date d;
        try {
            d = sdf.parse(event.getStartTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
//        if (isRevelsSTR.contains("1")) {
            int eventDate = EVENT_DAY_ZERO + Integer.parseInt(event.getDay());   //event dates start from 04th October
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(d);
            calendar1.set(Calendar.MONTH, EVENT_MONTH);
            calendar1.set(Calendar.YEAR, 2018);
            calendar1.set(Calendar.DATE, eventDate);
            calendar1.set(Calendar.SECOND, 0);
            long eventTimeInMillis = calendar1.getTimeInMillis();
            calendar1.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY) - 1);

            Calendar calendar2 = Calendar.getInstance();
            Log.d("Calendar 1", calendar1.getTimeInMillis() + "");
            Log.d("Calendar 2", calendar2.getTimeInMillis() + "");

            if (calendar2.getTimeInMillis() <= eventTimeInMillis)
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);

            Calendar calendar3 = Calendar.getInstance();
            calendar3.set(Calendar.SECOND, 0);
            calendar3.set(Calendar.MINUTE, 30);
            calendar3.set(Calendar.HOUR, 8);
            calendar3.set(Calendar.AM_PM, Calendar.AM);
            calendar3.set(Calendar.MONTH, Calendar.OCTOBER);
            calendar3.set(Calendar.YEAR, 2018);
            calendar3.set(Calendar.DATE, eventDate);
            Log.d("Calendar 3", calendar3.getTimeInMillis() + "");
            if (calendar2.getTimeInMillis() < calendar3.getTimeInMillis()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(), pendingIntent2);

                Log.d("Alarm", "set for " + calendar3.toString());
            }
//        }
//        else {
//            Log.d(TAG, "addNotification: pre Revels");
//            int eventDate = PRE_REVELS_DAY_ZERO + Integer.parseInt(event.getDay());   //event dates start from 19th February
//            Calendar calendar1 = Calendar.getInstance();
//            calendar1.setTime(d);
//            calendar1.set(Calendar.MONTH, PRE_REVELS_EVENT_MONTH);
//            calendar1.set(Calendar.YEAR, 2018);
//            calendar1.set(Calendar.DATE, eventDate);
//            calendar1.set(Calendar.SECOND, 0);
//            long eventTimeInMillis = calendar1.getTimeInMillis();
//            calendar1.set(Calendar.HOUR_OF_DAY, calendar1.get(Calendar.HOUR_OF_DAY) - 1);
//
//            Calendar calendar2 = Calendar.getInstance();
//            Log.d("Calendar 1", calendar1.getTimeInMillis() + "");
//            Log.d("Calendar 2", calendar2.getTimeInMillis() + "");
//
//            if (calendar2.getTimeInMillis() <= eventTimeInMillis)
//                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent1);
//
//            Calendar calendar3 = Calendar.getInstance();
//            calendar3.set(Calendar.SECOND, 0);
//            calendar3.set(Calendar.MINUTE, 30);
//            calendar3.set(Calendar.HOUR, 8);
//            calendar3.set(Calendar.AM_PM, Calendar.AM);
//            calendar3.set(Calendar.MONTH, Calendar.FEBRUARY);
//            calendar3.set(Calendar.YEAR, 2018);
//            calendar3.set(Calendar.DATE, eventDate);
//            Log.d("Calendar 3", calendar3.getTimeInMillis() + "");
//            if (calendar2.getTimeInMillis() < calendar3.getTimeInMillis()) {
//                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar3.getTimeInMillis(), pendingIntent2);
//
//                Log.d("Alarm", "set for " + calendar3.toString());
//            }
//        }
    }

    private void removeNotification(ScheduleModel event) {
        Intent intent = new Intent(activity, NotificationReceiver.class);
        intent.putExtra("eventName", event.getEventName());
        intent.putExtra("startTime", event.getStartTime());
        intent.putExtra("eventVenue", event.getVenue());
        intent.putExtra("eventID", event.getEventId());

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        //Request Codes
        int RC1 = Integer.parseInt(event.getCatId() + event.getEventId() + "0");
        int RC2 = Integer.parseInt(event.getCatId() + event.getEventId() + "1");
        pendingIntent1 = PendingIntent.getBroadcast(activity, RC1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        pendingIntent2 = PendingIntent.getBroadcast(activity, RC2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent1);
        alarmManager.cancel(pendingIntent2);
    }

    private void displayEventDialog(final ScheduleModel event, Context context) {


        final View view = View.inflate(context, R.layout.event_dialog_info, null);
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        if(!sp.getBoolean("displayedLongPressHint",false)){
            Snackbar.make(view, "HINT:Long press on an event to register for it!", Snackbar.LENGTH_LONG)
                    .setAction("Got It!", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sp.edit().putBoolean("displayedLongPressHint", true);
                            sp.edit().apply();
                        }
                    })
                    .show();
        }
        final Dialog dialog = new Dialog(context);
        TabbedDialog td = new TabbedDialog();
        final String eventID = event.getEventId();
        final EventDetailsModel schedule = realm.where(EventDetailsModel.class).equalTo("eventId", eventID).findFirst();
        TabbedDialog.EventFragment.DialogFavouriteClickListener fcl = new TabbedDialog.EventFragment.DialogFavouriteClickListener() {
            @Override
            public void onItemClick(boolean add) {
                //TODO: App crashes when snackbar is displayed(Currently commented out).Fix crash

                if (add) {
                    addFavourite(event);
                    //Snackbar.make(view, event.getEventName()+" Added to Favourites", Snackbar.LENGTH_LONG).show();
                } else {
                    removeFavourite(event);
                    //Snackbar.make(view, event.getEventName()+" removed from Favourites", Snackbar.LENGTH_LONG).show();
                }
                notifyDataSetChanged();
            }
        };
        td.setValues(event, fcl, isFavourite(event), schedule);
        td.show(activity.getSupportFragmentManager(), "tag");
    }
    public interface EventClickListener {
        void onItemClick(ScheduleModel event, View view);
    }
    public interface FavouriteClickListener {
        void onItemClick(ScheduleModel event, boolean add);
    }

    public interface EventLongPressListener {
        void onItemLongPress(ScheduleModel event);
    }

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName, eventVenue, eventTime, eventRound, eventDate;
        public ImageView eventIcon, favIcon;
        public View container;

        public EventViewHolder(View view) {
            super(view);
            eventIcon = view.findViewById(R.id.event_logo_image_view);
            favIcon = view.findViewById(R.id.event_fav_ico);
            eventName = view.findViewById(R.id.event_name_text_view);
            eventDate = view.findViewById(R.id.event_date_text_view);
            eventVenue = view.findViewById(R.id.event_venue_text_view);
            eventTime = view.findViewById(R.id.event_time_text_view);
            eventRound = view.findViewById(R.id.event_round_text_view);
            container = view.findViewById(R.id.event_item_relative_layout);
        }

        public void onBind(final ScheduleModel event, final EventClickListener eventClickListener, final EventLongPressListener eventLongPressListener, final FavouriteClickListener favouriteListener) {
            eventDate.setText(event.getDate());
            eventName.setText(event.getEventName());
            eventTime.setText(event.getStartTime() + " - " + event.getEndTime());
            eventVenue.setText(event.getVenue());
            eventRound.setText("R".concat(event.getRound()));
            IconCollection icons = new IconCollection();
            eventIcon.setImageResource(icons.getIconResource(activity, event.getCatName()));
            if (isFavourite(event)) {
                favIcon.setImageResource(R.drawable.ic_fav_selected);
                favIcon.setTag("selected");
            } else {
                favIcon.setImageResource(R.drawable.ic_fav_deselected);
                favIcon.setTag("deselected");
            }
            favIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Favourites Clicked
                    String favTag = favIcon.getTag().toString();
                    if (favTag.equals("deselected")) {
                        favIcon.setTag("selected");
                        favIcon.setImageResource(R.drawable.ic_fav_selected);
                        addFavourite(event);
                        favouriteListener.onItemClick(event, true);
                    } else {
                        favIcon.setTag("deselected");
                        favIcon.setImageResource(R.drawable.ic_fav_deselected);
                        removeFavourite(event);
                        favouriteListener.onItemClick(event, false);
                    }
                }
            });
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: Event clicked" + event.getEventName());
                    displayEventDialog(event, view.getContext());
                }
            });
            container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(eventLongPressListener!=null){
                        eventLongPressListener.onItemLongPress(event);
                    }
                    return true;
                }
            });
        }
    }

}

