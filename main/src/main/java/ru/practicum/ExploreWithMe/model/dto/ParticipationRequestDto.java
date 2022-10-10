package ru.practicum.ExploreWithMe.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ExploreWithMe.model.Status;

@Data
@AllArgsConstructor
public class ParticipationRequestDto {
    private long id;
    private String created;
    private Status status;
    private long event;
    private long requester;
}
