package com.example.cqrs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

/**
 * 재시도 정책을 설정하는 설정 클래스입니다.
 * 작업 실패 시 지수 백오프(Exponential Backoff) 방식으로 재시도를 수행합니다.
 */
@Configuration
@EnableRetry  // 재시도 기능 활성화
public class RetryConfig {

    /**
     * 재시도 템플릿을 구성하고 빈으로 등록합니다.
     *
     * @param maxAttempts     최대 재시도 횟수 (예: 3)
     * @param initialInterval 첫 재시도까지의 대기 시간 (밀리초 단위, 예: 1000)
     * @param multiplier      다음 재시도까지의 대기 시간 증가 배수 (예: 2.0)
     * @param maxInterval     최대 대기 시간 (밀리초 단위, 예: 10000)
     * @return 구성된 RetryTemplate
     */
    @Bean
    public RetryTemplate retryTemplate(
            @Value("${retry.maxAttempts}") int maxAttempts,
            @Value("${retry.initialInterval}") long initialInterval,
            @Value("${retry.multiplier}") double multiplier,
            @Value("${retry.maxInterval}") long maxInterval
    ) {
        // 재시도 간격을 지수적으로 증가시키는 정책 설정
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(initialInterval); // 첫 재시도 대기 시간
        backOffPolicy.setMultiplier(multiplier);           // 대기 시간 증가 배수
        backOffPolicy.setMaxInterval(maxInterval);         // 최대 대기 시간

        // 단순 재시도 정책 설정 (지정된 횟수만큼 재시도)
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(maxAttempts);

        // 재시도 템플릿 생성 및 정책 설정
        RetryTemplate template = new RetryTemplate();
        template.setBackOffPolicy(backOffPolicy);
        template.setRetryPolicy(retryPolicy);

        return template;
    }

}