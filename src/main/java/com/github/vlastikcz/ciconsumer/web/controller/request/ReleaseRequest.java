package com.github.vlastikcz.ciconsumer.web.controller.request;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
public class ReleaseRequest implements Serializable {
    @JsonSerialize
    @JsonProperty("number_of_release")
    private String numberOfRelease;
    @JsonSerialize
    @JsonProperty("datetime")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date dateTime;
}
