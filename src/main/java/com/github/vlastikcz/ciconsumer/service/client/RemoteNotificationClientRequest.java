package com.github.vlastikcz.ciconsumer.service.client;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class RemoteNotificationClientRequest {
    @Getter
    private final String url;
    @Getter
    private final Object body;

    public RemoteNotificationClientRequest(String url, Object body) {
        this.url = url;
        this.body = body;
    }
}
