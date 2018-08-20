package br.com.contas.dao;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.TipoAcaoTransacao;

@Repository
public class TipoAcaoTransacaoDAO extends HibernateDAO<TipoAcaoTransacao>{
	

	protected TipoAcaoTransacaoDAO() {
		super(TipoAcaoTransacao.class);
	}

	public Long recuperarIdPorNome(String nome) {
		
		Query q = getSession().createQuery("from tipo_acao_transacao where nome = ?");
		q.setString(0, nome);
		TipoAcaoTransacao tipoAcaoTransacao = (TipoAcaoTransacao) q.uniqueResult();
		return tipoAcaoTransacao.getId();
		
	}

}
