package com.github.vlastikcz.ciconsumer.service.target.logsystem;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;

public class LogSystemMessageFactory {
    private static final String WHAT_PREFIX = "DEPLOY-";
    private static final String TAGS = "code-release";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").withZone(ZoneId.systemDefault());

    public static LogSystemMessage fromReleaseDetail(ReleaseDetail releaseDetail) {
        final String what = WHAT_PREFIX + releaseDetail.getVersion();
        final String dateTime = DATE_TIME_FORMATTER.format(releaseDetail.getTimestamp());
        return new LogSystemMessage(what, TAGS, dateTime);
    }
}
