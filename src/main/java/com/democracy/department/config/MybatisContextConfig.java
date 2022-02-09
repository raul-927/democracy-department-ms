package com.democracy.department.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;

@Configuration
@EnableTransactionManagement
@MapperScan(value="com.democracy.department.mybatis.mappers")
public class MybatisContextConfig{
	
	@Value("${spring.liquibase.driverClassName}")
	private String className;
	
	@Value("${spring.liquibase.url}")
	private String url;
	
	@Value("${spring.r2dbc.username}")
	private String userName;
	
	@Value("${spring.r2dbc.password}")
	private String passWord;
	
	@Value("${spring.mybatis.mapperLocations}")
	private String relativePath;
	
	@Value("${spring.mybatis.typeAliasesPackage}")
	private String typeAliasesPackage;
	
	@Value("${spring.mybatis.typeHandlersPackage}")
	private String typeHandlersPackage;
	
	@Bean
	public DriverManagerDataSource dataSource() throws IllegalArgumentException, NamingException{
		/*
		 * JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		 * bean.setJndiName(className);
		 * bean.setProxyInterface(DriverManagerDataSource.class);
		 * bean.setLookupOnStartup(false); bean.afterPropertiesSet();
		 */
        
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName(this.className);
		driverManagerDataSource.setUrl(this.url);
		driverManagerDataSource.setUsername(this.userName);
		driverManagerDataSource.setPassword(this.passWord);
		return driverManagerDataSource;
		
	}
	
	@Bean
	public SqlSessionFactory sqlSessionFactory() throws Exception {
		
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	    Resource[] mapperLocations = resolver.getResources(this.relativePath);
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(this.dataSource());
		sqlSessionFactory.setTypeAliasesPackage(this.typeAliasesPackage);
		sqlSessionFactory.setTypeHandlersPackage(this.typeHandlersPackage);
		sqlSessionFactory.setMapperLocations(mapperLocations);
		//sqlSessionFactory.setDatabaseIdProvider(databaseProvider());
		
		return (SqlSessionFactory) sqlSessionFactory.getObject();
	}
	
	@Bean
	public SqlSessionTemplate sqlSession() throws Exception {
		SqlSessionTemplate sqlSessionTemplate= new SqlSessionTemplate(sqlSessionFactory());
		return sqlSessionTemplate;
	}
	
	//@Bean
	/*
	 * public DatabaseIdProvider databaseProvider() { Properties properties = new
	 * Properties(); properties.setProperty("sqlserver", "sqlserver");
	 * properties.setProperty("DB2", "db2"); properties.setProperty("Oracle",
	 * "oracle"); properties.setProperty("MySQL", "mysql");
	 * database.setProperties(properties); return database; }
	 */
}
