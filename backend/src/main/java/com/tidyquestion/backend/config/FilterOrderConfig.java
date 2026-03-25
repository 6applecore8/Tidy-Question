package com.tidyquestion.backend.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 过滤器顺序配置：
 * traceId -> access log -> security(jwt) -> controller。
 */
@Configuration
public class FilterOrderConfig {

    @Bean
    public TraceIdFilter traceIdFilter() {
        return new TraceIdFilter();
    }

    @Bean
    public AccessLogFilter accessLogFilter() {
        return new AccessLogFilter();
    }

    @Bean
    public FilterRegistrationBean<TraceIdFilter> traceIdFilterRegistration(TraceIdFilter traceIdFilter) {
        FilterRegistrationBean<TraceIdFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(traceIdFilter);
        bean.setOrder(1);
        return bean;
    }

    @Bean
    public FilterRegistrationBean<AccessLogFilter> accessLogFilterRegistration(AccessLogFilter accessLogFilter) {
        FilterRegistrationBean<AccessLogFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(accessLogFilter);
        bean.setOrder(2);
        return bean;
    }
}
