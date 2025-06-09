package com.mifincaapp.product;


import com.mifincaapp.product.config.DotenvTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(DotenvTestConfig.class)
class BackendProductApplicationTests {

    @Test
    void contextLoads() {
    }
}


