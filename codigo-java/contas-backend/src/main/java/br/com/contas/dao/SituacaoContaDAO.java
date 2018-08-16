package br.com.contas.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.SituacaoConta;

@Repository
public class SituacaoContaDAO extends HibernateDAO<SituacaoConta>{
	
	public static final String ATIVA = "ATIVA";
	public static final String BLOQUEADA = "BLOQUEADA";
	public static final String CANCELADA = "CANCELADA";

	protected SituacaoContaDAO() {
		super(SituacaoConta.class);
	}

	public Long recuperarIdPorNome(String nome) {
		
		Query q = getSession().createQuery("from situacao_conta where nome = ?");
		q.setString(0, nome);
		SituacaoConta situacaoConta = (SituacaoConta) q.uniqueResult();
		return situacaoConta.getId();
		
	}

}
