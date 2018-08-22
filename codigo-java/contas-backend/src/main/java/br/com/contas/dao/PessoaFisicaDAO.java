package br.com.contas.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import br.com.contas.domain.rdbs.PessoaFisica;

@Repository
public class PessoaFisicaDAO extends HibernateDAO<PessoaFisica> {

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

	public List<PessoaFisica> recuprar(String cpf, String nome) {
		
		StringBuilder query = new StringBuilder("from pessoa_fisica where 1=1 ");
		if (!StringUtils.isEmpty(cpf)) {
			query.append(" and cpf = :cpf ");
		}
		if (!StringUtils.isEmpty(nome)) {
			query.append(" and nomeCompleto like :nome ");
		}

		Query q = createQuery(query.toString());
		if (!StringUtils.isEmpty(cpf)) {
			q.setString("cpf", cpf);
		}
		if (!StringUtils.isEmpty(nome)) {
			q.setString("nome", "%" + nome + "%");
		}
		return q.list();
	}

}
