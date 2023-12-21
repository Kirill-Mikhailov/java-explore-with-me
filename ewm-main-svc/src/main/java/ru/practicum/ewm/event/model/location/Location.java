package ru.practicum.ewm.event.model.location;

import lombok.Data;

import javax.persistence.*;

@Data
@Embeddable
public class Location {
    private Double lat;
    private Double lon;
}
