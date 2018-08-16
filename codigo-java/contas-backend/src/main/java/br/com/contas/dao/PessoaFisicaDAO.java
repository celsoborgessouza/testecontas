package br.com.contas.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.PessoaFisica;

@Repository
public class PessoaFisicaDAO extends HibernateDAO<PessoaFisica>{

	protected PessoaFisicaDAO() {
		super(PessoaFisica.class);
	}

	public PessoaFisica recuperarPorCpf(String cpf) {
		
		Query q = getSession().createQuery("from pessoa_fisica where cpf = ?");
		q.setString(0, cpf);
		return (PessoaFisica) q.uniqueResult();	
	}

	@SuppressWarnings("unchecked")
	public List<PessoaFisica> recuperarTodos() {
		Query q = createQuery("from pessoa_fisica");
		return q.list();
	}

}
