# 1Week_LeeSongi.md

## Title: [1Week] 이송이

### 미션 요구사항 분석 & 체크리스트

---

- [x] 호감상대 삭제
  - [x] 대상 항목에 대한 소유권이 현재 로그인한 유저에게 있는지 체크해야 한다.
  - [x] likeable_person 테이블에서 {id} = id 인 데이터가 삭제되어야 한다.
  - [x] 삭제 후 다시 호감목록 페이지로 돌아와야 한다.
- [x] 구글 로그인 연동
  - [x] 스프링 OAuth2 client로 구현해야 한다.
  - [x] 구글 로그인으로 가입한 회원의 providerTypeCode는 'GOOGLE'이어야 한다.

### 1주차 미션 요약

---

**[접근 방법]**

- OAuth 클라이언트 아이디를 보호하기 위해서 application-oauth.yml파일을 분리하고 .gitignore에 포함했다.
- 다음 포스팅을 참고하여 커밋 메시지를 작성했다.
  - [Commit Convention이란?](https://kdjun97.github.io/git-github/commit-convention/)
  - [깃 커밋 메시지 컨벤션(Commit Convention), 커밋 메시지 작성요령](https://otugi.tistory.com/168)

- 예외사항(존재하지 않는 항목 삭제, 권한 없는 사용자의 삭제)에 대해 url요청으로 테스트 하는것이 어려웠다.
  - 'likeablePerson/delete/4'와 같이 직접 url에 요청을 보내면 Rq클래스의 referer에 값이 제대로 들어가지 않았다. 
  - 방법1 : 버튼을 만들어서 테스트 (테스트 후 삭제 필요)
    - 존재하지 않는 번호(999)를 삭제하는 요청을 보내는 버튼을 만들어서 테스트
    - user1 사용자로 로그인해 id=1인 항목(user3의 항목)을 삭제하는 요청을 보내는 버튼을 만들어서 테스트
  - 방법2 : 테스트 코드를 만들어 테스트
    - MockMvc의 request()함수를 사용해 Rq의 historyBackErrorMsg값이 오류메시지와 같은지 확인
    - ex) 존재하지 않는 항목입니다. / 삭제 권한이 없습니다. 


**[특이사항]**

- [x] 삭제 권한 체크 변경
  - [x] 데이터 조회는 pk인 id값으로
  - [x] rq 활용해 현재 로그인한 멤버 받아오기
- [x] 삭제 요청은 get이 아닌 delete로 변경하기
- [x] historyBack 기능 보완
- [x] 호감표시한 상대/호감표시 받은 상대 양방향으로 조회할 수 있도록 변경하기