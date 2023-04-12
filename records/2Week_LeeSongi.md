# 2Week_LeeSongi.md

## Title: [2Week] 이송이

### 미션 요구사항 분석 & 체크리스트

---

- [x] 호감 상대 등록 예외처리하기
  - [x] 1️⃣동일 대상에게 중복으로 호감표시 요청
    - [x] 사유가 다른 경우는 사유를 업데이트 (S-2)
    - [x] 사유가 같은 경우는 처리 불가
    - [x] 처리 불가한 경우 rq.historyBack
  - [x] 2️⃣호감표시는 최대 10개로 제한
    - [x] 처리 불가한 경우 rq.historyBack

-[x] 네이버 로그인 연동
  - [x] 스프링 Oauth2 클라이언트로 구현
  - [x] providerTypeCode: NAVER
  - [x] member테이블의 username에 이름과 성별 정보는 포함되지 않도록
    - [x] (1단계) NAVER__{"id":2222222222, "gender":"W", "name":"홍길동"}
    - [x] (2단계) NAVER__2222222222

### 2주차 미션 요약

---

**[접근 방법]**

**LikeablePersonService.java**
- 1️⃣동일 대상에게 중복으로 호감표시 요청
  - member가 한 호감표시 중, username에게 보낸 호감표시 찾기
    - member가 한 호감표시는 InstaMember에 FromLikeablerPeople로 저장돼있음
    - username에게 보낸 호감 표시는 for문 혹은, stream filter로 찾을 수 있음
  - 사유가 동일한지 여부에 따라 다르게 처리
    - 사유가 동일한 경우 F-3, 에러 처리하기
    - 사유가 다른 경우 S-2, 사유를 업데이트하기
- 2️⃣호감표시는 최대 10개로 제한
  - 기존에 호감표시를 했던 사람에 대해서는 사유 수정이 가능해야 함
    - 동일 대상 중복 호감표시 처리보다 늦게 검사하기
  - member가 한 호감표시가 10개 이상이라면 처리 불가

**LikeablePersonControllerTests.java**
- 1️⃣동일 대상에게 중복으로 호감표시 요청
  - t009 -> 사유 : 외모를 외모로 변경 : F-3 실패, historyBack
  - t010 -> 사유 : 외모를 성격으로 변경 : S-2 성공, redirected
    - 동일 대상에 호감표시가 여전히 한개인지 체크하기
- 2️⃣호감표시 최대 10개로 제한
  - t011 -> 새로운 대상에게 호감표시 : F-4 실패, historyBack
    - 호감표시 개수 늘어났는지 체크하기
  - t012 -> 기존 대상에게 호감표시, 사유 변경 : S-2 성공, redirected
    - 호감표시 개수 늘어났는지 체크하기

**CustomOAuth2UserService.java**
- application-oauth.yml 파일에 설정 추가
  - git-ignore 되었음
- 다음 블로그 글을 참고하여 계정 정보에서 id만 추출하였음
  - [OAuth 네이버 로그인하기](https://lotuus.tistory.com/80)


**[특이사항]**
- [x] 호감표시 제한 횟수를 application.yml에 지정하기
- [ ] 호감표시 중복 확인을 위해 검색하는 코드를 stream방식에서 JPA방식으로 변경
  - 제한 횟수가 늘어날수록, stream으로 검색하기에 웹서버에 부하가 늘어남
  - 어플의 성능과 확장성을 위해 JPA방식으로 변경하기
- [x] 테스트코드 보완하기
  - [x] t009
    - 새로운 호감표시 이전의 likeablePerson의 수와 이후의 LikeablePerson의 수 비교
    - 두 수가 같으면 테스트 통과