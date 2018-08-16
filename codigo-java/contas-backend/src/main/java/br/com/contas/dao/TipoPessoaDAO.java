package br.com.contas.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.TipoPessoa;

@Repository
public class TipoPessoaDAO extends HibernateDAO<TipoPessoa>{
	


	protected TipoPessoaDAO() {
		super(TipoPessoa.class);
	}

	public Long recuperarIdPorNome(String nome) {
		
		Query q = getSession().createQuery("from tipo_pessoa where nome = ?");
		q.setString(0, nome);
		TipoPessoa situacaoConta = (TipoPessoa) q.uniqueResult();
		return situacaoConta.getId();
		
	}

}
