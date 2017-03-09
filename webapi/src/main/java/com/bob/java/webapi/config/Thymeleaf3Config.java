package com.bob.java.webapi.config;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.*;
import org.springframework.boot.autoconfigure.web.ConditionalOnEnabledResourceChain;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.servlet.Servlet;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by bob on 17/3/9.
 */
@Configuration
@ConditionalOnClass({Thymeleaf.class, SpringTemplateEngine.class})
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class Thymeleaf3Config {

    private static final String DEFAULT_PREFIX = "classpath:/templates/";
    private static final String DEFAULT_SUFFIX = ".html";

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public SpringResourceTemplateResolver springHtmlResourceTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(this.applicationContext);
        resolver.setPrefix(DEFAULT_PREFIX);
        resolver.setSuffix(DEFAULT_SUFFIX);
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true);
        resolver.setOrder(1);
        return resolver;
    }

    @Bean
    public SpringResourceTemplateResolver springTxtResourceTemplateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(this.applicationContext);
        resolver.setPrefix("classpath:/templates/text/");
        resolver.setSuffix(".txt");
        resolver.setTemplateMode(TemplateMode.TEXT);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(true);
        resolver.setOrder(2);
        return resolver;
    }

    @Configuration
    @ConditionalOnJava(ConditionalOnJava.JavaVersion.EIGHT)
    @ConditionalOnClass({Java8TimeDialect.class})
    protected static class ThymeleafJava8TimeDialect {

        @Bean
        @ConditionalOnMissingBean
        public Java8TimeDialect java8TimeDialect() {
            return new Java8TimeDialect();
        }
    }

    @Configuration
    @ConditionalOnClass(name = {"nz.net.ultraq.thymeleaf.LayoutDialect"})
    protected static class ThymeleafWebLayoutConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public LayoutDialect layoutDialect() {
            return new LayoutDialect();
        }
    }

    @Configuration
    @ConditionalOnMissingBean(SpringTemplateEngine.class)
    protected static class SpringTemplateEngineConfiguration {

        @Autowired
        private final Collection<ITemplateResolver> templateResolvers = Collections.emptySet();

        @Autowired(required = false)
        private final Collection<IDialect> dialects = Collections.emptySet();

        @Bean
        public SpringTemplateEngine springTemplateEngine() {
            SpringTemplateEngine engine = new SpringTemplateEngine();
            engine.setEnableSpringELCompiler(true);
            for (ITemplateResolver templateResolver : this.templateResolvers) {
                engine.addTemplateResolver(templateResolver);
            }
            for (IDialect dialect : this.dialects) {
                engine.addDialect(dialect);
            }
            return engine;
        }
    }

    @Configuration
    @ConditionalOnClass({Servlet.class})
    @ConditionalOnWebApplication
    protected static class ThymeleafViewResolverConfiguration {

        @Autowired
        private SpringTemplateEngine engine;

        @Bean
        @ConditionalOnMissingBean(name = "thymeleafViewResolver")
        @ConditionalOnProperty(name = "spring.thymeleaf.enabled", matchIfMissing = true)
        public ThymeleafViewResolver thymeleafViewResolver() {
            ThymeleafViewResolver resolver = new ThymeleafViewResolver();
            resolver.setTemplateEngine(this.engine);
            resolver.setCharacterEncoding("UTF-8");
            resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 5);
            resolver.setCache(true);
            return resolver;
        }
    }

    @Configuration
    @ConditionalOnWebApplication
    protected static class ThymeleafResourceHandlingConfig {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnEnabledResourceChain
        public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
            return new ResourceUrlEncodingFilter();
        }
    }
}