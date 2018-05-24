package in.mittt.tt18.models.categories;

import java.util.ArrayList;
import java.util.List;

public class CategoriesListModel {

    private List<CategoryModel> categoriesList = new ArrayList<>();

    public CategoriesListModel() {
        // Required empty public constructor
    }

    public List<CategoryModel> getCategoriesList() {
        return categoriesList;
    }

    public void setCategoriesList(List<CategoryModel> categoriesList) {
        this.categoriesList = categoriesList;
    }
}
