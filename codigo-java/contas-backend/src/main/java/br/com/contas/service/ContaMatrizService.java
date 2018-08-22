package br.com.contas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.contas.domain.rdbs.Conta;
import br.com.contas.service.exception.ServiceException;;

@Service
public class ContaMatrizService {
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ContaService contaService;

	@Autowired
	private PessoaFisicaService pessoaFisicaSerivce;
	
	@Autowired
	private PessoaJuridicaService pessoaJuridicaService;

	@Autowired
	private SituacaoContaService situacaoContaService;

	@Autowired
	private TipoContaService tipoContaService;
	
	/*
	 * Conta Matriz - É a conta principal, a qual pode ter(n) contas filhas e essas também podem possuir suas
	 * filhas, formando assim uma hierarquia. É a principal conta da estrutura
	 * 
	 * Toda conta deve possuir uma Pessoa e esta pode ser Jurídica ou Física
	 */
	@Transactional(rollbackFor = Exception.class)
	public Conta criarContaPessoaFisica(String nomeConta, String cpf) throws ServiceException  {

		try {
			validarParametrosCriacaoContaPessoaFisica(cpf, nomeConta);

			Long idPessoa = pessoaFisicaSerivce.recuperarIdPorCpf(cpf);
			
			Long idConta = criarContaMatriz(nomeConta, idPessoa);

			return contaService.carregarConta(idConta);

		} catch (ServiceException e) {
			logger.error("Erro", e);
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			logger.error("Erro", e);
			throw new ServiceException("Não foi possível criar conta matriz da pessoa física", e);
		}
	}

	private void validarParametrosCriacaoContaPessoaFisica(String cpf, String nomeConta) throws ServiceException {

		validarCpf(cpf);
		validarNomeConta(nomeConta);
	}

	private void validarCpf(String cpf) throws ServiceException {
		if (StringUtils.isEmpty(cpf)) {
			throw new ServiceException("'CPF' deve ser informado");
		}
		
		if (cpf.length() != 11) {
			throw new ServiceException("Tamanho de cpf inválido");
		}
	}

	private void validarNomeConta(String nomeConta) throws ServiceException {
		if (StringUtils.isEmpty(nomeConta)) {
			throw new ServiceException("'Nome da conta' deve ser informado");
		}
	}
	
	
	private Long criarContaMatriz(String nomeConta, Long idPessoa) throws Exception {

		Long idTipoConta = tipoContaService.recuperarIdContaMatriz();
		Long idSituacaoConta = situacaoContaService.recuperarIdAtivo();
		Integer nivel = 1;
		Long idContaPai = null;
		Long idContaPrincipal = null;
		
		Long idConta = contaService.criarConta(nomeConta, idPessoa, idTipoConta, idSituacaoConta, nivel, idContaPai, idContaPrincipal);
		Conta conta = contaService.carregarConta(idConta);
		conta.setIdContaPrincipal(idConta);
		contaService.atualizarConta(conta);
		return idConta;
	}


	@Transactional(rollbackFor = Exception.class)
	public Conta criarContaPessoaJuridica(String nomeConta, String cnpj) throws ServiceException  {
		
		try {
			validarParametrosCriacaoContaPessoaJuridica(cnpj, nomeConta);

			Long idPessoa = pessoaJuridicaService.recuperarIdPorCnpj(cnpj);
			Long idConta = criarContaMatriz(nomeConta, idPessoa);
			return contaService.carregarConta(idConta);
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException("Não foi possível criar conta matriz da pessoa jurídica", e);
		}
	}


	private void validarParametrosCriacaoContaPessoaJuridica(String cnpj, String nomeConta) throws ServiceException {
		validarCnpj(cnpj);
		validarNomeConta(nomeConta);
		
	}

	private void validarCnpj(String cnpj) throws ServiceException {
		if (StringUtils.isEmpty(cnpj)) {
			throw new ServiceException("'CNPJ' deve ser informado");
		}
		
		if (cnpj.length() != 14) {
			throw new ServiceException("Tamanho do cnpj inválido");
		}

	}

}
