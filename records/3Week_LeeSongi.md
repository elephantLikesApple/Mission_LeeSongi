# 3Week_LeeSongi.md

## Title: [3Week] 이송이

### 미션 요구사항 분석 & 체크리스트

---

- [x] 개별 호감표시건에 대해 3시간동안 호감취소와 호감사유 변경 불가
    - [x] UI(form)에서 구현
    - [x] 백엔드 요청이 들어오면 쿨타임 확인하기
      - [x] 호감 사유변경 요청
      - [x] 호감 취소 요청

- [x] 네이버클라우드플랫폼을 통한 배포
    - ~~[x] (도메인 없이, IP로 접속) `https://서버IP:포트/` 형태로 접속~~
      - ~~https://101.101.216.169:8080/~~
      - 도메인 적용하여 지금은 작동하지 않음
    - [x] 도메인으로 접속
      - https://www.bbosong.kro.kr
      - 운영 서버에서는 소셜로그인, 인스타 연동 안되어도 됨

- [ ] 알림기능 구현
  - [ ] 호감 표시를 받은 경우 알림 확인
  - [ ] 기존 호감 표시의 사유가 변경된 경우 알림 확인
  - [ ] 각각의 알림은 생성시에 readDate가 null
  - [ ] 사용자가 알림을 읽으면 readDate을 현재날짜로 갱신

### 3주차 미션 요약

---

**[접근 방법]**

**LikeablePersonControllerTests**
- 쿨타임이 제대로 적용됐는지 확인하기
  - 지금 막 생성한 호감표시(likeablePerson/1)
    - (t017) 수정할 때 : request().attribute("historyBackErrorMsg", "2시간 59분 후에 해당 호감표시를 수정할 수 있습니다.")
    - (t019) 삭제할 때 : request().attribute("historyBackErrorMsg", "2시간 59분 후에 해당 호감표시를 삭제할 수 있습니다.")
  - 쿨타임이 지난 호감표시(likeablePerson/2)
    - (t016) 수정할 때 : status().is3xxRedirection()
    - (t018) 삭제할 때 : status().is3xxRedirection()
    
**LikeablePersonService**
- 호감사유 수정하기 전 canModifyLike() 함수에서 쿨타임 지났는지 확인
  - 쿨타임 지났을 때 : S-1
  - 쿨타임 남았을 때 : F-3, likeablePerson.getModifyUnlockDateRemainStrHuman() 호출
- 호감사유 삭제하기 전 canCancel() 함수에서 쿨타임 지났는지 확인
  - 쿨타임 지났을 때 : S-1
  - 쿨타임 남았을 때 : F-3, likeablePerson.getModifyUnlockDateRemainStrHuman() 호출

**LikeablePerson**
- 쿨타임 얼마나 남았는지 `HH시간 mm분` 형식으로 출력
- LocalDateTime은 날짜와 시간을 모두 고려해서 시간차이를 구하기 까다롭다
  - 타임스탬프로 변환하여 남은 시간, 분을 계산
  - 원하는 형식의 문자열로 변환

**[특이사항]**

리펙토링 리스트
- [ ] 배포된 도메인에서 소셜 로그인 동작하도록 하기
- [ ] 추가 미션(알림 기능)구현하기