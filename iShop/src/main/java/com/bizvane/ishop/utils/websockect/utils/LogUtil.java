package com.bizvane.ishop.utils.websockect.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

/**
 * Created by iBo on 16/3/15.
 */

@Component
public class LogUtil {

    public static Logger getLogger(Class cls) {
        Logger logger = LogManager.getLogger(cls);
        return logger;
    }


}
