package ru.practicum.ExploreWithMe.model;

import lombok.Data;

@Data
public class EventParam {
    private int[] users;
    private String[] states;
    private int[] categories;
    private String rangeStart;
    private String rangeEnd;
    private String text;
    private Boolean paid;
    private Boolean onlyAvailable;
    private String sort;

    public EventParam(int[] users,
                      String[] states,
                      int[] categories,
                      String rangeStart,
                      String rangeEnd) {
        this.users = users;
        this.states = states;
        this.categories = categories;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    public EventParam(int[] categories,
                      String rangeStart,
                      String rangeEnd,
                      String text,
                      Boolean paid,
                      Boolean onlyAvailable,
                      String sort) {
        this.categories = categories;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.text = text;
        this.paid = paid;
        this.onlyAvailable = onlyAvailable;
        this.sort = sort;
    }
}