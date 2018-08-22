package br.com.contas.service;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.contas.dao.ContaDAO;
import br.com.contas.domain.rdbs.Conta;
import br.com.contas.service.exception.ServiceException;

@Service
public class ContaService {
	
	@Autowired
	private ContaDAO dao;
	
	@Autowired
	private SituacaoContaService situacaoContaService;
	
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
	 * @return 
	 * @throws br.com.contas.service.exception.ServiceException 
	 */
	@Transactional(rollbackFor = Exception.class)
	public void atualizarSituacaoConta(Long idConta, String nomeSituacao) throws ServiceException {
		
		try {
			validarParametroAlterarSituacaoConta(idConta, nomeSituacao);
			
			Long idSituacaoConta = recuperarIdSituacaoConta(nomeSituacao);
			
			Conta conta = recuperarPorId(idConta);
			conta.setIdSituacaoConta(idSituacaoConta);
			
			atualizarConta(conta);
		} catch (Exception e) {
			throw new ServiceException("Não foi possível atualizar status da conta",e);
		}
	}



	private Long recuperarIdSituacaoConta(String nomeSituacao) throws ServiceException {
		Long idSituacaoConta = situacaoContaService.recuperarIdPorNome(nomeSituacao);
		if (idSituacaoConta == null) {
			throw new ServiceException(String.format("Não foi possível recuperar situação de conta %s", nomeSituacao));
		}
		return idSituacaoConta;
	}



	private void validarParametroAlterarSituacaoConta(Long idConta, String situacao) throws ServiceException {
		
		verificarIdConta(idConta);
		if (StringUtils.isEmpty(situacao)) {
			throw new ServiceException("Nome da situação da conta deve ser informado");
		}
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
	
	
	public Conta recuperarPorId(Long idConta) throws ServiceException {
		
		verificarIdConta(idConta);
		return dao.get(idConta);
	}
	
	public Conta carregarConta(Long idConta) throws ServiceException {
		
		verificarIdConta(idConta);
		return dao.load(idConta);
	}



	private void verificarIdConta(Long idConta) throws ServiceException {
		if (idConta == null) {
			throw new ServiceException("Identificador da conta deve ser informado");
		}
	}



}
