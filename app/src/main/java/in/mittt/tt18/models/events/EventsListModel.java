package in.mittt.tt18.models.events;

import java.util.List;

public class EventsListModel {

    private List<EventDetailsModel> events;

    public EventsListModel() {
    }

    public List<EventDetailsModel> getEvents() {
        return events;
    }

    public void setEvents(List<EventDetailsModel> events) {
        this.events = events;
    }
}
