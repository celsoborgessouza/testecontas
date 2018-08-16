package br.com.contas.dao;

import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.Conta;

@Repository
public class ContaDAO extends HibernateDAO<Conta> {

	protected ContaDAO() {
		super(Conta.class);
	}

}
