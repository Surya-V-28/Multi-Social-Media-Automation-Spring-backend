package com.example.learningwebflux;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.lang.NonNullApi;

public class FullyQualifiedBeanNameGenerator extends AnnotationBeanNameGenerator {
    @Override
    protected String buildDefaultBeanName(BeanDefinition beanDefinition) {
        return beanDefinition.getBeanClassName();
    }
}
