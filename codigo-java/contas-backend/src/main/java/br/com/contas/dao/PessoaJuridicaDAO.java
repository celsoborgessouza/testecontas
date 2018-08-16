package br.com.contas.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.PessoaFisica;
import br.com.contas.domain.rdbs.PessoaJuridica;

@Repository
public class PessoaJuridicaDAO extends HibernateDAO<PessoaJuridica> {

	protected PessoaJuridicaDAO() {
		super(PessoaJuridica.class);
	}

	public PessoaFisica recuperarPorCnpj(String cnpj) {
		
		Query q = getSession().createQuery("from pessoa_juridica where cnpj = ?");
		q.setString(0, cnpj);
		return (PessoaFisica) q.uniqueResult();	
	}

	@SuppressWarnings("unchecked")
	public List<PessoaJuridica> recuperarTodos() {
		Query q = createQuery("from pessoa_juridica");
		return q.list();
	}

}
