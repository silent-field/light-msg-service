package com.silent.lms.configuration;

import com.google.common.collect.Range;

public interface InternalConfigurations {
    int MESSAGE_ID_LOWER_LIMIT = 0;
    int MESSAGE_ID_UPPER_LIMIT = (1 << 16) - 1;
    int MESSAGE_ID_UPPER_OVERFLOW = 1 << 16;
    Range<Integer> MESSAGE_ID_RANGE = Range.openClosed(MESSAGE_ID_LOWER_LIMIT, MESSAGE_ID_UPPER_LIMIT);

    Range<Integer> PACKET_ID_RANGE = Range.closed(-1, MESSAGE_ID_UPPER_LIMIT);

    int MESSAGE_ID_PRODUCER_LOCK_SIZE = 8;

    // ------------------- 限制相关配置
}
