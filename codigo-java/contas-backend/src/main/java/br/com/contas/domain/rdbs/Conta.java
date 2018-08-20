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

@Entity(name = "conta")
public class Conta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "nome")
	private String nome;
	
	@Column(name = "data_criacao")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataCriacao;
	
	@Column(name = "saldo")
	private BigDecimal saldo;
	
	@Column(name = "nivel")
	private Integer nivel;
	
	@Column(name = "id_conta_pai")
	private Long idContaPai;
	
	@Column(name = "id_tipo_conta")
	private Long idTipoConta;
	
	@Column(name = "id_situacao_conta")
	private Long idSituacaoConta;
	
	@Column(name = "id_pessoa")
	private Long idPessoa;
	
	@Column(name = "id_conta_principal")
	private Long idContaPrincipal;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public Integer getNivel() {
		return nivel;
	}

	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}

	public Long getIdContaPai() {
		return idContaPai;
	}

	public void setIdContaPai(Long idContaPai) {
		this.idContaPai = idContaPai;
	}

	public Long getIdTipoConta() {
		return idTipoConta;
	}

	public void setIdTipoConta(Long idTipoConta) {
		this.idTipoConta = idTipoConta;
	}

	public Long getIdSituacaoConta() {
		return idSituacaoConta;
	}

	public void setIdSituacaoConta(Long idSituacaoConta) {
		this.idSituacaoConta = idSituacaoConta;
	}

	public Long getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(Long idPessoa) {
		this.idPessoa = idPessoa;
	}

	public Long getIdContaPrincipal() {
		return idContaPrincipal;
	}

	public void setIdContaPrincipal(Long idContaPrincipal) {
		this.idContaPrincipal = idContaPrincipal;
	}


	
	
}
