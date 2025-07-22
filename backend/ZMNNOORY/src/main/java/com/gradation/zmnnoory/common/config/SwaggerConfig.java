package com.gradation.zmnnoory.common.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

@OpenAPIDefinition(
        info = @io.swagger.v3.oas.annotations.info.Info(title = "즈믄누리 API 명세서",
                description = "SSAFY 13th 공통프로젝트 ZMNNOORY",
                version = "v1")
)
@Configuration
public class SwaggerConfig {

    @Bean
    public OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            this.addResponseBodyWrapperSchemaExample(operation);
            return operation;
        };
    }

    private void addResponseBodyWrapperSchemaExample(Operation operation) {
        operation.getResponses().forEach((statusCode, apiResponse) -> {
            final Content content = apiResponse.getContent();
            if (content != null) {
                content.forEach((mediaTypeKey, mediaType) -> {
                    Schema<?> originalSchema = mediaType.getSchema();
                    Schema<?> wrappedSchema = wrapSchema(originalSchema);
                    mediaType.setSchema(wrappedSchema);
                });
            }
        });
    }

    private Schema<?> wrapSchema(Schema<?> originalSchema) {
        final Schema<?> wrapperSchema = new Schema<>();
        Schema<?> headerSchema = new Schema<>().type("object").example(new HashMap<>());
        if (originalSchema.getProperties() != null) {
            headerSchema = new Schema<>().type("object").example(originalSchema.getProperties().get("headers"));
        }
        wrapperSchema.addProperty("headers", headerSchema);
        wrapperSchema.addProperty("body", originalSchema);

        // statusCode와 statusCodeValue 처리
        Schema<?> statusSchema = null;
        if (originalSchema.getProperties() != null) {
            statusSchema = originalSchema.getProperties().get("status");
        }

        if (statusSchema != null) {
            wrapperSchema.addProperty("statusCode", statusSchema);
            wrapperSchema.addProperty("statusCodeValue",
                    new Schema<>()
                            .type("integer")
                            .example(HttpStatus.valueOf(String.valueOf(statusSchema.getExample())).value()));
        } else {
            wrapperSchema.addProperty("statusCode", new Schema<>().example("statusCode"));
            wrapperSchema.addProperty("statusCodeValue", new Schema<>().example("statusCodeValue"));
        }

        return wrapperSchema;
    }
}
