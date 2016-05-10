package com.github.vlastikcz.ciconsumer.web.controller.request;

import java.time.Instant;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ReleaseRequestConverter implements Converter<ReleaseRequest, ReleaseDetail> {

    @Override
    public ReleaseDetail convert(ReleaseRequest releaseRequest) {
        final String version = releaseRequest.getNumberOfRelease();
        final Instant timestamp = releaseRequest.getDateTime().toInstant();
        return new ReleaseDetail(version, timestamp);
    }
}
