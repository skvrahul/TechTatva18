package in.mittt.tt18.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import in.mittt.tt18.R;
import in.mittt.tt18.models.categories.CategoriesListModel;
import in.mittt.tt18.models.categories.CategoryModel;
import in.mittt.tt18.models.events.EventDetailsModel;
import in.mittt.tt18.models.events.EventsListModel;
import in.mittt.tt18.models.results.ResultModel;
import in.mittt.tt18.models.results.ResultsListModel;
<<<<<<< HEAD
=======
import in.mittt.tt18.models.workshops.WorkshopsListModel;
>>>>>>> c3920adaab697de7b3894eccfc09e833dc73b1b9
import in.mittt.tt18.network.APIClient;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {

    boolean isWiFi;
    boolean isConnected;
    public Runnable test;
    private Realm mDatabase;
    boolean dataAvailableLocally;
    private Context context = this;
    private boolean categoriesDataAvailableLocally = false;
    private int apiCallsRecieved = 0;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mDatabase = Realm.getDefaultInstance();
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        dataAvailableLocally = sharedPreferences.getBoolean("dataAvailableLocally", false);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
        }

        if (dataAvailableLocally) {
            Log.d("Splash", "Data available locally");

            if (isConnected) {
                Log.d("Splash", "Is connected");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadAllFromInternet();
                        moveForward();
                    }
                }, 1000);
            } else {
                Log.d("Splash", "Not connected");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moveForward();
                    }
                }, 1000);
            }

        } else {
            Log.d("Splash", "Data not available locally");
            if (!isConnected) {
                Log.d("Splash", "Not connected");

            } else {
                Log.d("Splash", " connected");
                loadAllFromInternet();
            }
        }
    }

    private void loadAllFromInternet() {
        loadCategoriesFromInternet();
        loadResultsFromInternet();
        loadEventsFromInternet();

<<<<<<< HEAD
=======

>>>>>>> c3920adaab697de7b3894eccfc09e833dc73b1b9
        test = new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("dataAvailableLocally", true);
                editor.apply();
                moveForward();
            }
        };
        mHandler.postDelayed(test, 1000);
    }

    private void moveForward() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void loadResultsFromInternet() {
        Call<ResultsListModel> resultsCall = APIClient.getAPIInterface().getResultsList();
        resultsCall.enqueue(new Callback<ResultsListModel>() {
            @Override
            public void onResponse(@NonNull Call<ResultsListModel> call,
                                   @NonNull Response<ResultsListModel> response) {
                if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                    mDatabase.beginTransaction();
                    mDatabase.where(ResultModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getData());
                    mDatabase.commitTransaction();
                    Log.d("TAG","Results");
                }
            }
            @Override
            public void onFailure(Call<ResultsListModel> call, Throwable t) {
            }
        });
    }

    private void loadCategoriesFromInternet() {
        Call<CategoriesListModel> categoriesCall = APIClient.getAPIInterface().getCategoriesList();
        categoriesCall.enqueue(new Callback<CategoriesListModel>() {
            @Override
            public void onResponse(@NonNull Call<CategoriesListModel> call,
                                   @NonNull Response<CategoriesListModel> response) {
                if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                    apiCallsRecieved++;
                    mDatabase.beginTransaction();
                    mDatabase.where(CategoryModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getCategoriesList());
                    mDatabase.commitTransaction();
                    categoriesDataAvailableLocally = true;
                    Log.d("TAG", "Categories");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CategoriesListModel> call, @NonNull Throwable t) {

            }
        });
    }

    private void loadEventsFromInternet() {

        Call<EventsListModel> eventsCall = APIClient.getAPIInterface().getEventsList();
        eventsCall.enqueue(new Callback<EventsListModel>() {
            @Override
            public void onResponse(@NonNull Call<EventsListModel> call, @NonNull Response<EventsListModel> response) {
                if (response.isSuccessful() && response.body() != null && mDatabase != null) {
                    apiCallsRecieved++;
                    mDatabase.beginTransaction();
                    mDatabase.where(EventDetailsModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(response.body().getEvents());
                    mDatabase.commitTransaction();
                    //eventsDataAvailableLocally=true;
                    Log.d("TAG","Events");
                }
            }
<<<<<<< HEAD
=======

>>>>>>> c3920adaab697de7b3894eccfc09e833dc73b1b9
            @Override
            public void onFailure(Call<EventsListModel> call, Throwable t) {
                apiCallsRecieved++;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }
}
