package br.com.contas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.contas.domain.rdbs.Conta;
import br.com.contas.domain.rdbs.PessoaFisica;
import br.com.contas.service.exception.ServiceException;

/*
 * Conta Filial - É uma conta idêntica a Conta Matriz, porém possui obrigatoriamente uma conta Pai (pode
 * ser Conta Matriz ou outra Conta Filial) e pode ou não ter uma Conta Filial abaixo
 * 
 * Toda conta deve possuir uma Pessoa e esta pode ser Jurídica ou Física
 */
@Service
public class ContaFilialService {

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

	@Transactional(rollbackFor = Exception.class)
	public Conta criarContaPessoaFisica(String nomeConta, String cpf, Long idContaPai) throws Exception {

		validarParametroCriacaoContaFilial(cpf, nomeConta, idContaPai);

		Long idPessoa = recuperarIdPessoaFisica(cpf);

		Long idConta = criarContaFilial(nomeConta, idPessoa, idContaPai);

		return contaService.carregarConta(idConta);
	}

	private void validarParametroCriacaoContaFilial(String cpf, String nomeConta, Long idContaPai)
			throws ServiceException {

		validarCpf(cpf);
		validarNomeConta(nomeConta);

		if (idContaPai == null) {
			throw new ServiceException("Deve ser informado uma conta pai para a criação da conta filial");
		}

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

	/**
	 * Conta Filial - É uma conta idêntica a Conta Matriz, porém possui
	 * obrigatoriamente uma conta Pai (pode ser Conta Matriz ou outra Conta
	 * Filial) e pode ou não ter uma Conta Filial abaixo
	 * 
	 * Toda conta deve possuir uma Pessoa e esta pode ser Jurídica ou Física
	 * 
	 * @param nomeConta
	 * @param idPessoa
	 * @param idContaPai
	 * @return
	 * @throws ServiceException
	 */
	private Long criarContaFilial(String nomeConta, Long idPessoa, Long idContaPai) throws ServiceException {

		Long idTipoConta = tipoContaService.recuperarIdContaFilial();
		Long idSituacaoConta = situacaoContaService.recuperarIdAtivo();
		Integer nivel = recuperarNivel(idContaPai);
		Long idContaPrincipal = contaService.carregarConta(idContaPai).getIdContaPrincipal();

		if (idContaPrincipal == null) {
			throw new ServiceException("A conta pai deve retornar a refeência para conta principal");
		}

		return contaService.criarConta(nomeConta, idPessoa, idTipoConta, idSituacaoConta, nivel, idContaPai,
				idContaPrincipal);
	}

	private Long recuperarIdPessoaFisica(String cpf) throws ServiceException {

		PessoaFisica pessoaFisica;
		try {
			pessoaFisica = pessoaFisicaSerivce.recuperarPorCpf(cpf);
			return pessoaFisica.getIdPessoa();
		} catch (ServiceException e) {
			throw new ServiceException("Não foi possível recuperar a pessoa pelo CPF", e);
		}

	}

	private Integer recuperarNivel(Long idContaPai) throws ServiceException {

		Conta contaPai = contaService.carregarConta(idContaPai);
		Integer nivelPai = contaPai.getNivel();
		return nivelPai + 1;
	}

	@Transactional(rollbackFor = Exception.class)
	public Long criarContaPessoaJuridica(String nomeConta, String cnpj, Long idContaPai) throws Exception {

		validarParametroCriacaoContaPessoaJuridica(cnpj, nomeConta, idContaPai);

		Long idPessoa = pessoaJuridicaService.recuperarIdPorCnpj(cnpj);
		return criarContaFilial(nomeConta, idPessoa, idContaPai);
	}

	private void validarParametroCriacaoContaPessoaJuridica(String cnpj, String nomeConta, Long idContaPai)
			throws ServiceException {
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
