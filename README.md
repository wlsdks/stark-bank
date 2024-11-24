### 준비물
- Java 23
- PostgreSQL
- MongoDB
- IDE (IntelliJ IDEA, Eclipse, VSCode, etc...)

### 몽고DB 설정
- docker-compose.yml 파일을 실행하여 몽고DB를 설정합니다.
- 몽고DB는 읽기 Query 전용 DB로 사용합니다.
- 몽고DB 실행 후 웹 콘솔 접속하기: http://localhost:8101/
  - 계정: admin
  - 비밀번호: admin123

### 프로젝트 설명
- 제가 공부하고 싶어서 만든 CQRS 예시 프로젝트입니다.
- 코드 완성을 위해 도움받은 AI
  - [Claude3.5 Sonnet]
  - [OpenAI chatGPT]

### CQRS 패턴
- Command Query Responsibility Segregation (CQRS)
  - Command: 명령
    - Command는 데이터를 변경하는 작업을 담당합니다.
    - Command는 데이터를 변경하고, 변경된 데이터를 Event로 발행합니다.
  - Query: 조회
    - Query는 데이터를 조회하는 작업을 담당합니다.
  - 이벤트 소싱
    1. Command 비즈니스 로직에서 데이터를 변경하고 테이블에 저장 or 업데이트합니다. 
    2. 이후 저장/업데이트에 대한 이벤트를 생성하고 DB의 Event Stroe에 저장합니다. 
    3. 이벤트가 성공적으로 저장되었다면 이벤트를 발행합니다.
    4. EventListener에서는 발행된 이벤트를 수신하여 읽기 테이블의 데이터를 변경합니다.

### DB 구조 분리 (읽기/쓰기)
- 한가지 DB만을 사용해도 CQRS 패턴은 구현할 수 있습니다.
- 읽기 전용 DB와 쓰기 전용 DB를 분리하여 사용합니다.
- 읽기 전용 DB는 Query(조회)를 처리하고 쓰기 전용 DB는 Command(명령)를 처리합니다.
- 읽기/쓰기 분리를 위해 application.yaml에서 write, read DB를 설정합니다.
  - DatabasConfig 클래스를 통해 yaml에 적은 write, read DB를 설정합니다.
  - DatabasConfig 클래스에서 @Primary 어노테이션을 사용하여 기본 DB를 설정합니다.
- DatabasConfig 클래스는 다음과 같이 설정합니다.
  - @Primary 어노테이션이 있으면 쓰기 전용 DB로 설정하고 없으면 읽기 전용 DB로 설정합니다.
  - 읽기/쓰기에 따라 entityManagerFactory를 빈 등록하면서 패키지를 분리합니다. 