package br.com.contas.dao;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.Transacao;

@Repository
public class TransacaoDAO extends HibernateDAO<Transacao> {

	private static final String QUERY_RECUPERAR_ESTORNO_APORTE_POR_CODIGO = "select t.* from transacao t inner join aporte a on t.id_aporte = a.id and a.codigo = :codigo";
	
	
	private static final String QUERY_COUNT_ESTORNO_APORTE_REALIZADO = "select count(*) from transacao t "
			+ "inner join aporte a on t.id_aporte = a.id "
			+ "and t.id_tipo_status_transacao = :idTipoStatusSucesso "
			+ "and t.id_tipo_acao_transacao = :idAcaoEstrono "
			+ "and a.codigo = :codigo";

	private static final String QUERY_ESTORNO_APORTE_REALIZADO = "select t.* from transacao t "
			+ "inner join aporte a on t.id_aporte = a.id "
			+ "and t.id_tipo_status_transacao = :idTipoStatusSucesso "
			+ "and t.id_tipo_acao_transacao = :idAcaoEstrono "
			+ "and a.codigo = :codigo";


	private static final String QUERY_COUNT_ESTORNO_TRANACAO_REALIZADO = "select count(*) from transacao t "
			+ "where t.id_tipo_status_transacao = :idTipoStatusSucesso "
			+ "and t.id_tipo_acao_transacao = :idAcao "
			+ "and t.id_transferencia_estornado = :id";

	public TransacaoDAO() {
		super(Transacao.class);
	}

	@SuppressWarnings("unchecked")
	public List<Transacao> recuperarHistoricoTransacoesPelaConta(Long idConta) {

		SQLQuery q = createSqlQuery("select * from transacao where (id_conta_origem = :idContaOrigem or id_conta_destino = :idContaDestino)");
		q.setParameter("idContaOrigem", idConta);
		q.setParameter("idContaDestino", idConta);
		List<Transacao> objects = q.list();
		return objects;
	}


	public Transacao recuperarPorCodigoAporte(String codigoAprote) {
		
		SQLQuery q = createSqlQuery(QUERY_RECUPERAR_ESTORNO_APORTE_POR_CODIGO);
		q.addEntity(Transacao.class);
		q.setParameter("codigo", codigoAprote);
		return (Transacao) q.uniqueResult();
	}

	public boolean isEstornoAporteJaRealizado(String codigoAprote, Long idAcaoEstorno, Long idTipoStatusSucesso) {
		

		SQLQuery q = createSqlQuery(QUERY_COUNT_ESTORNO_APORTE_REALIZADO);
		q.setParameter("codigo", codigoAprote);
		q.setParameter("idAcaoEstrono", idAcaoEstorno);
		q.setParameter("idTipoStatusSucesso", idTipoStatusSucesso);
		BigInteger count = (BigInteger) q.uniqueResult();
		
		return BigInteger.ZERO.equals(count) ? false : true;
	}
	
	
	public Transacao recuperarEstornoJaRealizado(String codigoAprote, Long idAcaoEstorno, Long idTipoStatusSucesso) {
		
		SQLQuery q = createSqlQuery(QUERY_ESTORNO_APORTE_REALIZADO);
		q.addEntity(Transacao.class);
		q.setParameter("codigo", codigoAprote);
		q.setParameter("idAcaoEstrono", idAcaoEstorno);
		q.setParameter("idTipoStatusSucesso", idTipoStatusSucesso);
		return (Transacao) q.uniqueResult();
	}

	public boolean isEstornoTransacaoJaRealizado(Long idTransacao, Long idAcaoEstornoTransacao, Long idTipoStatusSucesso) {

		SQLQuery q = createSqlQuery(QUERY_COUNT_ESTORNO_TRANACAO_REALIZADO);
		q.setParameter("id", idTransacao);
		q.setParameter("idAcao", idAcaoEstornoTransacao);
		q.setParameter("idTipoStatusSucesso", idTipoStatusSucesso);
		BigInteger count = (BigInteger) q.uniqueResult();
		
		return BigInteger.ZERO.equals(count) ? false : true;
	}
	

}
