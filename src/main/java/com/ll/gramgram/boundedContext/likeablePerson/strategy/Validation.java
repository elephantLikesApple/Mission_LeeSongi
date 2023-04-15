package com.ll.gramgram.boundedContext.likeablePerson.strategy;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

public class Validation {
    private final List<ValidationStrategy> strategies = new ArrayList<>();

    public Validation then(ValidationStrategy next) {
        strategies.add(next);
        return this;
    }

    public RsData<LikeablePerson> excute(Member member, String username, int attractiveTypeCode) {
        for(ValidationStrategy strategy : strategies) {
            RsData<LikeablePerson> result = strategy.validate(member, username, attractiveTypeCode);
            if(result.isFail()) {
                return result;
            }
            if(result.getResultCode().contains("S-2")) return result;
        }
        return RsData.of("S-1", "호감상대 등록 가능 상태입니다.");
    }

    public static class CheckConnectedInstaMember implements ValidationStrategy {
        public RsData<LikeablePerson> validate(Member member, String username, int attractiveTypeCode) {
            if (!member.hasConnectedInstaMember()) {
                return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
            }
            return RsData.of("S-3", "검증 성공");
        }
    }

    public static class CheckSelfLike implements ValidationStrategy {
        public RsData<LikeablePerson> validate(Member member, String username, int attractiveTypeCode) {
            if (member.getInstaMember().getUsername().equals(username)) {
                return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
            }
            return RsData.of("S-3", "검증 성공");
        }
    }

    @RequiredArgsConstructor
    public static class CheckDuplicatedLike implements ValidationStrategy {
        private final LikeablePersonRepository likeablePersonRepository;

        public RsData<LikeablePerson> validate(Member member, String username, int attractiveTypeCode) {
            LikeablePerson likeablePersonToUsername = likeablePersonRepository.findByFromInstaMemberIdAndToInstaMember_username(member.getInstaMember().getId(), username);
            if(likeablePersonToUsername != null && likeablePersonToUsername.getAttractiveTypeCode() == attractiveTypeCode) {
                return RsData.of("F-3", "동일 대상에 중복으로 호감표시 할 수 없습니다.");
            }
            return RsData.of("S-3", "검증 성공");
        }
    }

    @RequiredArgsConstructor
    public static class CheckMaxLikes implements ValidationStrategy {
        private final LikeablePersonRepository likeablePersonRepository;

        public RsData<LikeablePerson> validate(Member member, String username, int attractiveTypeCode) {
            LikeablePerson likeablePersonToUsername = likeablePersonRepository.findByFromInstaMemberIdAndToInstaMember_username(member.getInstaMember().getId(), username);
            List<LikeablePerson> myLikes = member.getInstaMember().getFromLikeablePeople();
            if (myLikes.size() >= AppConfig.getLikeablePersonFromMax() && likeablePersonToUsername == null) {
                return RsData.of("F-4", "호감상대는 %s명까지 등록 가능합니다.".formatted(AppConfig.getLikeablePersonFromMax()));
            }
            return RsData.of("S-3", "검증 성공");
        }
    }

    @RequiredArgsConstructor
    public static class CheckAttractiveTypeCodeChange implements ValidationStrategy {
        private final LikeablePersonRepository likeablePersonRepository;

        public RsData<LikeablePerson> validate(Member member, String username, int attractiveTypeCode) {
            LikeablePerson likeablePersonToUsername = likeablePersonRepository.findByFromInstaMemberIdAndToInstaMember_username(member.getInstaMember().getId(), username);
            if(likeablePersonToUsername != null && likeablePersonToUsername.getAttractiveTypeCode() != attractiveTypeCode) {
                return RsData.of("S-2", "존재하는 호감 표시이나, 사유를 변경할 수 있습니다.", likeablePersonToUsername);
            }
            return RsData.of("S-3", "검증 성공");
        }
    }
}