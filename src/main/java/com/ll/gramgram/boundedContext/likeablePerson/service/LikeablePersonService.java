package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final Rq rq;
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    private final MemberRepository memberRepository;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        RsData<LikeablePerson> canAddRsData = canAdd(member, username, attractiveTypeCode);

        if(canAddRsData.isFail()) {
            return canAddRsData;
        }

        if(canAddRsData.getResultCode().equals("S-2")) {
            LikeablePerson likeablePersonToUsername = canAddRsData.getData();
            String preAttractiveType = likeablePersonToUsername.getAttractiveTypeDisplayName();

            likeablePersonToUsername.setAttractiveTypeCode(attractiveTypeCode);
            return RsData.of("S-2", "%s에 대한 호감사유를 %s에서 %s(으)로 변경합니다.".formatted(username, preAttractiveType, likeablePersonToUsername.getAttractiveTypeDisplayName()));
        }

        InstaMember fromInstaMember = member.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장
        fromInstaMember.addFromLikeablePerson(likeablePerson); // member의 호감 표시
        toInstaMember.addToLikeablePerson(likeablePerson); // member에게 호감 표시

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    private RsData<LikeablePerson> canAdd(Member member, String username, int attractiveTypeCode) {
        if (!member.hasConnectedInstaMember()) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        LikeablePerson likeablePersonToUsername = likeablePersonRepository.findByFromInstaMemberIdAndToInstaMember_username(member.getInstaMember().getId(), username);
        if(likeablePersonToUsername!=null && likeablePersonToUsername.getAttractiveTypeCode() == attractiveTypeCode) {
            return RsData.of("F-3", "동일 대상에 중복으로 호감표시 할 수 없습니다.");
        }

        List<LikeablePerson> myLikes = member.getInstaMember().getFromLikeablePeople();
        if (myLikes.size() >= AppConfig.getLikeablePersonFromMax() && likeablePersonToUsername == null) {
            return RsData.of("F-4", "호감상대는 %s명까지 등록 가능합니다.".formatted(AppConfig.getLikeablePersonFromMax()));
        }

        if(likeablePersonToUsername != null) { // 호감 타입까지 같은 경우는 이 코드까지 도달하지 못한다.
            return RsData.of("S-2", "존재하는 호감 표시이나, 사유를 변경할 수 있습니다.", likeablePersonToUsername);
        }

        return RsData.of("S-1", "호감상대 등록 가능 상태입니다.");
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    public Optional<LikeablePerson> findById(Long id) {
        return likeablePersonRepository.findById(id);
    }

    @Transactional
    public RsData<LikeablePerson> delete(Long id, InstaMember instaMember) {
        RsData<LikeablePerson> likeablePersonRsData = canDelete(id, instaMember);

        if(likeablePersonRsData.isFail()) return likeablePersonRsData;

        likeablePersonRsData.getData().getFromInstaMember().removeToLikeablePerson(likeablePersonRsData.getData());
        likeablePersonRsData.getData().getToInstaMember().removeToLikeablePerson(likeablePersonRsData.getData());
        likeablePersonRepository.delete(likeablePersonRsData.getData());
        return RsData.of("S-1", "%s님에 대한 호감 표시가 철회되었습니다.".formatted(likeablePersonRsData.getData().getToInstaMemberUsername()));
    }

    private RsData<LikeablePerson> canDelete(Long id, InstaMember instaMember) {
        Optional<LikeablePerson> likeablePerson = likeablePersonRepository.findById(id);

        if(likeablePerson.isEmpty()) {
            return RsData.of("F-1", "존재하지 않는 항목입니다.");
        }

        if(!Objects.equals(instaMember.getId(), likeablePerson.get().getFromInstaMember().getId())) {
            return RsData.of("F-2", "삭제 권한이 없습니다.");
        }

        return RsData.of("S-1", "삭제 가능 상태입니다.", likeablePerson.get());
    }

    public long countByFromInstaMember_username(String username) {
        return likeablePersonRepository.countByFromInstaMember_username(username);
    }
}
