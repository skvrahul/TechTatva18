package in.mittt.tt18.models.workshops;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class WorkshopsListModel {


    @SerializedName("data")
    @Expose
    private List<WorkshopsModel> workshopsList = new ArrayList<>();

    public WorkshopsListModel() {
    }

    public List<WorkshopsModel> getWorkshopsList() {
        return workshopsList;
    }

    public void setWorkshopsList(List<WorkshopsModel> workshopsList) {
        this.workshopsList = workshopsList;
    }
}

