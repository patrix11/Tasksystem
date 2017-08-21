package se.group.tasksystem.data.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import se.group.tasksystem.data.config.CustomAuditor;

@Configuration
@EnableJpaRepositories("se.group.tasksystem.data.repository")
@EnableJpaAuditing
@EnableTransactionManagement
@Profile("Development")
public class TestingInfrastructure {

	@Bean(name = "hsqldb")
	public DataSource InMemoryDataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase database = builder.setType(EmbeddedDatabaseType.HSQL).setName("spring-tasksystem").build();

		return database;
	}

	@Bean
	public JpaTransactionManager transactionManager(EntityManagerFactory factory) {
		return new JpaTransactionManager(factory);
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {

		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.HSQL);
		adapter.setShowSql(false);
		adapter.setGenerateDdl(true);
		return adapter;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setDataSource(InMemoryDataSource());
		factory.setJpaVendorAdapter(jpaVendorAdapter());
		factory.setPackagesToScan("se.group.tasksystem.data.model");
		return factory;
	}

	@Bean
	AuditorAware<String> auditorProvider() {
		return new CustomAuditor();
	}

}
