package in.mittt.tt18.models.events;

import java.util.ArrayList;
import java.util.List;

public class ScheduleListModel {

    private List<ScheduleModel> data = new ArrayList<ScheduleModel>();

    public List<ScheduleModel> getData() {
        return data;
    }

    public void setData(List<ScheduleModel> data) {
        this.data = data;
    }
}
