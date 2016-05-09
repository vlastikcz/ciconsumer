package com.github.vlastikcz.ciconsumer.domain.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class CIReleaseProcessing {
    @Getter
    private final CIRelease ciRelease;
    @Getter
    private final List<String> finishedProcessors;

    public CIReleaseProcessing(CIRelease ciRelease, List<String> finishedProcessors) {
        this.ciRelease = ciRelease;
        this.finishedProcessors = new ArrayList<>(finishedProcessors);
    }
}
