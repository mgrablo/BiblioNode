package io.github.mgrablo.BiblioNode.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PaginationConfig implements WebMvcConfigurer {
	private final PaginationProperties paginationProperties;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();

		pageableResolver.setFallbackPageable(PageRequest.of(0, paginationProperties.defaultPageSize()));
		pageableResolver.setMaxPageSize(paginationProperties.maxPageSize());

		resolvers.add(pageableResolver);
	}

	@Bean
	public OperationCustomizer customizePagination() {
		return (operation, handlerMethod) -> {
			if (operation.getParameters() != null) {
				operation.getParameters().stream()
						.filter(p -> "size".equals(p.getName()))
						.forEach(p -> {
							if (p.getSchema() != null) {
								p.getSchema().setDefault(paginationProperties.defaultPageSize());
							}
						});
			}
			return operation;
		};
	}
}
