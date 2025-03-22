package com.example.cqrs.infrastructure.config.openapi

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * OpenAPI 문서화 설정
 * Swagger UI와 API 문서 생성을 위한 설정 클래스
 */
@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val securitySchemeName = "bearerAuth"

        return OpenAPI()
            .info(apiInfo())
            .addServersItem(Server().url("/"))
            .addSecurityItem(SecurityRequirement().addList(securitySchemeName))
            .components(
                Components()
                    .addSecuritySchemes(
                        securitySchemeName,
                        SecurityScheme()
                            .name(securitySchemeName)
                            .type(SecurityScheme.Type.HTTP)
//                            .scheme("bearer")
//                            .bearerFormat("JWT")
                    )
            )
    }

    private fun apiInfo() = Info()
        .title("CQRS 금융 시스템 API")
        .description("이벤트 소싱과 CQRS 패턴을 사용한 금융 계좌 관리 및 상품 관리 API")
        .version("1.0.0")
        .contact(
            Contact()
                .name("stark")
                .email("dig04059@gmail.com")
                .url("none")
        )
        .license(
            License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html")
        )

}