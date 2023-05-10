# 4Week_LeeSongi.md

## Title: [4Week] 이송이

### 미션 요구사항 분석 & 체크리스트

---
**필수미션**
- [x] 내가 받은 호감리스트에서 성별 필터링 기능 구현
  - [x] 특정 성별을 가진 사람에게서 받은 호감만 필터링해서 볼 수 있다.
- [x] 네이버클라우드플랫폼을 통한 배포 (도메인, https) 적용
  - [x] `https://도메인/` 형태로 접속이 가능
    - [bbosong.kro.kr](https://www.bbosong.kro.kr)
  - [x] 운영서버에서 각종 소셜로그인, 인스타아이디 연결이 잘 되어야 함


**선택미션**
- [x] 내가 받은 호감리스트에서 호감사유 필터링 기능 구현
  - [x] 특정 호감사유의 호감만 필터링해서 볼 수 있다.
- [x] 내가 받은 호감리스트에서 정렬 기능 구현
  - [x] 최신순(기본) : 가장 최근에 받은 호감표시를 우선적으로 표시
  - [x] 날짜순 : 가장 오래전에 받은 호감표시를 우선적으로 표시
  - [x] 인기 많은 순 : 가장 인기가 많은 사람들의 호감표시를 우선적으로 표시
  - [x] 인기 적은 순 : 가장 인기가 적은 사람들의 호감표시를 우선적으로 표시
  - [x] 성별순 : 여성에게 받은 호감표시 우선적으로 표시
    - 2순위 정렬 기준은 최신순
  - [x] 호감사유순 : 외모 -> 성격 -> 능력 순으로 우선적으로 표시
    - 2순위 정렬 기준은 최신순
- [ ] 젠킨스를 통해서 리포지터리의 main 브랜치에 커밋 이벤트 발생시 자동배포


### 4주차 미션 요약

---

**[접근 방법]**

**LikeablePersonControllerTests.java**
- 내가 받은 호감리스트에서 필터링 기능
  - 요청 : get(usr/likeablePerson/toList)
  - 매칭되는 함수 : LikeablePersonController.showToList(Model model)
  - 성공 여부 : model의 attribute를 읽어서 조건 확인
    - (t018) 성별 필터링
      - user4(insta_user4)가 받은 호감은 여성에게 1개
      - 모델에 넘어온 리스트의 fromInstaMember의 gender값이 모두 입력한 gender값과 같은지 확인
    - (t019) 호감사유 필터링
      - user4(insta_user4)가 받은 호감 사유는 1(외모)에 1개
      - 모델에 넘어온 리스트의 attractiveTypeCode가 모두 입력한 attractiveTypeCode와 같은지 확인

**LikeablePersonService.java**
- 내가 받은 호감리스트에서 필터링 기능
  - filterLikablePeople()
    - gender의 값이 W또는 M이었다가 전체로 돌아가면 `null`이 아니라 `""`이 된다.
    - gender의 값이 비었는지 검사는 이렇게 `gender != null && !gender.trim().equals("")`
    - attractiveTypeCode의 기본값은 0이다. (0인 경우는 필터링하지 않는다.)
- 내가 받은 호감리스트에서 정렬 기능
  - filterLikablePeople()
    - 기준1(기본) : 최신순
      - modifyDate가 가장 최근인것부터 오래된것 순으로 정렬
    - 기준2 : 날짜순
      - 최신순의 역방향, 오래된것부터 최근인것 순으로 정렬
    - 기준3 : 인기 많은 순 
      - fromInstaMember의 toLikeablePeople 리스트 길이가 긴 순으로 정렬
    - 기준4 : 인기 적은 순
    - 기준5 : 성별순
      - 여자 > 남자
      - 각 성별 중에선 최신순으로 정렬
    - 기준6 : 호감사유순
      - attractiveTypeCode 번호가 작은 순
      - 각 호감사유 중에선 최신순으로 정렬
    
**[특이사항]**
- 리펙토링
  - [ ] 필터링 함수에 querydsl 적용하기
  - [ ] 젠킨스 자동 배포