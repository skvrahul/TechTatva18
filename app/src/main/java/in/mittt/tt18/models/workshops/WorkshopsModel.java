package in.mittt.tt18.models.workshops;

import android.app.Activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;


public class WorkshopsModel extends RealmObject {
    @SerializedName("wid")
    @Expose
    private String wid;
    @SerializedName("wname")
    @Expose
    private String wname;
    @SerializedName("wcost")
    @Expose
    private String wcost;
    @SerializedName("wdate")
    @Expose
    private String wdate;
    @SerializedName("wshuru")
    @Expose
    private String wshuru;
    @SerializedName("wkhatam")
    @Expose
    private String wkhatam;
    @SerializedName("wdesc")
    @Expose
    private String wdesc;
    @SerializedName("wvenue")
    @Expose
    private String wvenue;
    @SerializedName("cname")
    @Expose
    private String cname;
    @SerializedName("cnumb")
    @Expose
    private String cnumb;

    public WorkshopsModel(){

    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getWname() {
        return wname;
    }

    public void setWname(String wname) {
        this.wname = wname;
    }

    public String getWcost() {
        return wcost;
    }

    public void setWcost(String wcost) {
        this.wcost = wcost;
    }

    public String getWdate() {
        return wdate;
    }

    public void setWdate(String wdate) {
        this.wdate = wdate;
    }

    public String getWshuru() {
        return wshuru;
    }

    public void setWshuru(String wshuru) {
        this.wshuru = wshuru;
    }

    public String getWkhatam() {
        return wkhatam;
    }

    public void setWkhatam(String wkhatam) {
        this.wkhatam = wkhatam;
    }

    public String getWdesc() {
        return wdesc;
    }

    public void setWdesc(String wdesc) {
        this.wdesc = wdesc;
    }

    public String getWvenue() {
        return wvenue;
    }

    public void setWvenue(String wvenue) {
        this.wvenue = wvenue;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCnumb() {
        return cnumb;
    }

    public void setCnumb(String cnumb) {
        this.cnumb = cnumb;
    }



}
