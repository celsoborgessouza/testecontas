package br.com.contas.domain.rdbs;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Pessoa implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "id_tipo_pessoa", insertable = true, updatable = false)
	private Long idTipoPessoa;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIdTipoPessoa() {
		return idTipoPessoa;
	}

	public void setIdTipoPessoa(Long idTipoPessoa) {
		this.idTipoPessoa = idTipoPessoa;
	}

	
}
