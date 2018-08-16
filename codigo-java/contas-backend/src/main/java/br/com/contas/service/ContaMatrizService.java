package br.com.contas.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.contas.service.exception.ServiceException;;

@Service
public class ContaMatrizService {

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

	@Transactional(rollbackOn = Exception.class)
	public Long criarContaPessoaFisica(String nomeConta, String cpf) throws Exception {

		try {
			validarCriacaoContaPessoaFisica(cpf, nomeConta);

			Long idPessoa = pessoaFisicaSerivce.recuperarIdPorCpf(cpf);

			return criarContaMatriz(nomeConta, idPessoa);

		} catch (ServiceException e) {
			throw new ServiceException(e);
		} catch (Exception e) {
			throw new Exception("Não foi possível criar conta matriz da pessoa física", e);
		}
	}

	private void validarCriacaoContaPessoaFisica(String cpf, String nomeConta) throws ServiceException {

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

	private Long criarContaMatriz(String nomeConta, Long idPessoa) throws Exception {

		Long idTipoConta = tipoContaService.recuperarIdContaMatriz();
		Long idSituacaoConta = situacaoContaService.recuperarIdAtivo();
		Integer nivel = 1;
		Long idContaPai = null;
		
		
		return contaService.criarConta(nomeConta, idPessoa, idTipoConta, idSituacaoConta, nivel, idContaPai);
	}

	// TODO PESSOA JURIÍDICA MATRIZ
	@Transactional(rollbackOn = Exception.class)
	public Long criarContaPessoaJuridica(String nomeConta, String cnpj) throws Exception {
		
		validarCriacaoContaPessoaJuridica(cnpj, nomeConta);

		Long idPessoa = pessoaJuridicaService.recuperarIdPorCnpj(cnpj);
		return criarContaMatriz(nomeConta, idPessoa);
	}


	private void validarCriacaoContaPessoaJuridica(String cnpj, String nomeConta) throws ServiceException {
		validarCnpj(cnpj);
		validarNomeConta(nomeConta);
		
	}

	private void validarCnpj(String cnpj) throws ServiceException {
		if (StringUtils.isEmpty(cnpj)) {
			throw new ServiceException("'CNPJ' deve ser informado");
		}
	}

}
