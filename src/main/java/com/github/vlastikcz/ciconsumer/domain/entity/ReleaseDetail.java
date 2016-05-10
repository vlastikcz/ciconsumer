package com.github.vlastikcz.ciconsumer.domain.entity;

import java.time.Instant;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ReleaseDetail {
    @Getter
    private final String version;
    @Getter
    private final Instant timestamp;

    public ReleaseDetail(String version, Instant timestamp) {
        this.version = version;
        this.timestamp = timestamp;
    }
}
