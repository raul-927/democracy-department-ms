package com.democracy.department.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.ExpressionBasedPreInvocationAdvice;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreInvocationAuthorizationAdviceVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.acls.AclEntryVoter;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.context.annotation.AdviceMode;
//@Configuration
//@EnableTransactionManagement
//@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true, securedEnabled = true, mode= AdviceMode.PROXY)
public class AclMethodSecurityConfiguration /*extends GlobalMethodSecurityConfiguration*/{
	
	private static final String className 	="com.mysql.cj.jdbc.Driver";
	private static final String url 		= "jdbc:mysql://localhost/mundocostenio_db?createDatabaseIfNotExist=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static final String userName 	="root";
	private static final String passWord 	="";
	
	//@Autowired
    MethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler;

	/*
	@Override 
	protected AccessDecisionManager accessDecisionManager() {
		AffirmativeBased manager = customAccessDecisionManager(); 
		return manager; 
	}
	*/
	
	//@Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(new AclPermissionEvaluator(aclService()));
        expressionHandler.setPermissionCacheOptimizer(new AclPermissionCacheOptimizer(aclService()));
        return expressionHandler;
    }
	
	//@Bean
	public DriverManagerDataSource dataSource2() {
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
		driverManagerDataSource.setDriverClassName(className);
		driverManagerDataSource.setUrl(url);
		driverManagerDataSource.setUsername(userName);
		driverManagerDataSource.setPassword(passWord);
		return driverManagerDataSource;
	}
	
	//@Bean
	public MethodSecurityExpressionHandler 
	  defaultMethodSecurityExpressionHandler() {
	    DefaultMethodSecurityExpressionHandler expressionHandler
	      = new DefaultMethodSecurityExpressionHandler();
	    AclPermissionEvaluator permissionEvaluator 
	      = new AclPermissionEvaluator(aclService());
	    expressionHandler.setPermissionEvaluator(permissionEvaluator);
	    return expressionHandler;
	}
	
	//@Bean 
	public JdbcMutableAclService aclService() {
		JdbcMutableAclService jdbcMutableAclService = new JdbcMutableAclService(dataSource2(), lookupStrategy(), aclCache());
		jdbcMutableAclService.setClassIdentityQuery("SELECT @@IDENTITY");
		  jdbcMutableAclService.setSidIdentityQuery("SELECT @@IDENTITY");	
	    return jdbcMutableAclService;
	}

	//@Bean
	public AclAuthorizationStrategy aclAuthorizationStrategy() {
	    return new AclAuthorizationStrategyImpl(
	      new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"), 
	      new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"), 
	      new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"));
	}
	
	//@Bean
	public PermissionGrantingStrategy permissionGrantingStrategy() {
	    return new DefaultPermissionGrantingStrategy(
	      new ConsoleAuditLogger());
	}
	
	//@Bean
	public EhCacheBasedAclCache aclCache() {
	    return new EhCacheBasedAclCache(
	      aclEhCacheFactoryBean().getObject(), 
	      permissionGrantingStrategy(), 
	      aclAuthorizationStrategy()
	    );
	}
	
	//@Bean
	public EhCacheFactoryBean aclEhCacheFactoryBean() {
	    EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
	    ehCacheFactoryBean.setCacheManager(aclCacheManager().getObject());
	    ehCacheFactoryBean.setCacheName("aclCache");
	    return ehCacheFactoryBean;
	}
	 
	//@Bean
	public EhCacheManagerFactoryBean aclCacheManager() {
	    return new EhCacheManagerFactoryBean();
	}
	
	//@Bean 
	public LookupStrategy lookupStrategy() { 
	    return new BasicLookupStrategy(
	      dataSource2(), 
	      aclCache(), 
	      aclAuthorizationStrategy(), 
	      new ConsoleAuditLogger()
	    ); 
	}
	
	//@Bean
	public AclEntryVoter aclDeletePostVoter() {
		Permission[] requirePermission = {BasePermission.DELETE};
		AclEntryVoter aclEntryVoter = new AclEntryVoter(aclService(),"ACL_POST_DELETE",requirePermission);
		aclEntryVoter.setProcessDomainObjectClass(Object.class);
		return aclEntryVoter;
	}
	
	//@Bean
	public AclEntryVoter aclUpdatePostVoter() {
		Permission[] requirePermission = {BasePermission.WRITE};
		AclEntryVoter aclEntryVoter = new AclEntryVoter(aclService(),"ACL_POST_UPDATE",requirePermission);
		aclEntryVoter.setProcessDomainObjectClass(Object.class);
		return aclEntryVoter;
	}
	/*
	@Bean
	public AclEntryVoter aclReadPostVoter() {
		Permission[] requirePermission = {BasePermission.READ};
		AclEntryVoter aclEntryVoter = new AclEntryVoter(aclService(),"ACL_POST_READ",requirePermission);
		aclEntryVoter.setProcessDomainObjectClass(Post.class);
		return aclEntryVoter;
	}
	*/
	//@Bean
	public AffirmativeBased customAccessDecisionManager() {
		List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<AccessDecisionVoter<?>>();
		decisionVoters.add(aclDeletePostVoter());
		decisionVoters.add(aclUpdatePostVoter());
		//decisionVoters.add(aclReadPostVoter());
		decisionVoters.add(preInvocationAuthorizationAdviceVoter());
		AffirmativeBased affirmativeBased = new AffirmativeBased(decisionVoters);
		return affirmativeBased;
	}
	
	//@Bean
	public PreInvocationAuthorizationAdviceVoter preInvocationAuthorizationAdviceVoter() {
		PreInvocationAuthorizationAdviceVoter preInvocationAuthorizationAdviceVoter = new PreInvocationAuthorizationAdviceVoter(new ExpressionBasedPreInvocationAdvice());
		return preInvocationAuthorizationAdviceVoter;
	}
	
	//@Bean
	public AclPermissionEvaluator customPermissionEvaluator() {
		return new AclPermissionEvaluator(aclService());
	}
	
	//@Bean
	public MethodSecurityExpressionHandler customExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setPermissionEvaluator(customPermissionEvaluator());
		return handler;
	}
	
	//@Bean
	public DataSourceTransactionManager transactionManager() {
		return new DataSourceTransactionManager(dataSource2());
	}
}
