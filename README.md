### CQRS 예시 프로젝트입니다.
- 정말 단순히 제가 공부하고 싶어서 만든 프로젝트입니다.
- AI (Claude3.5 Sonnet)의 도움을 받아 완성하였습니다.
- Command Query Responsibility Segregation (CQRS) 패턴을 사용하였습니다.

### Command: 명령
- Command는 데이터를 변경하는 작업을 담당합니다.
- Command는 데이터를 변경하고, 변경된 데이터를 Event로 발행합니다.
- EventListener는 Event를 구독하고, Event가 발생하면 데이터를 변경합니다.

### Query: 조회
- Query는 데이터를 조회하는 작업을 담당합니다.

### DB 구조 분리 (읽기/쓰기)
- 한가지 DB만 사용해도 CQRS 패턴을 사용할 수 있습니다.
- DB 구조를 분리하여, 읽기 전용 DB와 쓰기 전용 DB를 사용합니다.
- 쓰기 전용 DB는 Command를 처리하고, 읽기 전용 DB는 Query를 처리합니다.