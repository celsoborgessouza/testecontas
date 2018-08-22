package br.com.contas.domain.rdbs;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Transacao implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "data_transacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataTransacao;

	@Column(name = "valor")
	private BigDecimal valor;

	@Column(name = "descricao_status_transacao")
	private String descricaoStatusTransacao;

	@Column(name = "id_tipo_status_transacao")
	private Long idTipoStatusTransacao;

	@Column(name = "id_tipo_acao_transacao")
	private Long idTipoAcaoTransacao;

	@Column(name = "id_aporte")
	private Long idAporte;

	@Column(name = "id_conta_origem")
	private Long idContaOrigem;

	@Column(name = "id_conta_destino")
	private Long idContaDestino;
	
	@Column(name = "id_transferencia_estornado")
	private Long idTransferenciaEstornado;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDataTransacao() {
		return dataTransacao;
	}

	public void setDataTransacao(Date dataTransacao) {
		this.dataTransacao = dataTransacao;
	}

	public Long getIdTipoStatusTransacao() {
		return idTipoStatusTransacao;
	}

	public void setIdTipoStatusTransacao(Long idTipoStatusTransacao) {
		this.idTipoStatusTransacao = idTipoStatusTransacao;
	}

	public Long getIdTipoAcaoTransacao() {
		return idTipoAcaoTransacao;
	}

	public void setIdTipoAcaoTransacao(Long idTipoAcaoTransacao) {
		this.idTipoAcaoTransacao = idTipoAcaoTransacao;
	}

	public Long getIdAporte() {
		return idAporte;
	}

	public void setIdAporte(Long idAporte) {
		this.idAporte = idAporte;
	}

	public Long getIdContaOrigem() {
		return idContaOrigem;
	}

	public void setIdContaOrigem(Long idContaOrigem) {
		this.idContaOrigem = idContaOrigem;
	}

	public Long getIdContaDestino() {
		return idContaDestino;
	}

	public void setIdContaDestino(Long idContaDestino) {
		this.idContaDestino = idContaDestino;
	}

	public String getDescricaoStatusTransacao() {
		return descricaoStatusTransacao;
	}

	public void setDescricaoStatusTransacao(String descricaoStatusTransacao) {
		this.descricaoStatusTransacao = descricaoStatusTransacao;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Long getIdTransferenciaEstornado() {
		return idTransferenciaEstornado;
	}

	public void setIdTransferenciaEstornado(Long idTransferenciaEstornado) {
		this.idTransferenciaEstornado = idTransferenciaEstornado;
	}
	
	
}
