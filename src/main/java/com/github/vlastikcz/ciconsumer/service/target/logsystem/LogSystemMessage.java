package com.github.vlastikcz.ciconsumer.service.target.logsystem;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@JsonRootName("event")
public class LogSystemMessage {
    @Getter
    private final String what;
    @Getter
    private final String tags;
    @Getter
    @JsonProperty("datetime")
    private final String dateTime;

    public LogSystemMessage(String what, String tags, String dateTime) {
        this.what = what;
        this.tags = tags;
        this.dateTime = dateTime;
    }

}
