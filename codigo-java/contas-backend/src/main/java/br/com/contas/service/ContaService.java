package br.com.contas.service;

import java.math.BigDecimal;
import java.util.Date;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.contas.dao.ContaDAO;
import br.com.contas.domain.rdbs.Conta;

@Service
public class ContaService {
	
	@Autowired
	private ContaDAO dao;
	
	/**
	 * 
	 * @param nomeConta
	 * @param idPessoa
	 * @param idTipoConta
	 * @param idSituacaoConta
	 * @param nivel
	 * @param idContaPai
	 * @param idContaPrinciapl TODO
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public Long criarConta(String nomeConta, Long idPessoa, Long idTipoConta, Long idSituacaoConta, Integer nivel, Long idContaPai, Long idContaPrinciapl) {
		
			
		Conta conta = new Conta();
		conta.setNome(nomeConta);
		conta.setDataCriacao(new Date());
		conta.setSaldo(BigDecimal.ZERO);
		conta.setNivel(nivel);
		conta.setIdPessoa(idPessoa);
		conta.setIdTipoConta(idTipoConta);
		conta.setIdSituacaoConta(idSituacaoConta);
		conta.setIdContaPai(idContaPai);
		conta.setIdContaPrincipal(idContaPrinciapl);
		
		return (Long) dao.save(conta);
	}	
	

	
	/**
	 * 
	 * Toda conta pode estar ariva, bloqueada ou cancelada
	 * Alterar status da conta para ativa, bloqueada ou cancelada
	 * 
	 * @param idConta
	 * @param idSituacaoConta
	 */
	@Transactional(rollbackFor = Exception.class)
	public void atualizarSituacaoConta(Long idConta, Long idSituacaoConta) {
		
		Conta conta = new Conta();
		conta.setId(idConta);
		conta.setIdSituacaoConta(idSituacaoConta);
		
		atualizarConta(conta);
	}

	@Transactional(rollbackFor = Exception.class)
	public void atualizarSaldo(Long idConta, BigDecimal saldo) {
		
		Conta conta = dao.load(idConta);
		conta.setId(idConta);
		conta.setSaldo(saldo);
		conta.setDataCriacao(new Date());
		
		atualizarConta(conta);
	}

	@Transactional(rollbackFor = Exception.class)
	public void atualizarConta(Conta conta) {
		dao.update(conta);
	}
	


	public Conta recuperarConta(Long idConta) {
		
		if (idConta == null) {
			throw new ServiceException("Identificador da conta deve ser informado");
		}
		
		return dao.load(idConta);
	}

	public Conta recuperarContaParaManipular(Long idConta) {
		
		if (idConta == null) {
			throw new ServiceException("Identificador da conta deve ser informado");
		}
		
		return dao.get(idConta);
	}

	/**
	 * Conta principal tem idContaPai = null

	 * @param idContaPai 
	 * @return
	 */
	public Long recuperarContaPrincipal(Long idContaPai) {
		
		if (idContaPai == null) {
			
		}
		
		do {
			Conta conta = recuperarConta(idContaPai);
			idContaPai = conta.getIdContaPai();
			
		} while (idContaPai != null) ;
		
		return idContaPai;
	}


}
