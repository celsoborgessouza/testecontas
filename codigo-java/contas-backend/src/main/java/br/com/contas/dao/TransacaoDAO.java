package br.com.contas.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.Transacao;

@Repository
public class TransacaoDAO extends HibernateDAO<Transacao> {

	private static final String QUERY_RECUPERAR_ESTORNO_POR_CODIGO = "select {t.*} from transacao inner join aporte a on t.id_aporte = a.id and a.codigo = :codigo";
	
	
	private static final String QUERY_COUNT_ESTORNO_APORTE_REALIZADO = "select count(*) from transacao t "
			+ "inner join aporte a on t.id_aporte = a.id "
			+ "and t.id_status = :idTipoStatusSucesso "
			+ "and t.id_tipo_acao = :idAcaoEstrono "
			+ "and a.codigo = :codigo";

	public TransacaoDAO() {
		super(Transacao.class);
	}

	@SuppressWarnings("unchecked")
	public List<Transacao> recuperarHistoricoTransacoesPelaConta(Long idConta) {

		Query q = createQuery("from transacao where (id_conta_origem = ? or id_conta_destino = ?)");
		List<Transacao> objects = q.list();
		return objects;
	}

	public Transacao recuperarPorCodigoAporte(String codigoAprote) {
		
		SQLQuery q = createSqlQuery(QUERY_RECUPERAR_ESTORNO_POR_CODIGO);
		q.addEntity(Transacao.class);
		q.setParameter("codigo", codigoAprote);
		return (Transacao) q.uniqueResult();
	}

	public boolean isEstornoJaRealizado(String codigoAprote, Long idAcaoEstorno, Long idTipoStatusSucesso) {
		

		SQLQuery q = createSqlQuery(QUERY_COUNT_ESTORNO_APORTE_REALIZADO);
		q.setParameter("codigo", codigoAprote);
		q.setParameter("idAcaoEstrono", idAcaoEstorno);
		q.setParameter("id_tipo_situacao", idTipoStatusSucesso);
		Integer count = (Integer) q.uniqueResult();
		
		return count == 0 ? true : false;
	}
	
	

}
