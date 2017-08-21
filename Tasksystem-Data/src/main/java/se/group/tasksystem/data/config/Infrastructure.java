package se.group.tasksystem.data.config;

import static se.group.tasksystem.data.config.DBInfo.databasePassword;
import static se.group.tasksystem.data.config.DBInfo.databaseUrl;
import static se.group.tasksystem.data.config.DBInfo.databaseUsername;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
@EnableJpaRepositories("se.group.tasksystem.data.repository")
@EnableJpaAuditing
@EnableTransactionManagement
@Profile("Production")
public class Infrastructure {

	@Bean(name = "mysql")
	public DataSource dataSource() {
		HikariConfig config = new HikariConfig();
		config.setDriverClassName("com.mysql.jdbc.Driver");
		config.setJdbcUrl(databaseUrl);
		config.setUsername(databaseUsername);
		config.setPassword(databasePassword);

		return new HikariDataSource(config);
	}

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {

		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.MYSQL);
		adapter.setShowSql(false);
		adapter.setGenerateDdl(true);
		return adapter;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(dataSource());
		factory.setJpaVendorAdapter(jpaVendorAdapter());
		factory.setPackagesToScan("se.group.tasksystem.data.model");
		return factory;
	}

	@Bean
	AuditorAware<String> auditorProvider() {
		return new CustomAuditor();
	}
}
