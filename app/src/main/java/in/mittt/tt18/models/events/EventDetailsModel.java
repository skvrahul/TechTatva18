package in.mittt.tt18.models.events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class EventDetailsModel extends RealmObject {

    @SerializedName("domain")
    @Expose
    private String domain;

    @SerializedName("id")
    @Expose
    @PrimaryKey
    private String eventId;

    @SerializedName("name")
    @Expose
    private String eventName;

    @SerializedName("short_desc")
    @Expose
    private String shortDesc;

    @SerializedName("long_desc")
    @Expose
    private String longDesc;

    @SerializedName("min_size")
    @Expose
    private String minTeamSize;

    @SerializedName("max_size")
    @Expose
    private String maxTeamSize;

    @SerializedName("contact_name")
    @Expose
    private String contactName;

    @SerializedName("contact_num")
    @Expose
    private String contactNo;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("category")
    @Expose
    private String catName;

    @SerializedName("cid")
    @Expose
    private String catId;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getEventID() {
        return eventId;
    }

    public void setEventID(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getShortDesc() {
        return shortDesc;
    }

    public void setShortDesc(String shortDesc) {
        this.shortDesc = shortDesc;
    }

    public String getLongDesc() {
        return longDesc;
    }

    public void setLongDesc(String longDesc) {
        this.longDesc = longDesc;
    }

    public String getMinTeamSize() {
        return minTeamSize;
    }

    public void setMinTeamSize(String minTeamSize) {
        this.minTeamSize = minTeamSize;
    }

    public String getMaxTeamSize() {
        return maxTeamSize;
    }

    public void setMaxTeamSize(String maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatID() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }
}