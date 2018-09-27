package in.mittt.tt18.models.categories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;

public class CategoryModel extends RealmObject{

    @SerializedName("id")
    @Expose
    private String categoryID;

    @SerializedName("name")
    @Expose
    private String categoryName;

    @SerializedName("type")
    @Expose
    private String type;

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}