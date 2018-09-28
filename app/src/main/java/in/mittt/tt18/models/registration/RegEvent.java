package in.mittt.tt18.models.registration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Saptarshi on 9/28/2018.
 */
public class RegEvent {
    @SerializedName("0")
    @Expose
    private String _0;

    @SerializedName("1")
    @Expose
    private String _1;

    @SerializedName("2")
    @Expose
    private String _2;

    @Expose
    @SerializedName("event_name")
    private String eventName;

    @Expose
    @SerializedName("event_id")
    private String eventID;

    @Expose
    @SerializedName("team_id")
    private String teamID;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getTeamID() {
        return teamID;
    }

    public void setTeamID(String teamID) {
        this.teamID = teamID;
    }

    public String getEventID(){return eventID;}

    public void setEventID(String eventID){
        this.eventID=eventID;
    }
    public String get0() {
        return _0;
    }

    public void set0(String _0) {
        this._0 = _0;
    }

    public String get1() {
        return _1;
    }

    public void set1(String _1) {
        this._1 = _1;
    }

    public String get2() {
        return _2;
    }

    public void set2(String _2) {
        this._2 = _2;
    }
}

