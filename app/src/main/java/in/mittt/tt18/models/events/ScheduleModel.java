package in.mittt.tt18.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class ScheduleModel extends RealmObject {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("cat_id")
    @Expose
    private String catId;

    @SerializedName("event_id")
    @Expose
    private String eventId;

    @SerializedName("round")
    @Expose
    private String round;

    @SerializedName("venue")
    @Expose
    private String venue;

    @SerializedName("start_time")
    @Expose
    private String startTime;

    @SerializedName("end_time")
    @Expose
    private String endTime;

    @SerializedName("day")
    @Expose
    private String day;

    @SerializedName("date")
    @Expose
    private String date;

    @SerializedName("category")
    @Expose
    private String catName;

    @SerializedName("event")
    @Expose
    private String eventName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}