# 금융 계좌 관리 시스템 (CQRS 아키텍처)

이 프로젝트는 이벤트 소싱(Event Sourcing)과 CQRS(Command Query Responsibility Segregation) 패턴을 기반으로 한 금융 계좌 관리 시스템입니다. 이 시스템은 계좌 관리, 금융 상품 관리 기능을 제공하며 트랜잭션 무결성, 확장성, 이벤트 추적성에 중점을 두고 설계되었습니다.

## 시스템 흐름도

### 기본 CQRS 흐름
```
[클라이언트] 
    │
    ├── [Command API] ──> [Command 서비스] ──> [PostgreSQL]
    │                        │
    │                        ▼
    │                    [이벤트 저장소] ──> [이벤트 핸들러] ──> [MongoDB]
    │
    └── [Query API] ───> [Query 서비스] ───> [MongoDB]
```

### 계좌 생성 흐름
```
1. 클라이언트 → AccountCommandController: 계좌 생성 요청
2. AccountCommandController → AccountCommandService: 계좌 생성 요청 전달
3. AccountCommandService: 
   - 계좌 중복 검사
   - 계좌 엔티티 생성 (AccountEntity)
   - 계좌 저장 (PostgreSQL)
   - AccountCreatedEventEntity 생성
   - 이벤트 저장 (EventStoreRepository)
   - 이벤트 발행 (ApplicationEventPublisher)
4. AccountEventListener: 
   - 이벤트 수신
   - MongoDB 읽기 모델 업데이트 (AccountDocument)
```

### 입금/출금/이체 흐름
```
1. 클라이언트 → CommandController: 입금/출금/이체 요청
2. CommandService:
   - 계좌 조회 및 유효성 검증
   - 계좌 잔액 업데이트 (PostgreSQL)
   - 해당 이벤트 엔티티 생성 (MoneyDepositedEventEntity 등)
   - 이벤트 저장 및 발행
3. EventListener:
   - 이벤트 수신
   - MongoDB 읽기 모델 업데이트
   - RetryTemplate을 통한 재시도 메커니즘 적용
```

## 아키텍처 개요

시스템은 명령(쓰기)과 쿼리(읽기) 작업 사이의 명확한 분리로 구현되었습니다:

- **명령 측(Command Side)**: 상태 변경 처리 (PostgreSQL)
- **쿼리 측(Query Side)**: 데이터 조회 최적화 (MongoDB)
- **이벤트 저장소(Event Store)**: 모든 상태 변경을 이벤트로 기록 (PostgreSQL)

### 주요 아키텍처 패턴

- **CQRS**: 읽기 및 쓰기 작업을 분리하여 각각 독립적으로 최적화
- **이벤트 소싱**: 애플리케이션 상태 변경을 이벤트 시퀀스로 기록
- **도메인 주도 설계(DDD)**: 비즈니스 로직을 도메인과 하위 도메인 중심으로 구성

## 주요 기능

### 계좌 관리
- 새 계좌 생성
- 입금
- 출금
- 계좌 간 이체
- 계좌 거래 내역 조회
- 계좌 잔액 조회

### 금융 상품 관리
- 금융 상품 생성 (예금, 적금, 대출 등)
- 상품 정보 업데이트
- 상품 비활성화
- 다양한 기준으로 상품 검색 및 필터링

### 시스템 기능
- 트랜잭션 재생 기능
- 실패한 트랜잭션 복구
- 읽기 및 쓰기 모델 간 데이터 동기화
- 동시성 처리
- 성능 최적화를 위한 스냅샷 기능

## 기술 스택

- **언어**: Kotlin (Java 21 호환)
- **프레임워크**: Spring Boot 3.4.4
- **쓰기 데이터베이스**: PostgreSQL
- **읽기 데이터베이스**: MongoDB
- **API 문서화**: Swagger/OpenAPI
- **재시도 메커니즘**: Spring Retry
- **트랜잭션**: 이벤트 소싱을 통한 분산 트랜잭션 관리
- **빌드 도구**: Gradle (Kotlin DSL)

## 핵심 컴포넌트

### 애플리케이션 계층
- **AccountCommandService**: 계좌 생성, 입금, 출금, 이체 등 명령 처리
- **AccountQueryService**: 계좌 정보 조회
- **ProductCommandService**: 금융 상품 생성, 수정, 비활성화 처리
- **ProductQueryService**: 금융 상품 조회 및 검색
- **EventReplayService**: 이벤트 재생 기능
- **DataSynchronizationService**: 읽기/쓰기 모델 간 데이터 동기화

