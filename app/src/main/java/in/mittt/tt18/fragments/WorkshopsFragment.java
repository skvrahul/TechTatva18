package in.mittt.tt18.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
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
import in.mittt.tt18.adapters.WorkshopsAdapter;
import in.mittt.tt18.models.workshops.WorkshopsModel;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A {@link Fragment} for displaying the workshops.
 */
public class WorkshopsFragment extends Fragment {
    List<WorkshopsModel> mWorkshopList = new ArrayList<>();
    WorkshopsAdapter adapter;
    LinearLayout mNoDataLayout;
    private Realm mDatabase;
    private String TAG = WorkshopsFragment.class.getSimpleName();

    public WorkshopsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_workshops, container, false);
        mDatabase = Realm.getDefaultInstance();
        RealmResults<WorkshopsModel> realmResults = mDatabase.where(WorkshopsModel.class).findAll().sort("date");
        mWorkshopList = mDatabase.copyFromRealm(realmResults);
        Log.d(TAG, "onCreate: Length" + mWorkshopList.size());
        adapter = new WorkshopsAdapter(getActivity(), mWorkshopList);
        RecyclerView recyclerView = rootView.findViewById(R.id.workshop_recycler_view);
        mNoDataLayout = rootView.findViewById(R.id.no_workshop_data_layout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        if (mWorkshopList.isEmpty()) {
            Log.d(TAG, "onCreate: Empty Workshops list");
            recyclerView.setVisibility(View.INVISIBLE);
            mNoDataLayout.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "onCreate: Non-Empty Workshops list");
            recyclerView.setVisibility(View.VISIBLE);
            mNoDataLayout.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDatabase.close();
    }
}
