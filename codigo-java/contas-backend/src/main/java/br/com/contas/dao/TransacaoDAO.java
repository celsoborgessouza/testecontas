package br.com.contas.dao;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.Transacao;

@Repository
public class TransacaoDAO extends HibernateDAO<Transacao> {

	public TransacaoDAO() {
		super(Transacao.class);
	}

	@SuppressWarnings("unchecked")
	public List<Transacao> getTransacoes(Long idTransacao) {

		Query q = createQuery("from transacao where (id_origem = ? or id_destino = ?)");
		List<Transacao> objects = q.list();
		return objects;
	}
	
	

}
