package br.com.contas.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.SituacaoConta;
import br.com.contas.domain.rdbs.TipoStatusTransacao;

@Repository
public class TipoStatusTransacaoDAO extends HibernateDAO<TipoStatusTransacao>{
	


	protected TipoStatusTransacaoDAO() {
		super(TipoStatusTransacao.class);
	}

	public Long recuperarIdPorNome(String nome) {
		
		Query q = getSession().createQuery("from tipo_status_transacao where nome = ?");
		q.setString(0, nome);
		SituacaoConta situacaoConta = (SituacaoConta) q.uniqueResult();
		return situacaoConta.getId();
		
	}

}
