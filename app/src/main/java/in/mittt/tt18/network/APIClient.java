package in.mittt.tt18.network;

import in.mittt.tt18.models.categories.CategoriesListModel;
import in.mittt.tt18.models.events.EventsListModel;
import in.mittt.tt18.models.events.ScheduleListModel;
import in.mittt.tt18.models.results.ResultsListModel;
import in.mittt.tt18.models.workshops.WorkshopsListModel;
import in.mittt.tt18.models.workshops.WorkshopsModel;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class APIClient {
    private static Retrofit retrofit = null;

    private static final String BASE_URL = "https://api.mitportals.in/";

    public static APIInterface getAPIInterface(){

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        }
        return retrofit.create(APIInterface.class);
    }
    public interface APIInterface{
        @GET("events")
        Call<EventsListModel> getEventsList();

        @GET("categories")
        Call<CategoriesListModel> getCategoriesList();

        @GET("results")
        Call<ResultsListModel> getResultsList();

        @GET("schedule")
        Call<ScheduleListModel> getScheduleList();

<<<<<<< HEAD
=======
        @GET("workshops")
        Call<WorkshopsListModel> getWorkshopsList();

>>>>>>> c3920adaab697de7b3894eccfc09e833dc73b1b9
    }

}

