package br.com.contas.service;

import java.math.BigDecimal;
import java.util.Date;

import javax.transaction.Transactional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.contas.dao.ContaDAO;
import br.com.contas.domain.rdbs.Conta;

@Service
public class ContaService {
	
	@Autowired
	private ContaDAO dao;
	
	@Transactional(rollbackOn = Exception.class)
	public Long criarConta(String nomeConta, Long idPessoa, Long idTipoConta, Long idSituacaoConta, Integer nivel, Long idContaPai) {
		
		validarConta(nomeConta, idPessoa, idTipoConta, idSituacaoConta, nivel, idContaPai);
		
		Conta conta = new Conta();
		conta.setNome(nomeConta);
		conta.setDataCriacao(new Date());
		conta.setSaldo(BigDecimal.ZERO);
		conta.setNivel(nivel);
		conta.setIdPessoa(idPessoa);
		conta.setIdTipoConta(idTipoConta);
		conta.setIdSituacaoConta(idSituacaoConta);
		conta.setIdContaPai(idContaPai);
		
		return (Long) dao.save(conta);
	}	
	
	

	private void validarConta(String nomeConta, Long idPessoa, Long idTipoConta, Long idSituacaoConta, Integer nivel,
			Long idContaPai) {
		
		
	}



	@Transactional(rollbackOn = Exception.class)
	public void atualizarSituacaoConta(Long idConta, Long idSituacaoConta) {
		
		Conta conta = new Conta();
		conta.setId(idConta);
		conta.setIdSituacaoConta(idSituacaoConta);
		
		dao.update(conta);
	}

	@Transactional(rollbackOn = Exception.class)
	public void atualizarSaldo(Long idConta, BigDecimal saldo) {
		
		Conta conta = new Conta();
		conta.setId(idConta);
		conta.setSaldo(saldo);
		
		dao.update(conta);
	}



	public Conta recuperarConta(Long idConta) {
		
		if (idConta == null) {
			throw new ServiceException("Identificador da conta deve ser informado");
		}
		
		return dao.load(idConta);
	}
}
