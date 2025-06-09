package com.mifincaapp.product.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiKeyProperties {

    @Value("${MIFINCA_API_HEADER_NAME}")
    private String headerName;

    @Value("${MIFINCA_API_HEADER_VALUE}")
    private String headerValue;

    public String getHeaderName() {
        return headerName;
    }

    public String getHeaderValue() {
        return headerValue;
    }
}
