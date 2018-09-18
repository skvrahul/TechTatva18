package in.mittt.tt18.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;



public class ScheduleListModel {

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("data")
    @Expose
    private List<ScheduleModel> data = new ArrayList<>();

    public List<ScheduleModel> getData() {
        return data;
    }

    public void setData(List<ScheduleModel> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}