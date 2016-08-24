package com.ancun.boot.encrypt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourcesLoader;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置文件加载完全后，解密加密字段
 *
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/24
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
public class EncryptEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    /** 解密后字段存放位置 */
    public static final String DECRYPTED_PROPERTY_SOURCE_NAME = "decrypted";

    /**
     * The default order for the processor.
     */
    public static final int DEFAULT_ORDER = Ordered.HIGHEST_PRECEDENCE + 15;

    private int order = DEFAULT_ORDER;

    private static Log logger = LogFactory.getLog(EncryptEnvironmentPostProcessor.class);

    private TextEncryptor encryptor = null;

    private boolean failOnError = true;

    private PropertySources propertySources;

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 如果启用加密
        if (environment.containsProperty("encrypt.enabled")) {
            this.propertySources = environment.getPropertySources();
            KeyProperties properties = createKey(environment, application);
            this.encryptor = EncryptorFactory.create(properties);
            initialize(environment);
        }
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    private KeyProperties createKey(ConfigurableEnvironment environment, SpringApplication application){
        KeyProperties properties = new KeyProperties();
        PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(
                properties);
        ConfigurationProperties annotation = AnnotationUtils
                .findAnnotation(properties.getClass(), ConfigurationProperties.class);
        if (annotation != null && annotation.locations().length != 0) {
            factory.setPropertySources(loadPropertySources(environment, application, annotation.locations(), annotation.merge()));
        }
        else {
            factory.setPropertySources(this.propertySources);
        }
        if (annotation != null) {
            factory.setIgnoreInvalidFields(annotation.ignoreInvalidFields());
            factory.setIgnoreUnknownFields(annotation.ignoreUnknownFields());
            factory.setExceptionIfInvalid(annotation.exceptionIfInvalid());
            factory.setIgnoreNestedProperties(annotation.ignoreNestedProperties());
            if (StringUtils.hasLength(annotation.prefix())) {
                factory.setTargetName(annotation.prefix());
            }
        }
        try {
            factory.bindPropertiesToTarget();
        }
        catch (Exception ex) {
            String targetClass = ClassUtils.getShortName(properties.getClass());
            throw new BeanCreationException(KeyProperties.class.getName(), "Could not bind properties to "
                    + targetClass + " (" + getAnnotationDetails(annotation) + ")", ex);
        }

        return properties;
    }

    private void initialize(ConfigurableEnvironment environment) {

        MutablePropertySources propertySources = environment.getPropertySources();

        Map<String, Object> map = decrypt(propertySources);

        propertySources.addFirst(new SystemEnvironmentPropertySource(DECRYPTED_PROPERTY_SOURCE_NAME, map));
    }

    private Map<String, Object> decrypt(PropertySources propertySources) {
        Map<String, Object> overrides = new LinkedHashMap<String, Object>();
        List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
        for (PropertySource<?> source : propertySources) {
            sources.add(0, source);
        }
        for (PropertySource<?> source : propertySources) {
            decrypt(source, overrides);
        }
        return overrides;
    }

    private Map<String, Object> decrypt(PropertySource<?> source) {
        Map<String, Object> overrides = new LinkedHashMap<String, Object>();
        decrypt(source, overrides);
        return overrides;
    }

    private void decrypt(PropertySource<?> source, Map<String, Object> overrides) {

        if (source instanceof EnumerablePropertySource) {

            EnumerablePropertySource<?> enumerable = (EnumerablePropertySource<?>) source;
            for (String key : enumerable.getPropertyNames()) {
                Object property = source.getProperty(key);
                if (property != null) {
                    String value = property.toString();
                    if (value.startsWith("{cipher}")) {
                        value = value.substring("{cipher}".length());
                        try {
                            value = this.encryptor.decrypt(value);
                            if (logger.isDebugEnabled()) {
                                logger.debug("Decrypted: key=" + key);
                            }
                        }
                        catch (Exception e) {
                            String message = "Cannot decrypt: key=" + key;
                            if (this.failOnError) {
                                throw new IllegalStateException(message, e);
                            }
                            if (logger.isDebugEnabled()) {
                                logger.warn(message, e);
                            }
                            else {
                                logger.warn(message);
                            }
                            // Set value to empty to avoid making a password out of the
                            // cipher text
                            value = "";
                        }
                        overrides.put(key, value);
                    }
                }
            }

        }
        else if (source instanceof CompositePropertySource) {

            for (PropertySource<?> nested : ((CompositePropertySource) source)
                    .getPropertySources()) {
                decrypt(nested, overrides);
            }

        }

    }

    private PropertySources loadPropertySources(
            ConfigurableEnvironment environment, SpringApplication application,
            String[] locations, boolean mergeDefaultSources) {
        try {
            PropertySourcesLoader loader = new PropertySourcesLoader();
            for (String location : locations) {
                Resource resource = application.getResourceLoader()
                        .getResource(environment.resolvePlaceholders(location));
                String[] profiles = environment.getActiveProfiles();
                for (int i = profiles.length; i-- > 0;) {
                    String profile = profiles[i];
                    loader.load(resource, profile);
                }
                loader.load(resource);
            }
            MutablePropertySources loaded = loader.getPropertySources();
            if (mergeDefaultSources) {
                for (PropertySource<?> propertySource : this.propertySources) {
                    loaded.addLast(propertySource);
                }
            }
            return loaded;
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private String getAnnotationDetails(ConfigurationProperties annotation) {
        if (annotation == null) {
            return "";
        }
        StringBuilder details = new StringBuilder();
        details.append("prefix=").append(annotation.prefix());
        details.append(", ignoreInvalidFields=").append(annotation.ignoreInvalidFields());
        details.append(", ignoreUnknownFields=").append(annotation.ignoreUnknownFields());
        details.append(", ignoreNestedProperties=")
                .append(annotation.ignoreNestedProperties());
        return details.toString();
    }

}
