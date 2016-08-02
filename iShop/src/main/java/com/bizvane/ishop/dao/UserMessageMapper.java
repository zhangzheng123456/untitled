package com.bizvane.ishop.dao;

import com.bizvane.ishop.entity.DefUserMessage;
import com.bizvane.ishop.entity.RelUserMessage;

/**
 * Created by yin on 2016/8/2.
 */
public interface UserMessageMapper {
    int addDefUserMessage(DefUserMessage defUserMessage);

    int addRelUserMessage(RelUserMessage relUserMessage);
}
