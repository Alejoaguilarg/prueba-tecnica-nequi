package co.com.bancolombia.config;

import co.com.bancolombia.api.error.GlobalErrorAttributes;
import co.com.bancolombia.api.error.GlobalErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;

@Configuration
public class ExceptionHandlingConfig {

    @Bean
    public GlobalErrorWebExceptionHandler globalErrorHandler(GlobalErrorAttributes attributes,
                                                             ApplicationContext context,
                                                             ServerCodecConfigurer configurer) {
        return new GlobalErrorWebExceptionHandler(attributes, context, configurer);
    }
}

