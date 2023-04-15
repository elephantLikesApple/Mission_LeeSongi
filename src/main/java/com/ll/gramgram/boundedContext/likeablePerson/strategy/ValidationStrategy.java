package com.ll.gramgram.boundedContext.likeablePerson.strategy;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.member.entity.Member;

public interface ValidationStrategy {
    RsData<LikeablePerson> validate(Member member, String username, int attractiveTypeCode);
}