### 인프라 계층
- **DatabaseConfig**: 읽기/쓰기 데이터소스 구성
- **RetryConfig**: 이벤트 처리 실패 시 재시도 정책 구성
- **AccountEventRepository**: 계좌 이벤트 저장 및 조회
- **EventStoreRepository**: 모든 이벤트 타입에 대한 공통 저장소

### 인터페이스 계층
- **AccountCommandController**: 계좌 명령 API 엔드포인트
- **AccountQueryController**: 계좌 조회 API 엔드포인트
- **ProductCommandController**: 상품 명령 API 엔드포인트
- **ProductQueryController**: 상품 조회 API 엔드포인트

## 이벤트 타입

### 계좌 이벤트
- **AccountCreatedEventEntity**: 계좌 생성
- **MoneyDepositedEventEntity**: 입금
- **MoneyWithdrawnEventEntity**: 출금
- **MoneyTransferredInEventEntity**: 이체 입금
- **MoneyTransferredOutEventEntity**: 이체 출금
- **BalanceChangeFailedEventEntity**: 잔액 변경 실패

### 상품 이벤트
- **ProductCreatedEventEntity**: 상품 생성
- **ProductUpdatedEventEntity**: 상품 수정
- **ProductDeactivatedEventEntity**: 상품 비활성화

## 데이터베이스 설정

시스템은 두 개의 별도 데이터베이스를 사용합니다:

1. **PostgreSQL**:
  - 명령 모델(현재 상태) 저장
  - 모든 상태 변경에 대한 이벤트 저장소
  - 트랜잭션 관리

2. **MongoDB**:
  - 최적화된 읽기 모델 저장
  - 효율적인 쿼리를 위한 비정규화된 데이터
  - 쓰기 성능에 영향을 주지 않는 빠른 읽기 접근 제공

## 애플리케이션 실행 방법

1. PostgreSQL과 MongoDB가 실행 중인지 확인
2. `application.yaml`에서 데이터베이스 연결 설정 구성
3. 애플리케이션 빌드: `./gradlew build`
4. 애플리케이션 실행: `./gradlew bootRun`

애플리케이션은 http://localhost:8100 에서 사용 가능합니다.
API 문서는 http://localhost:8100/swagger-ui.html 에서 접근할 수 있습니다.

## 시스템 설계 핵심 요소

### 이벤트 소싱
모든 상태 변경은 이벤트로 캡처되어 다음과 같은 이점을 제공합니다:
- 완전한 감사 추적
- 특정 시점의 시스템 상태 재구성 가능
- 데이터 복구 및 테스트를 위한 재생 기능
- 이벤트 기반 아키텍처 통합

### CQRS 이점
- 읽기 및 쓰기 작업의 독립적 확장
- 각 작업 유형에 최적화된 데이터 모델
- 높은 부하에서 성능 향상
- 최종 일관성을 통한 회복력

### 내결함성
- 이벤트 재생을 통한 실패 작업 복구
- 일시적 장애에 대한 자동 재시도 메커니즘
- 포괄적인 오류 로깅 및 추적

## 스냅샷 메커니즘

시스템은 성능 최적화를 위해 스냅샷 메커니즘을 구현합니다:
- 이벤트 수가 임계값(100개)을 초과할 때 계좌 상태의 스냅샷 생성
- 상태 재구성 시 모든 이벤트를 처음부터 재생하지 않고 최신 스냅샷부터 재생
- 시스템 부하 감소 및 응답 시간 향상

## 동시성 처리

시스템은 낙관적 잠금(Optimistic Locking)을 사용하여 동시성 문제를 처리합니다:
- 이벤트 엔티티의 @Version 필드
- ObjectOptimisticLockingFailureException 처리 로직
- ConcurrencyException 정의 및 발생

## 프로젝트 구조

프로젝트는 관심사 분리가 명확한 클린 아키텍처 접근 방식을 따릅니다:

- `application`: 애플리케이션 서비스 및 유스 케이스
- `infrastructure`: 데이터베이스, 구성 및 기술적 구현
- `interfaces`: API 컨트롤러 및 DTO
- `common`: 공유 유틸리티 및 예외