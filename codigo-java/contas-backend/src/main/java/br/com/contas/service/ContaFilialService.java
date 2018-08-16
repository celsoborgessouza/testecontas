package br.com.contas.service;

import java.math.BigDecimal;
import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.contas.dao.ContaDAO;
import br.com.contas.domain.rdbs.Conta;
import br.com.contas.domain.rdbs.PessoaFisica;
import br.com.contas.service.exception.ServiceException;


@Service
public class ContaFilialService {
	
	@Autowired
	private ContaDAO dao;
	
	@Autowired
	private PessoaFisicaService pessoaFisicaSerivce;
	
	@Autowired
	private SituacaoContaService situacaoContaService;
	
	@Autowired
	private TipoContaService tipoContaService;
	
	
	
	@Transactional(rollbackOn = Exception.class)
	public Long criarContaMatrizPessoaFisica(String nomeConta, String cpf) throws Exception    {
		
		try {
			validarCriacaoContaMatriz(cpf, nomeConta);
			Long idPessoa = recuperarIdPessoaFisica(cpf);
			
			return criarContaMatriz(nomeConta, idPessoa);
		} catch (ServiceException e) {
			throw new ServiceException(e);
		} catch (Exception e) {
			throw new Exception("Não foi possível criar conta matriz da pessoa física", e);
		}
	}
	
	private void validarCriacaoContaMatriz(String cpf, String nomeConta) throws ServiceException {
		
		validarCpf(cpf);		
		validarNomeConta(nomeConta);
	}
	
	private void validarCpf(String cpf) throws ServiceException {
		if (StringUtils.isEmpty(cpf)) {
			throw new ServiceException("'CPF' deve ser informado");
		}
	}

	
	private void validarNomeConta(String nomeConta) throws ServiceException {
		if (StringUtils.isEmpty(nomeConta)) {
			throw new ServiceException("'Nome da conta' deve ser informado");
		}
	}

	private Long criarContaMatriz(String nomeConta, Long idPessoa) throws Exception  {
		Long idTipoConta = recuperarTipoContaMatriz();
		Long idSituacaoConta = recuperarSituacaoInical();
		Integer nivel = 1;
		Long idContaPai = null;
		
		
		return criarConta(nomeConta, idPessoa, idTipoConta, idSituacaoConta, nivel, idContaPai);
	}
	
	private Long recuperarTipoContaMatriz() throws ServiceException {

		try {
			return tipoContaService.recuperarIdContaMatriz();
		} catch (ServiceException e) {
			throw new ServiceException("Não foi possível recuperar o tipo inicial da conta");
		}
	}

	private Long recuperarSituacaoInical() throws ServiceException {
		
		try {
			return situacaoContaService.recuperarIdAtivo();
		} catch (ServiceException e) {
			throw new ServiceException("Não foi possível recuperar a situação inical da conta");
		}
	}


	private Long recuperarIdPessoaFisica(String cpf) throws Exception  {
		
		try {
			PessoaFisica pessoaFisica = pessoaFisicaSerivce.recuperarPorCpf(cpf);
			return pessoaFisica.getIdPessoa();
		} catch (Exception e) {
			throw new Exception("Não foi possível recuperar a pessoa pelo CPF", e);
		}
		
	}


	private Long criarConta(String nomeConta, Long idPessoa, Long idTipoConta, Long idSituacaoConta, Integer nivel, Long idContaPai) {
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
	
	
	@Transactional(rollbackOn = Exception.class)
	public Long criarContaPessoaFisica(String nomeConta, String cpf, Long idContaPai) throws Exception  {
		
		validarCriacaoContaFilial(cpf, nomeConta, idContaPai);
		
		Long idPessoa = recuperarIdPessoaFisica(cpf);		
		return criarContaFilial(nomeConta, idPessoa, idContaPai);
	}
		
	private Long criarContaFilial(String nomeConta, Long idPessoa, Long idContaPai) throws ServiceException {
	
		Long idTipoConta = recuperarTipoContaFilial();
		Long idSituacaoConta = recuperarSituacaoInical();
		Integer nivel = recuperarHierarquia(idContaPai);
		
		
		return criarConta(nomeConta, idPessoa, idTipoConta, idSituacaoConta, nivel, idContaPai);
	}

	private Integer recuperarHierarquia(Long idContaPai) {
		// TODO Auto-generated method stub
		return null;
	}

	private Long recuperarTipoContaFilial() {
		// TODO Auto-generated method stub
		return null;
	}

	private void validarCriacaoContaFilial(String cpf, String nomeConta, Long idContaPai) throws ServiceException {
		
		validarCpf(cpf);
		validarNomeConta(nomeConta);
		
		if (idContaPai == null) {
			throw new ServiceException("Deve ser informado uma conta pai para a criação da conta filha");
		}
		
	}

	// TODO PESSOA JURIÍDICA MATRIZ
	@Transactional(rollbackOn = Exception.class)
	public Long criarContaMatrizPessoaJuridica(String nomeConta, String cnpj) throws Exception  {
			
		Long idPessoa = null;
		return criarContaMatriz(nomeConta, idPessoa);
	}

	// TODO PESSOA JURIÍDICA FILIAL
	@Transactional(rollbackOn = Exception.class)
	public Long criarContaMatrizPessoaJuridicaFilial(String nomeConta, String cnpj, Long idContaPai) throws Exception  {
		
		Long idPessoa = null;
		return criarContaMatriz(nomeConta, idPessoa);
	}
	
	// TODO
	@Transactional(rollbackOn = Exception.class)
	public void atualizarContaPessoaJuridica(Long idConta, String razaoSocial, String nomeFantasia) {
		
	}

	
	// TODO
	@Transactional(rollbackOn = Exception.class)
	public void atualizarStatus (Long idConta, Long idStatus) {
		
	}

	
	// TODO
	@Transactional(rollbackOn = Exception.class)
	public void realizaTransacaorAporte(Long idConta, BigDecimal valor) {
		
	}
	
	// TODO
	public void realizarTransacaoEstronoAporte (Long idContaDestino, String codigo) {
		
	}
	
	// TODO
	@Transactional(rollbackOn = Exception.class)
	public void realizarTransacaoTransferencaia(Long idContaOrigm, Long idContaDestino, BigDecimal valor) {
		
	}

	
	// TODO
	/*
	 * Estorno da conta de origm para a conta de destino
	 */
	public void realizarTransacaoEstornoTransferecnia (Long idContaOrigm, Long idContaDestino) {
		
	}
	
	// TODO
	public void recuperarTransacoes(Long idConta) {
		
	}

	
}
