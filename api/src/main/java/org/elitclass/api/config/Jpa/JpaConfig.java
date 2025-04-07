package org.elitclass.api.config.Jpa;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = "org.elitclass.db")
@EnableJpaRepositories(basePackages = "org.elitclass.db")
public class JpaConfig {
}
