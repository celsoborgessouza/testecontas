package br.com.contas.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.Pessoa;

@Repository
public class PessoaDAO extends HibernateDAO<Pessoa> {

	public PessoaDAO() {
		super(Pessoa.class);
	}

	@SuppressWarnings("unchecked")
	public List<Pessoa> getPessoas() {

		Query q = createQuery("from Pessoa");
		List<Pessoa> objects = q.list();
		return objects;
	}
	
	

}
