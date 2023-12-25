package artemzenkov.kursach_remont.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("artemzenkov.kursach_remont.domain")
@EnableJpaRepositories("artemzenkov.kursach_remont.repos")
@EnableTransactionManagement
public class DomainConfig {
}
