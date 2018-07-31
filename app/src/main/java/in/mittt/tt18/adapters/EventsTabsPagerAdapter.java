package in.mittt.tt18.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import in.mittt.tt18.fragments.DaysFragment;


public class EventsTabsPagerAdapter extends FragmentStatePagerAdapter {
    String tabTitles[] = new String[]{"Day 1", "Day 2", "Day 3", "Day 4"};
    String searchTerm;
    String categoryFilter;
    String venueFilter;
    String startTimeFilter;
    String endTimeFilter;
    boolean filter;

    public EventsTabsPagerAdapter(FragmentManager fm, String searchTerm, String categoryFilter, String venueFilter, String startTimeFilter, String endTimeFilter, boolean filter) {
        super(fm);
        this.searchTerm = searchTerm;
        this.categoryFilter = categoryFilter;
        this.venueFilter = venueFilter;
        this.startTimeFilter = startTimeFilter;
        this.endTimeFilter = endTimeFilter;
        this.filter = filter;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return DaysFragment.newInstance(position, searchTerm, categoryFilter, venueFilter, startTimeFilter, endTimeFilter, filter);
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}