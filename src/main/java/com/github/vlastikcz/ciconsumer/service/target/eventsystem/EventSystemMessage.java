package com.github.vlastikcz.ciconsumer.service.target.eventsystem;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@JsonRootName("event")
public class EventSystemMessage {
    @Getter
    private final String id;
    @Getter
    private final short type;
    @Getter
    private final long timestamp;

    public EventSystemMessage(String id, long timestamp) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = 2;
    }
}
