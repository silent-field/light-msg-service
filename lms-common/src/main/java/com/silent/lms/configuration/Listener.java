package com.silent.lms.configuration;

import com.silent.lms.annotations.NotNull;

/**
 * @author gy
 * @version 1.0
 * @date 2021/5/2
 * @description:
 */
public interface Listener {
    int getPort();

    String getBindAddress();

    String readableName();

    @NotNull
    String getName();
}
