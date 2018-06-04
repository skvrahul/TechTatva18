package in.mittt.tt18.models.events;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class ScheduleListModel {

    @SerializedName("data")
    @Expose
    private List<ScheduleModel> data = new ArrayList<ScheduleModel>();

    public List<ScheduleModel> getData() {
        return data;
    }

    public void setData(List<ScheduleModel> data) {
        this.data = data;
    }

}