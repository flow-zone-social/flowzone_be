package com.fsocial.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PageConfig {

    @Value("${app.PAGE_LIMIT}")
    public int pageLimit;
}
