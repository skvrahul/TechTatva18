package in.mittt.tt18.fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import in.mittt.tt18.R;
import in.mittt.tt18.adapters.ResultsAdapter;
import in.mittt.tt18.models.results.EventResultModel;
import in.mittt.tt18.models.results.ResultModel;
import in.mittt.tt18.models.results.ResultsListModel;
import in.mittt.tt18.network.APIClient;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A {@link Fragment} for displaying the results.
 */
public class ResultsFragment extends Fragment {
    Realm mDatabase;
    private List<EventResultModel> resultsList = new ArrayList<>();
    private ResultsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout rootLayout;
    private LinearLayout noResultsLayout;
    private CardView resultsAvailable;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment newInstance(){
        return new ResultsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getActivity() != null){
            getActivity().setTitle("Results");
        }
        mDatabase = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_results,container,false);
        rootLayout = view.findViewById(R.id.results_root_layout);
        resultsAvailable = view.findViewById(R.id.results_available);
        noResultsLayout = view.findViewById(R.id.no_results_layout);
        RecyclerView resultsRecyclerView = view.findViewById(R.id.results_recycler_view);
        swipeRefreshLayout = view.findViewById(R.id.results_swipe_refresh_layout);
        adapter = new ResultsAdapter(resultsList,getContext(),getActivity());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(resultsRecyclerView.getContext(),DividerItemDecoration.VERTICAL);
        resultsRecyclerView.addItemDecoration(dividerItemDecoration);
        resultsRecyclerView.setAdapter(adapter);
        resultsRecyclerView.setLayoutManager(layoutManager);
        displayData();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectivityManager cmTemp = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkTemp = cmTemp.getActiveNetworkInfo();
                boolean isConnectedTemp = activeNetworkTemp != null && activeNetworkTemp.isConnectedOrConnecting();
                if(isConnectedTemp){
                    updateData();
                }
                else
                {
                    Snackbar.make(view,"Check connection!",Snackbar.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
        });
        return view;
    }
    private void displayData(){
        if(mDatabase==null){
            Log.d("results", "Database is null");
            resultsAvailable.setVisibility(View.GONE);
            noResultsLayout.setVisibility(View.VISIBLE);
            return;
        }

        RealmResults<ResultModel> realmResults = mDatabase.where(ResultModel.class).findAll();
        realmResults.sort("eventName", Sort.ASCENDING,"position", Sort.ASCENDING);
        List<ResultModel> results = mDatabase.copyFromRealm(realmResults);

        Log.d("TAG", "" + results.isEmpty());
        if(!results.isEmpty()){
            resultsList.clear();
            List<String> eventNamesList = new ArrayList<>();

            for(ResultModel result : results) {
                String eventName = result.getEventName()+" "+result.getRound();
                if(eventNamesList.contains(eventName)){
                    resultsList.get(eventNamesList.indexOf(eventName)).eventResultsList.add(result);
                }
                else
                {
                    EventResultModel eventResult = new EventResultModel();
                    eventResult.eventName = result.getEventName();
                    eventResult.eventRound= result.getRound();
                    eventResult.eventCategory = result.getCatName();
                    eventResult.eventResultsList.add(result);
                    resultsList.add(eventResult);
                    eventNamesList.add(eventName);
                }
            }
            adapter.notifyDataSetChanged();
        }
        else
        {
            resultsAvailable.setVisibility(View.GONE);
            noResultsLayout.setVisibility(View.VISIBLE);
        }
    }
    public void updateData(){
        Call<ResultsListModel> callResultsList = APIClient.getAPIInterface().getResultsList();
        callResultsList.enqueue(new Callback<ResultsListModel>() {
            List<ResultModel> results=new ArrayList<>();
            @Override
            public void onResponse(@NonNull Call<ResultsListModel> call, @NonNull Response<ResultsListModel> response) {
                if(response.isSuccessful() && response.body() != null){
                    results = response.body().getData();
                    mDatabase.beginTransaction();
                    mDatabase.where(ResultModel.class).findAll().deleteAllFromRealm();
                    mDatabase.copyToRealm(results);
                    mDatabase.commitTransaction();
                    noResultsLayout.setVisibility(View.GONE);
                    resultsAvailable.setVisibility(View.VISIBLE);
                    displayData();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResultsListModel> call, @NonNull Throwable t) {
                resultsAvailable.setVisibility(View.GONE);
                noResultsLayout.setVisibility(View.VISIBLE);
                Snackbar.make(rootLayout, "Error fetching results", Snackbar.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.close();
        mDatabase = null;
    }

}
