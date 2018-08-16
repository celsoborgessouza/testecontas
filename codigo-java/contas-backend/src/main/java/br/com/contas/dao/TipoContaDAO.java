package br.com.contas.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.TipoConta;

@Repository
public class TipoContaDAO extends HibernateDAO<TipoConta>{

	protected TipoContaDAO() {
		super(TipoConta.class);
	}

	public Long recuperarPorNome(String nome) {
		
		Query q = getSession().createQuery("from tipo_conta where nome = ?");
		q.setString(0, nome);
		TipoConta tipoConta = (TipoConta) q.uniqueResult();
		return tipoConta.getId();
	}

}
