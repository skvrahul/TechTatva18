package in.mittt.tt18.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import in.mittt.tt18.R;
import in.mittt.tt18.fragments.CategoriesFragment;
import in.mittt.tt18.fragments.EventsFragment;
import in.mittt.tt18.fragments.HomeFragment;
import in.mittt.tt18.fragments.ResultsFragment;
import in.mittt.tt18.fragments.WorkshopsFragment;
import in.mittt.tt18.models.categories.CategoriesListModel;
import in.mittt.tt18.models.categories.CategoryModel;
import in.mittt.tt18.models.events.EventDetailsModel;
import in.mittt.tt18.models.events.EventsListModel;
import in.mittt.tt18.models.events.ScheduleListModel;
import in.mittt.tt18.models.events.ScheduleModel;
import in.mittt.tt18.models.results.ResultModel;
import in.mittt.tt18.models.results.ResultsListModel;
import in.mittt.tt18.models.workshops.WorkshopsListModel;
import in.mittt.tt18.network.APIClient;
import in.mittt.tt18.utilities.BottomNavigationViewHelper;
import in.mittt.tt18.utilities.NetworkUtils;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView navigation;
    // Listener for the selected bottom navigation items
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // Load the respective fragments when each item is selected
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle(R.string.title_home);
                    return loadFragment(new HomeFragment());
                case R.id.navigation_schedule:
                    setTitle(R.string.title_schedule);
                    return loadFragment(new EventsFragment());
                case R.id.navigation_categories:
                    setTitle(R.string.title_categories);
                    return loadFragment(new CategoriesFragment());
                case R.id.navigation_workshops:
                    setTitle(R.string.title_workshops);
                    return loadFragment(new WorkshopsFragment());
                case R.id.navigation_results:
                    setTitle(R.string.title_results);
                    return loadFragment(new ResultsFragment());
            }
            return false;
        }
    };
    private Realm mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDatabase = Realm.getDefaultInstance();

        navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Load the home fragment first when the app launches
        setTitle(R.string.title_home);
        navigation.setSelectedItemId(R.id.navigation_home);

        boolean isConnected = NetworkUtils.isInternetConnected(this);
        if (isConnected) {
            loadAllFromInternet();
            Log.i(TAG, "onCreate: Connected and background updated");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_about_us: {
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                return true;
            }
            case R.id.menu_developers: {
                startActivity(new Intent(MainActivity.this, DevelopersActivity.class));
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadAllFromInternet() {
        loadResultsFromInternet();
        loadEventsFromInternet();
        loadSchedulesFromInternet();
        loadCategoriesFromInternet();
        loadWorkshopsFromInternet();
    }

    private void loadEventsFromInternet() {

        Call<EventsListModel> eventsCall = APIClient.getAPIInterface().getEventsList();
        eventsCall.enqueue(new Callback<EventsListModel>() {
            @Override
            public void onResponse(@NonNull Call<EventsListModel> call, @NonNull Response<EventsListModel> response) {
                if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                    Log.d(TAG, "onResponse: Loading events....");
                    mDatabase.beginTransaction();
                    mDatabase.where(EventDetailsModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealmOrUpdate(response.body().getEvents());
                    mDatabase.commitTransaction();
                    Log.d(TAG, "Events updated in background");
                }
            }

            @Override
            public void onFailure(@NonNull Call<EventsListModel> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: Events not updated ");
            }
        });
    }

    private void loadSchedulesFromInternet() {
        Call<ScheduleListModel> schedulesCall = APIClient.getAPIInterface().getScheduleList();
        schedulesCall.enqueue(new Callback<ScheduleListModel>() {
            @Override
            public void onResponse(@NonNull Call<ScheduleListModel> call, @NonNull Response<ScheduleListModel> response) {
                if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                    mDatabase.beginTransaction();
                    mDatabase.where(ScheduleModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealmOrUpdate(response.body().getData());
                    mDatabase.commitTransaction();
                    Log.d(TAG, "Schedule updated in background");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ScheduleListModel> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: Schedules not updated ");
            }
        });
    }

    private void loadCategoriesFromInternet() {
        Call<CategoriesListModel> categoriesCall = APIClient.getAPIInterface().getCategoriesList();
        categoriesCall.enqueue(new Callback<CategoriesListModel>() {
            @Override
            public void onResponse(@NonNull Call<CategoriesListModel> call, @NonNull Response<CategoriesListModel> response) {
                if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                    mDatabase.beginTransaction();
                    mDatabase.where(CategoryModel.class).findAll().deleteAllFromRealm();
                    //mDatabase.copyToRealmOrUpdate(response.body().getCategoriesList());
                    mDatabase.copyToRealmOrUpdate(response.body().getCategoriesList());
                    //mDatabase.where(CategoryModel.class).equalTo("categoryName", "minimilitia").or().equalTo("categoryName", "Mini Militia").or().equalTo("categoryName", "Minimilitia").or().equalTo("categoryName", "MiniMilitia").or().equalTo("categoryName", "MINIMILITIA").or().equalTo("categoryName", "MINI MILITIA").findAll().deleteAllFromRealm();
                    mDatabase.commitTransaction();
                    Log.d(TAG, response.body().getCategoriesList().size() + "Categories updated in background");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoriesListModel> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: Categories not updated");
            }
        });
    }

    private void loadResultsFromInternet() {
        Call<ResultsListModel> resultsCall = APIClient.getAPIInterface().getResultsList();
        resultsCall.enqueue(new Callback<ResultsListModel>() {
            List<ResultModel> results = new ArrayList<>();

            @Override
            public void onResponse(@NonNull Call<ResultsListModel> call, @NonNull Response<ResultsListModel> response) {
                if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                    results = response.body().getData();
                    mDatabase.beginTransaction();
                    mDatabase.where(ResultModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealmOrUpdate(results);
                    mDatabase.commitTransaction();
                    Log.d(TAG, "Results updated in the background");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultsListModel> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: Results not updated");
            }
        });
    }

    private void loadWorkshopsFromInternet() {
        Call<WorkshopsListModel> workshopsCall = APIClient.getAPIInterface().getWorkshopsList();
        workshopsCall.enqueue(new Callback<WorkshopsListModel>() {
            @Override
            public void onResponse(@NonNull Call<WorkshopsListModel> call, @NonNull Response<WorkshopsListModel> response) {
                if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                    mDatabase.beginTransaction();
                    mDatabase.copyToRealmOrUpdate(response.body().getWorkshopsList());
                    mDatabase.commitTransaction();
                    Log.d(TAG, response.body().getWorkshopsList().size() + "Workshops updated in background");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WorkshopsListModel> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: Workshops not updated");
            }
        });
    }

    /**
     * Loads fragment for each selected navigation item
     *
     * @param fragment to be loaded
     * @return true if successful else false
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }


    public void changeFragment(Fragment fragment) {
//        if(fragment.getClass() == ResultsTabsFragment.class){
//            navigation.setSelectedItemId(R.id.navigation_results);
//        }else
        if (fragment.getClass() == CategoriesFragment.class) {
            navigation.setSelectedItemId(R.id.navigation_categories);
        } else if (fragment.getClass() == EventsFragment.class) {
            navigation.setSelectedItemId(R.id.navigation_schedule);
        } else {
            Log.i(TAG, "changeFragment: Unexpected fragment passed!!");
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).replace(R.id.fragment_container, fragment);
        transaction.commit();
    }
}