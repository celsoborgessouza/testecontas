package br.com.contas.dao;

import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;


public abstract class HibernateDAO<T> implements IHibernateDAO<T> {
	
	protected Class<T> clazz;
	
	/*
	 * represenata uma conexão com o banco de dados, e com ele poderá ser persistido no
	 * banco de dados. Mas para isso é necessário a API do Hibernate	
	 */
	protected Session session;
	
	/*
	 * Fábrica de sessions. Precisa ser inicializado apenas uma vez pela leitura do arquivo hibernate.cfg.xml.
	 * Depois de inicializado, a factory é utilizada para obeter a sessão do Hibernate
	 */
	@Autowired
	protected SessionFactory sessionFactory;
	
	public HibernateDAO() {
		
	}
	
	protected HibernateDAO(Class<T> clazz) {
		super();
		this.clazz = clazz;
	}


	/* (non-Javadoc)
	 * @see br.com.contas.dao.IHibernateDAO#delete(T)
	 */
	@Override
	public void delete(T entity) {
		getSession().delete(entity);
	}
	
	/* (non-Javadoc)
	 * @see br.com.contas.dao.IHibernateDAO#update(T)
	 */
	@Override
	public void update(T entity){
		getSession().update(entity);
	}
	
	public void merge(T entity) {
		getSession().merge(entity);
	}
	
	/* (non-Javadoc)
	 * @see br.com.contas.dao.IHibernateDAO#save(T)
	 */
	@Override
	public Serializable save(T entity) {
		return getSession().save(entity);
	}
	
	/* (non-Javadoc)
	 * @see br.com.contas.dao.IHibernateDAO#saveOrUpdate(T)
	 */
	@Override
	public void saveOrUpdate(T entity) {
		getSession().saveOrUpdate(entity);
	}
	
	protected Query createQuery(String query) {
		return getSession().createQuery(query);
	}
	
	protected Criteria createCriteria() {
		return getSession().createCriteria(this.clazz);
	}
	
	protected SQLQuery createSqlQuery(String query) {
		return getSession().createSQLQuery(query);
	}
	
	/* (non-Javadoc)
	 * @see br.com.contas.dao.IHibernateDAO#load(java.io.Serializable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T load(Serializable id) {
		return (T) getSession().load(this.clazz, id);
	}
	
	/* (non-Javadoc)
	 * @see br.com.contas.dao.IHibernateDAO#get(java.io.Serializable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T get(Serializable id) {
		return (T) getSession().get(this.clazz, id);
	}

	public Session getSession() {
		if (sessionFactory != null) {
			session = sessionFactory.getCurrentSession();
		}
		
		if (session == null) {
			throw new RuntimeException("Hibernate session is null");
		}
		
		return session;
	}
	
	
}
