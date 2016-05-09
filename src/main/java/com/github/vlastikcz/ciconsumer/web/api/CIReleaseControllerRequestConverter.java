package com.github.vlastikcz.ciconsumer.web.api;

import java.time.Instant;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.github.vlastikcz.ciconsumer.domain.entity.CIRelease;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CIReleaseControllerRequestConverter implements Converter<CIReleaseControllerRequest, CIRelease> {

    @Override
    public CIRelease convert(CIReleaseControllerRequest ciReleaseControllerRequest) {
        final String version = ciReleaseControllerRequest.getNumberOfRelease();
        final Instant timestamp = ciReleaseControllerRequest.getDateTime().toInstant();
        return new CIRelease(version, timestamp);
    }
}
