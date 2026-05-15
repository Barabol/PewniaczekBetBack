package com.pewniaczekbet;

import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableJdbcHttpSession(maxInactiveIntervalInSeconds = 10080) // NOTE: tydzien narazie
public class JdbcSessionConfig {
}
