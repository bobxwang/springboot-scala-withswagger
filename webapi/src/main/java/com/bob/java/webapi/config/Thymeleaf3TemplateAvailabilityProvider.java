package com.bob.java.webapi.config;

import org.springframework.boot.autoconfigure.template.TemplateAvailabilityProvider;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertyResolver;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;

/**
 * Created by bob on 17/3/9.
 */
public class Thymeleaf3TemplateAvailabilityProvider implements TemplateAvailabilityProvider {

    private static final String DEFAULT_PREFIX = "classpath:/templates/";
    private static final String DEFAULT_SUFFIX = ".html";

    @Override
    public boolean isTemplateAvailable(String view, Environment environment, ClassLoader classLoader,
                                       ResourceLoader resourceLoader) {

        if (ClassUtils.isPresent("org.thymeleaf.spring4.SpringTemplateEngine", classLoader)
                && ClassUtils.isPresent("org.thymeleaf.Thymeleaf", classLoader)) {
            PropertyResolver resolver = new RelaxedPropertyResolver(environment, "spring.thymeleaf3.");
            String prefix = resolver.getProperty("prefix", DEFAULT_PREFIX);
            String suffix = resolver.getProperty("suffix", DEFAULT_SUFFIX);
            return resourceLoader.getResource(prefix + view + suffix).exists();
        }
        return false;
    }
}