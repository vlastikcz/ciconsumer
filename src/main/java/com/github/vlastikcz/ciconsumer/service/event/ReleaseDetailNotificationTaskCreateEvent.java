package com.github.vlastikcz.ciconsumer.service.event;

import java.util.Objects;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetailNotificationTask;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ReleaseDetailNotificationTaskCreateEvent {
    @Getter
    private final ReleaseDetailNotificationTask releaseDetailNotificationTask;

    public ReleaseDetailNotificationTaskCreateEvent(ReleaseDetailNotificationTask releaseDetailNotificationTask) {
        this.releaseDetailNotificationTask = Objects.requireNonNull(releaseDetailNotificationTask, "'releaseDetailNotificationTask' cannot be null");
    }
}
