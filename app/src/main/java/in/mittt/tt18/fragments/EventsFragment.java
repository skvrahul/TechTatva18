package in.mittt.tt18.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import in.mittt.tt18.R;
import in.mittt.tt18.activities.EventRegistrationActivity;
import in.mittt.tt18.activities.FavouritesActivity;
import in.mittt.tt18.activities.LoginActivity;
import in.mittt.tt18.activities.ProfileActivity;
import in.mittt.tt18.adapters.EventsAdapter;
import in.mittt.tt18.application.TT18;
import in.mittt.tt18.models.events.ScheduleModel;
import in.mittt.tt18.models.registration.EventRegistrationResponse;
import in.mittt.tt18.models.registration.LoginResponse;
import in.mittt.tt18.network.RegistrationClient;
import in.mittt.tt18.views.SwipeScrollView;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventsFragment extends Fragment {
    private final int NUM_DAYS = 4;
    TabLayout tabs;
    View eventsLayout;
    View view;
    LinearLayout noData;
    RecyclerView eventsRV;
    List<ScheduleModel> events;
    List<ScheduleModel> currentDayEvents = new ArrayList<>();
    List<ScheduleModel> filteredEvents = new ArrayList<>();
    EventsAdapter adapter;
    SwipeScrollView swipeSV;
    GestureDetector swipeDetector;
    private MenuItem searchItem;
    private MenuItem filterItem;
    int tabNumber;
    String[] sortCriteria = {"startTime", "eventName", "catName"};
    Sort[] sortOrder = {Sort.ASCENDING, Sort.ASCENDING, Sort.ASCENDING};
    private Realm realm;
    private String TAG = "EventsFragment";
    private int filterStartHour = 12;
    private int filterStartMinute = 30;
    private int filterEndHour = 23;
    private int filterEndMinute = 59;
    private String filterCategory = "All";
    private String filterVenue = "All";
    private String filterEventType = "All";
    private List<String> venueList = new ArrayList<>();
    private View rootView;
    private List<String> categoriesList = new ArrayList<>();
    private List<String> eventTypeList = new ArrayList<>();

    //    private int PREREVELS_DAY = -1;
    public static EventsFragment newInstance() {
        return new EventsFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        categoriesList.add("All");
        venueList.add("All");
        eventTypeList.add("All");
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().findViewById(R.id.toolbar).setElevation(0);
                AppBarLayout appBarLayout = getActivity().findViewById(R.id.app_bar);
                appBarLayout.setElevation(0);
                appBarLayout.setExpanded(true, true);
                appBarLayout.setTargetElevation(0);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void showAlert(String message) {
        new AlertDialog.Builder(getContext()).setIcon(R.drawable.ic_info).setTitle("Info").setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setCancelable(true).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        final View view = inflater.inflate(R.layout.fragment_events, container, false);
        rootView = view;
        initViews(view);
        events = realm.copyFromRealm(realm.where(ScheduleModel.class).findAll().sort(sortCriteria, sortOrder));

        if (events.size() == 0) {

        } else {
            EventsAdapter.FavouriteClickListener favClickListener = new EventsAdapter.FavouriteClickListener() {
                @Override
                public void onItemClick(ScheduleModel event, boolean add) {
                    //Favourite Clicked
                    if (add) {
                        Snackbar.make(view, event.getEventName() + " added to Favourites!", Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(view, event.getEventName() + " removed from Favourites!", Snackbar.LENGTH_SHORT).show();
                    }
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }
            };
            //Fetching list of Venues, Categories and Event names for the filter
            getAllCategories();
            getAllEvents();
            getAllVenues();

            EventsAdapter.EventLongPressListener longPressListener = new EventsAdapter.EventLongPressListener() {
                @Override
                public void onItemLongPress(ScheduleModel event) {

                    registerForEvent(event.getEventId());
                }
            };

            //Binding the Events RecyclerView to the EventsAdapter
            adapter = new EventsAdapter(getActivity(), filteredEvents, null, longPressListener, favClickListener);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            eventsRV.setLayoutManager(layoutManager);
            eventsRV.setItemAnimator(new DefaultItemAnimator());
            eventsRV.setAdapter(adapter);
            //Setting current day
            setCurrentDay();
        }
        return view;
    }

    private void setCurrentDay() {
        Calendar cal = Calendar.getInstance();
        Calendar day1 = new GregorianCalendar(2018, 9, 3);
        Calendar day2 = new GregorianCalendar(2018, 9, 4);
        Calendar day3 = new GregorianCalendar(2018, 9, 5);
        Calendar day4 = new GregorianCalendar(2018, 9, 6);
        Calendar curDay = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        /*if(curDay.getTimeInMillis() < day1.getTimeInMillis()){
            tabNumber =0;
        }else */
        if (curDay.getTimeInMillis() < day2.getTimeInMillis()) {
            tabNumber = 0;
        } else if (curDay.getTimeInMillis() < day3.getTimeInMillis()) {
            tabNumber = 1;
        } else if (curDay.getTimeInMillis() < day4.getTimeInMillis()) {
            tabNumber = 2;
        } else {
            tabNumber = 3;
        }
        try {
            TabLayout.Tab tabz = tabs.getTabAt(tabNumber);
            dayFilter(tabNumber + 1);
            applyFilters();
            tabz.select();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void registerForEvent(String eventID){
        Log.d(TAG, "registerForEvent: called");
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Trying to register you for event... please wait!");
        dialog.setCancelable(false);
        dialog.show();
        RequestBody body =  RequestBody.create(MediaType.parse("text/plain"), "eventid="+eventID);
        Call<EventRegistrationResponse> call = RegistrationClient.getRegistrationInterface(getContext()).eventReg(RegistrationClient.generateCookie(getContext()),body);
        call.enqueue(new Callback<EventRegistrationResponse>() {
            @Override
            public void onResponse(Call<EventRegistrationResponse> call, Response<EventRegistrationResponse> response) {
                dialog.cancel();
                if (response != null && response.body() != null){
                    try {
                        showAlert(response.body().getMessage());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
                    showAlert("Error! Please try again.");
                }
            }
            @Override
            public void onFailure(Call<EventRegistrationResponse> call, Throwable t) {
                dialog.cancel();
                showAlert("Error connecting to server! Please try again.");
            }
        });

    }
    private void getAllCategories() {
        RealmResults<ScheduleModel> scheduleResult = realm.where(ScheduleModel.class).findAll()
                .sort(sortCriteria, sortOrder);
        List<ScheduleModel> scheduleResultList = realm.copyFromRealm(scheduleResult);
        for (int i = 0; i < scheduleResultList.size(); i++) {
            String cat = scheduleResultList.get(i).getCatName();
            if (!categoriesList.contains(cat)) {
                categoriesList.add(cat);
            }
        }
    }

    private void getAllEvents() {
        RealmResults<ScheduleModel> scheduleResult = realm.where(ScheduleModel.class).findAll()
                .sort(sortCriteria, sortOrder);
        List<ScheduleModel> scheduleResultList = realm.copyFromRealm(scheduleResult);
        for (int i = 0; i < scheduleResultList.size(); i++) {
            String event = scheduleResultList.get(i).getEventName();
            if (!eventTypeList.contains(event)) {
                eventTypeList.add(event);
            }
        }
    }

    private void getAllVenues() {
        RealmResults<ScheduleModel> scheduleResult = realm.where(ScheduleModel.class).findAll()
                .sort(sortCriteria, sortOrder);
        List<ScheduleModel> scheduleResultList = realm.copyFromRealm(scheduleResult);
        for (int i = 0; i < scheduleResultList.size(); i++) {
            String venue = scheduleResultList.get(i).getVenue();
            if (!venueList.contains(venue)) {
                venueList.add(venue);
            }
        }
    }

    private void applyFilters() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.US);
        Date startDate;
        Date endDate;
        //Adding all the events of the current day to the currentDayEvents List and filtering those
        //If this step is not done then the filtering is done on the list that has already been filtered
//        if (tabs.getSelectedTabPosition() == 0) {
//            dayFilter(-1);//PreRevels
//        } else {

            dayFilter(tabs.getSelectedTabPosition() + 1);

//        }
        List<ScheduleModel> tempList = new ArrayList<>();
        tempList.addAll(currentDayEvents);


        for (ScheduleModel event : currentDayEvents) {
            try {
                if (!filterCategory.equals("All") && !filterCategory.toLowerCase().equals(event.getCatName().toLowerCase())) {
                    //Filtering the category
                    if (tempList.contains(event)) {
                        tempList.remove(event);
                        continue;
                    }
                }

                if (!filterVenue.equals("All") && !event.getVenue().toLowerCase().contains(filterVenue.toLowerCase())) {
                    //Filtering based on venue
                    if (tempList.contains(event)) {
                        tempList.remove(event);
                        continue;
                    }
                }

                if (!filterEventType.equals("All") && !filterEventType.toLowerCase().equals(event.getEventName().toLowerCase())) {
                    //Filtering based on Event Type
                    if (tempList.contains(event)) {
                        tempList.remove(event);
                        continue;
                    }
                }

                startDate = sdf.parse(event.getStartTime());
                endDate = sdf.parse(event.getEndTime());

                Calendar c1 = Calendar.getInstance();
                Calendar c2 = Calendar.getInstance();
                Calendar c3 = Calendar.getInstance();
                Calendar c4 = Calendar.getInstance();

                c1.setTime(startDate);
                c2.setTime(endDate);

                c3.set(c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DATE), filterStartHour, filterStartMinute, c1.get(Calendar.SECOND));
                c3.set(Calendar.MILLISECOND, c1.get(Calendar.MILLISECOND));
                c4.set(c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DATE), filterEndHour, filterEndMinute, c2.get(Calendar.SECOND));
                c4.set(Calendar.MILLISECOND, c2.get(Calendar.MILLISECOND));

                if (!((c1.getTimeInMillis() >= c3.getTimeInMillis()) && (c2.getTimeInMillis() <= c4.getTimeInMillis()))) {
                    if (tempList.contains(event)) {
                        tempList.remove(event);
                        continue;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (tempList.isEmpty()) {
            if (view != null)
                Snackbar.make(view, "No events found!", Snackbar.LENGTH_SHORT).show();
        } else {
            if (view != null)
                Snackbar.make(view, "Filters applied for Day " + getArguments().getInt("day", 1) + "!", Snackbar.LENGTH_SHORT).show();
        }

        if (adapter != null)
            adapter.updateList(tempList);

    }

    private void clearFilters() {
        filterStartHour = 12;
        filterStartMinute = 30;
        filterEndHour = 23;
        filterEndMinute = 59;
        filterCategory = "All";
        filterVenue = "All";
        filterEventType = "All";
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_schedule, menu);
        Log.d(TAG, "onCreateOptionsMenu: ");
        searchItem = menu.findItem(R.id.action_search);
        filterItem = menu.findItem(R.id.action_filter);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Initializing Components

                View view = View.inflate(getActivity(), R.layout.dialog_filter, null);
                final Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.setContentView(view);

                LinearLayout clearFiltersLayout = view.findViewById(R.id.clear_filters_layout);
                LinearLayout startTimeLayout = view.findViewById(R.id.filter_start_time_layout);
                final TextView startTimeTextView = view.findViewById(R.id.start_time_text_view);

                LinearLayout endTimeLayout = view.findViewById(R.id.filter_end_time_layout);
                final TextView endTimeTextView = view.findViewById(R.id.end_time_text_view);

                TextView cancelTextView = view.findViewById(R.id.filter_cancel_text_view);
                TextView applyTextView = view.findViewById(R.id.filter_apply_text_view);

                final Spinner categorySpinner = view.findViewById(R.id.category_spinner);
                final Spinner venueSpinner = view.findViewById(R.id.event_venue_spinner);
                final Spinner eventTypeSpinner = view.findViewById(R.id.event_type_spinner);

                ArrayAdapter<String> categorySpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_custom_spinner, categoriesList);
                categorySpinner.setAdapter(categorySpinnerAdapter);

                categorySpinner.setSelection(categoriesList.indexOf(filterCategory));

                ArrayAdapter<String> venueSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_custom_spinner, venueList);
                venueSpinner.setAdapter(venueSpinnerAdapter);

                venueSpinner.setSelection(venueList.indexOf(filterVenue));

                ArrayAdapter<String> eventTypeSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.item_custom_spinner, eventTypeList);
                eventTypeSpinner.setAdapter(eventTypeSpinnerAdapter);

                eventTypeSpinner.setSelection(eventTypeList.indexOf(filterEventType));

                String sTime = "";
                String eTime = "";

                if (filterStartHour < 12)
                    sTime = filterStartHour + ":" + (filterStartMinute < 10 ? "0" + filterStartMinute : filterStartMinute) + " AM";
                else if (filterStartHour == 12)
                    sTime = filterStartHour + ":" + (filterStartMinute < 10 ? "0" + filterStartMinute : filterStartMinute) + " PM";
                else if (filterStartHour > 12)
                    sTime = (filterStartHour - 12) + ":" + (filterStartMinute < 10 ? "0" + filterStartMinute : filterStartMinute) + " PM";

                if (filterEndHour < 12)
                    eTime = filterEndHour + ":" + (filterEndMinute < 10 ? "0" + filterEndMinute : filterEndMinute) + " AM";
                else if (filterEndHour == 12)
                    eTime = filterEndHour + ":" + (filterEndMinute < 10 ? "0" + filterEndMinute : filterEndMinute) + " PM";
                else if (filterEndHour > 12)
                    eTime = (filterEndHour - 12) + ":" + (filterEndMinute < 10 ? "0" + filterEndMinute : filterEndMinute) + " PM";

                startTimeTextView.setText(sTime);
                endTimeTextView.setText(eTime);

                cancelTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.hide();
                    }
                });

                applyTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        applyFilters();
                        dialog.hide();
                    }
                });

                categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        filterCategory = categorySpinner.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                venueSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        filterVenue = venueSpinner.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        filterEventType = eventTypeSpinner.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                startTimeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog tpDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String startTime = "";
                                filterStartHour = hourOfDay;
                                filterStartMinute = minute;
                                if (hourOfDay < 12)
                                    startTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute) + " AM";
                                else if (hourOfDay == 12)
                                    startTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute) + " PM";
                                else if (hourOfDay > 12)
                                    startTime = (hourOfDay - 12) + ":" + (minute < 10 ? "0" + minute : minute) + " PM";

                                startTimeTextView.setText(startTime);
                            }
                        }, filterStartHour, filterStartMinute, false);

                        tpDialog.show();
                    }
                });

                endTimeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog tpDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String endTime = "";
                                filterEndHour = hourOfDay;
                                filterEndMinute = minute;
                                if (hourOfDay < 12)
                                    endTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute) + " AM";
                                else if (hourOfDay == 12)
                                    endTime = hourOfDay + ":" + (minute < 10 ? "0" + minute : minute) + " PM";
                                else if (hourOfDay > 12)
                                    endTime = (hourOfDay - 12) + ":" + (minute < 10 ? "0" + minute : minute) + " PM";

                                endTimeTextView.setText(endTime);
                            }
                        }, filterEndHour, filterEndMinute, false);

                        tpDialog.show();
                    }
                });

                clearFiltersLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.hide();
                        clearFilters();
                        applyFilters();
                        if (eventsLayout != null)
                            Snackbar.make(rootView, "Filters cleared!", Snackbar.LENGTH_SHORT).show();
                    }
                });

                dialog.show();

                return false;

            }
        });
        final SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String text) {
                queryFilter(text);
                TT18.searchOpen = 2;
                return false;
            }
            @Override
            public boolean onQueryTextChange(String text) {
                queryFilter(text);
                TT18.searchOpen = 2;
                return false;
            }
        });
        searchView.setQueryHint("Search..");
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchView.clearFocus();
                TT18.searchOpen = 2;
                return false;
            }


        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile: {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (sp.getBoolean("loggedIn", false))
                    startActivity(new Intent(getActivity(), ProfileActivity.class));
                else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                return true;
            }
            case R.id.menu_favourites: {
                startActivity(new Intent(getActivity(), FavouritesActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    private void initViews(View view) {
        swipeDetector = new GestureDetector(view.getContext(), new SwipeListener());
        swipeSV = view.findViewById(R.id.events_swipe_scroll_view);
        swipeSV.setGestureDetector(swipeDetector);
        tabs = view.findViewById(R.id.tabs);
        eventsLayout = view.findViewById(R.id.events_linear_layout);
        eventsRV = view.findViewById(R.id.events_recycler_view);
        noData = view.findViewById(R.id.no_events_data_layout);
//        tabs.addTab(tabs.newTab().setText("Pre Revels"));
        for (int i = 0; i < NUM_DAYS; i++) {
            tabs.addTab(tabs.newTab().setText("Day " + (i + 1)));
        }
        DayTabListener tabListener = new DayTabListener();
        tabs.addOnTabSelectedListener(tabListener);
        eventsRV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                swipeDetector.onTouchEvent(motionEvent);
                return true;
            }
        });
    }

    public void dayFilter(int day) {
        currentDayEvents.clear();
//        //Filtering PreRevels events
//        Log.d(TAG, "dayFilter 1: " + day);
//        if (day == -1) {
//            for (int i = 0; i < events.size(); i++) {
//                Log.d(TAG, "dayFilter Value: " + events.get(i).getIsRevels());
//                if (events.get(i).getIsRevels().contains("0")) {
//                    currentDayEvents.add(events.get(i));
//                }
//            }
//            if (adapter != null) {
//                if (currentDayEvents.isEmpty()) {
//                    eventsRV.setVisibility(View.GONE);
//                    noData.setVisibility(View.VISIBLE);
//                } else {
//                    eventsRV.setVisibility(View.VISIBLE);
//                    noData.setVisibility(View.GONE);
//                }
//                adapter.updateList(currentDayEvents);
//            }
//            return;
//        }
        //Filtering the remaining events
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getDay().contains(day + "")) {
                currentDayEvents.add(events.get(i));
            }
        }
        if (adapter != null) {
            if (currentDayEvents.isEmpty()) {
                eventsRV.setVisibility(View.GONE);
                noData.setVisibility(View.VISIBLE);
            } else {
                eventsRV.setVisibility(View.VISIBLE);
                noData.setVisibility(View.GONE);
            }
            adapter.updateList(currentDayEvents);
        }
    }

    public void queryFilter(String query) {
        query = query.toLowerCase();
        List<ScheduleModel> temp = new ArrayList<>();
        for (int i = 0; i < currentDayEvents.size(); i++) {
            if ((currentDayEvents.get(i).getEventName().toLowerCase().contains(query) || currentDayEvents.get(i).getCatName().toLowerCase().contains(query))) {
                temp.add(currentDayEvents.get(i));
                Log.d(TAG, "queryFilter: " + currentDayEvents.get(i).getEventName());
            }
        }
        try {
            adapter.updateList(temp);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
            realm = null;
        }
    }

    class DayTabListener implements TabLayout.OnTabSelectedListener {


        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            Log.d(TAG, "onTabSelected TabPos: " + tab.getPosition());
            int day = tab.getPosition() + 1;
//            if (tab.getPosition() == 0) {
//                day = PREREVELS_DAY;
//            }
            Log.d(TAG, "onTabSelected: day = " + day);
            dayFilter(day);
            applyFilters();
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            int day = tab.getPosition() + 1;
            Log.d(TAG, "onTabUnselected: day =  " + day);

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            int day = tab.getPosition() + 1;
//            if (tab.getPosition() == 0) {
//                day = PREREVELS_DAY;
//            }
            Log.d(TAG, "onTabReselected: day = " + day);
            dayFilter(day);
            applyFilters();


        }
    }

    class SwipeListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 100;
        private static final int SWIPE_MAX_VERTICAL = 300;
        private static final int SWIPE_THRESHOLD_VELOCITY = 300;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.d(TAG, "onFling: " + (Math.abs(e1.getY() - e2.getY())));

            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && Math.abs(e1.getY() - e2.getY()) < SWIPE_MAX_VERTICAL) {
                // Right to left Swipe
                Log.d(TAG, "onFling: RtoL Fling");
                int tabIndex = tabs.getSelectedTabPosition();
                if (!(tabIndex == NUM_DAYS - 1)) {
                    //Selecting the next tab
                    TabLayout.Tab t = tabs.getTabAt(tabIndex + 1);
                    if (t != null)
                        t.select();
                }
                return false;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY && Math.abs(e1.getY() - e2.getY()) < SWIPE_MAX_VERTICAL) {
                // Left to right Swipe
                Log.d(TAG, "onFling: LtoR Fling");
                int tabIndex = tabs.getSelectedTabPosition();
                if (!(tabIndex == 0)) {
                    //Selecting the previous tab
                    TabLayout.Tab t = tabs.getTabAt(tabIndex - 1);
                    if (t != null)
                        t.select();
                }
                return false;
            }

            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Bottom to top
            } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                return false; // Top to bottom
            }
            return false;
        }
    }

}

