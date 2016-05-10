package com.github.vlastikcz.ciconsumer.service.target;

import com.github.vlastikcz.ciconsumer.domain.entity.ReleaseDetail;

public interface RemoteNotificationTarget {
    boolean notify(ReleaseDetail releaseDetail);
}
