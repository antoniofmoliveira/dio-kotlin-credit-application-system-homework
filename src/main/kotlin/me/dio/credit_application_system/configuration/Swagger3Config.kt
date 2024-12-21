package me.dio.credit_application_system.configuration

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class Swagger3Config {

    @Bean
    fun publicApi(): GroupedOpenApi? {
        return GroupedOpenApi.builder()
            .group("spingcredtiapplicationsystem-public")
            .pathsToMatch("/api/customers/**", "/api/credits/**")
            .build()
    }

    @Bean
    fun customOpenApi(): OpenAPI? {
        return OpenAPI()
            .components(Components())
            .info(Info()
                .title("Credit Application System")
                .contact(Contact().name("Antonio F M Oliveira")
                    .email("antoniofmoliveira@gmail.com")
                    .url("github.com/antoniofmoliveira"))
                .version("1.0.0"))
    }
}