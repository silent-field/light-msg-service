package com.silent.lms.configuration;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
public class LmsServerId {
    private String lmsServerId;

    private LmsServerId() {
        lmsServerId = generateId();
    }

    public String get() {
        return lmsServerId;
    }

    public String generateId() {
        return RandomStringUtils.randomAlphanumeric(10);
    }

    public void set(final String lmsServerId) {
        this.lmsServerId = lmsServerId;
    }

    // ------------------- 单例
    public static LmsServerId instance = InstanceHolder.instance;

    private static class InstanceHolder {
        private static final LmsServerId instance = new LmsServerId();
    }
}
