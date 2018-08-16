package br.com.contas.utils.test;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.AbstractXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate3.SessionFactoryUtils;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@SuppressWarnings("deprecation")
public class SpringUtil {
	
	private static SpringUtil instance;
	protected AbstractXmlApplicationContext ctx;
	
	// Hibernate
	protected HibernateTransactionManager txManager;
	private Session session;
	
	private SpringUtil() {		
		try {
			ctx = new ClassPathXmlApplicationContext("testContext.xml");
		} catch (BeansException e) {
			e.printStackTrace();
			throw e;	
		}	
	}
	
	public static SpringUtil getInstance() {
		if (instance == null) {
			instance = new SpringUtil();
		}
		return instance;
	}
	
	@SuppressWarnings("rawtypes")
	public Object getBean(Class c) {
		if (ctx == null) {
			return null;
		}
		
		open();
		String[] beanNamesForType = ctx.getBeanNamesForType(c);
		if(beanNamesForType == null || beanNamesForType.length == 0) {
			return null;
		}
		
		String name = beanNamesForType[0];
		Object bean = getBean(name);
		return bean;
	}

	private Object getBean(String name) {
		if (ctx == null) {
			return null;
		}
		
		open();
		Object bean = ctx.getBean(name);
		return bean;
	}

	private void open() {
		if (session == null) {
			openSession();
		}
	}

	/**
	 * Deixa a Session viva nesta thread. Mesma coisa de uma
	 * requisição web utilizando o filtro "OpenSessionInViewFilter"
	 * @return
	 */
	private Session openSession() {
		
		if (ctx != null) {
			txManager = (HibernateTransactionManager) ctx.getBean("transactionManager");
			SessionFactory sessionFactory = txManager.getSessionFactory();
			session = sessionFactory.openSession();
			TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
		}
		return session;
	}
	
	/**
	 * Remove session da thread
	 */
	public void closeSession(){
		
		if (ctx != null && txManager != null) {
			SessionFactory sessionFactory = txManager.getSessionFactory();
			TransactionSynchronizationManager.unbindResource(sessionFactory);
			SessionFactoryUtils.closeSession(session);
			session = null;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		
	}
	
	/*
	private Session getSession() {
		return session;
	}

	private void setSession(Session session) {
		this.session = session;
	}
	*/
	
	
}
