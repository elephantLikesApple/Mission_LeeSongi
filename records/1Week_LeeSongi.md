# 1Week_LeeSongi.md

## Title: [1Week] 이송이

### 미션 요구사항 분석 & 체크리스트

---

- [ ] 호감상대 삭제
  - [ ] 대상 항목에 대한 소유권이 현재 로그인한 유저에게 있는지 체크해야 한다.
  - [ ] likeable_person 테이블에서 {id} = id 인 데이터가 삭제되어야 한다.
  - [ ] 삭제 후 다시 호감목록 페이지로 돌아와야 한다.
- [ ] 구글 로그인 연동
  - [ ] 스프링 OAuth2 client로 구현해야 한다.
  - [ ] 구글 로그인으로 가입한 회원의 providerTypeCode는 'GOOGLE'이어야 한다.

### 1주차 미션 요약

---

**[접근 방법]**

- OAuth 클라이언트 아이디를 보호하기 위해서 application-oauth.yml파일을 분리하고 .gitignore에 포함했다.
- 다음 포스팅을 참고하여 커밋 메시지를 작성했다.
  - [Commit Convention이란?](https://kdjun97.github.io/git-github/commit-convention/)
  - [깃 커밋 메시지 컨벤션(Commit Convention), 커밋 메시지 작성요령](https://otugi.tistory.com/168)

**[특이사항]**