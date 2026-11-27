package com.fsocial;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * Nạp file .env cho các test @SpringBootTest.
 * <p>
 * Khi chạy app, {@code PostServiceApplication.main()} đã đẩy .env vào System properties.
 * Test không đi qua main() nên {@code ${SPRING_PROFILES_ACTIVE}}, {@code ${POSTGRES_URL}}...
 * không được resolve và DataSource không cấu hình được. Biến môi trường thật vẫn được ưu tiên.
 */
public class DotenvTestEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> values = new HashMap<>();
        Dotenv.configure().ignoreIfMissing().load().entries().forEach(entry -> {
            if (System.getenv(entry.getKey()) == null) {
                values.put(entry.getKey(), entry.getValue());
            }
        });
        environment.getPropertySources().addLast(new MapPropertySource("dotenvTest", values));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
