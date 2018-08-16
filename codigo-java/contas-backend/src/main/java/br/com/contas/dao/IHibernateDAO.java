package br.com.contas.dao;

import java.io.Serializable;

public interface IHibernateDAO<T> {

	void delete(T entity);

	void update(T entity);

	Serializable save(T entity);

	void saveOrUpdate(T entity);

	T load(Serializable id);

	T get(Serializable id);

}