package at.spengergasse.ehif_dbi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DBI Catholic API")
                        .description("REST API for accessing 5EHIF DBI Project data")
                        .version("1.0")
                );
    }

    @Bean
    public OpenApiCustomizer renameControllerTags() {
        return openApi -> {
            if (openApi.getPaths() == null) return;

            openApi.getPaths().values().forEach(pathItem -> {
                pathItem.readOperations().forEach(op -> {
                    List<String> updatedTags = op.getTags().stream()
                            .map(tag -> {
                                if (tag.endsWith("-controller")) {
                                    tag = tag.replace("-rest", "");
                                    tag = tag.replace("-controller", "");
                                    return Arrays.stream(tag.split("-"))
                                            .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                                            .collect(Collectors.joining());
                                }

                                return tag;
                            })
                            .toList();
                    op.setTags(updatedTags);
                });
            });
        };
    }

}
