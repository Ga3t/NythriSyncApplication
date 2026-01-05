package com.caliq.api_gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KeyRequester {
    private static final Logger logger = LoggerFactory.getLogger(KeyRequester.class);

}
