package br.com.contas.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.contas.dao.PessoaFisicaDAO;
import br.com.contas.domain.rdbs.PessoaFisica;
import br.com.contas.service.exception.ServiceException;
import br.com.contas.utils.DateUtils;

@Service
@Transactional
public class PessoaFisicaService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PessoaFisicaDAO dao;

	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private TipoPessoaService tipoPessoaService;

	/**
	 * 
	 * @param cpf
	 * @param nomeCompleto
	 * @param dataNascimento
	 *            - formato de entrda dd/MM/yyy
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class)
	public PessoaFisica criar(String cpf, String nomeCompleto, String dataNascimento) throws ServiceException {

		try {
			validarCamposCriacao(cpf, nomeCompleto, dataNascimento);

			Long idTipoPessoa = tipoPessoaService.recuperarIdPessoaFisica();
			Long idPessoa = pessoaService.criar(idTipoPessoa);

			if (idPessoa == null) {
				throw new ServiceException("Não foi possível configurar pessa física");
			}

			PessoaFisica pessoaFisica = configurarPessoaFisica(cpf, nomeCompleto, dataNascimento, idPessoa);
			
			Long id = (Long) dao.save(pessoaFisica);
			return dao.get(id);
			
		} catch (ServiceException e) {
			logger.error("Error", e);
			throw new ServiceException(e.getMessage(), e);
		} catch (ConstraintViolationException e) {
			logger.error("Erro", e);
			throw new ServiceException(String.format("Já existe pessoa física cadastrada com o cpf: %s", cpf), e);
		} catch (Exception e) {
			logger.error("Error", e);
			throw new ServiceException("Não foi possível criar pessoa física", e);
		}

	}

	private PessoaFisica configurarPessoaFisica(String cpf, String nomeCompleto, String dataNascimento, Long idPessoa)
			throws ServiceException {

		try {
			Date date = stringToDate(dataNascimento);

			PessoaFisica pessoaFisica = new PessoaFisica();
			pessoaFisica.setCpf(cpf);
			pessoaFisica.setNomeCompleto(nomeCompleto);
			pessoaFisica.setIdPessoa(idPessoa);
			pessoaFisica.setDataNascimento(date);
			return pessoaFisica;
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException("Não foi possível criar pessoa física", e);
		}
	}

	private Date stringToDate(String dataNascimento) throws ServiceException {

		try {
			return DateUtils.converterParaDate(dataNascimento);
		} catch (ParseException e) {
			throw new ServiceException("Data de nascimento com formato válido. Formato deve ser dd/MM/yyyy");
		}

	}

	public PessoaFisica recuperarPorCpf(String cpf) throws ServiceException {

		try {
			validarCpf(cpf);
			return dao.recuperarPorCpf(cpf);
		} catch (ServiceException e) {

			throw new ServiceException("Falha ao recuperar pessoa física por cpf", e);
		}
	}

	public Long recuperarIdPorCpf(String cpf) throws ServiceException {
		PessoaFisica pessoaFisica = recuperarPorCpf(cpf);

		if (pessoaFisica == null) {
			throw new ServiceException(String.format("CPF %s não está cadastrado.", cpf));
		}
		return pessoaFisica.getIdPessoa();
	}

	/*
	 * Validações utilizadas pelos méotodos do serviço
	 */

	private void validarCamposCriacao(String cpf, String nomeCompleto, String dataNascimento) throws ServiceException {
		validarCpf(cpf);

		validarNomeCompleto(nomeCompleto);

		validarDataNascimento(dataNascimento);

	}

	private void validarCpf(String cpf) throws ServiceException {
		if (StringUtils.isEmpty(cpf)) {
			throw new ServiceException("'CPF' não pode ser nulo");
		}

		if (cpf.trim().length() != 11) {
			throw new ServiceException("'CPF' deve ter 11 dígitos");
		}
	}

	public List<PessoaFisica> recuperarTodos() throws ServiceException {

		try {
			return dao.recuperarTodos();
		} catch (Exception e) {

			logger.error("Error", e);
			throw new ServiceException("Não foi possível realizar todas as pessoas físicas", e);
		}
	}

	public List<PessoaFisica> recuperar(String cpf, String nome) throws ServiceException {

		try {
			return dao.recuprar(cpf, nome);
		} catch (Exception e) {
			throw new ServiceException("Não foi possíve realizar a consulta de pessoa fisica por parâmetro", e);
		}
	}
	
	/**
	 * São atualizados somente nome completo e data de nascimento
	 * O cpf é utilizado para recupera o identificador da pessoa
	 * 
	 * @param cpf
	 * @param nomeCompleto
	 * @param dataFormatada
	 * @return
	 * @throws ServiceException 
	 */
	@Transactional(rollbackFor = Exception.class)
	public PessoaFisica atualizar(String cpf, String nomeCompleto, String dataNascimento) throws ServiceException {
		try {
			
			
			validarCamposAtualizacao(cpf, nomeCompleto, dataNascimento);
			
			Date date = stringToDate(dataNascimento);
			
			PessoaFisica pessoaFisicaAtual = recuperarPorCpf(cpf);
			pessoaFisicaAtual.setNomeCompleto(nomeCompleto);
			pessoaFisicaAtual.setDataNascimento(date);
			
			Long id = pessoaFisicaAtual.getId();
			
			dao.update(pessoaFisicaAtual);
			return dao.get(id);
			
		} catch (ServiceException e) {
			logger.error("Error", e);
			throw new ServiceException(e.getMessage(), e);
		} catch (ConstraintViolationException e) {
			logger.error("Erro", e);
			throw new ServiceException(String.format("Já existe pessoa física cadastrada com o cpf: %s", cpf), e);
		} catch (Exception e) {
			logger.error("Error", e);
			throw new ServiceException("Não foi possível criar pessoa física", e);
		}

	}

	private void validarCamposAtualizacao(String cpf, String nomeCompleto, String dataNascimento) throws ServiceException {
				
		validarCpf(cpf);
		validarNomeCompleto(nomeCompleto);
		validarDataNascimento(dataNascimento);
		
	}

	private void validarDataNascimento(String dataNascimento) throws ServiceException {
		if (StringUtils.isEmpty(dataNascimento)) {
			throw new ServiceException("'Data de Nascimento' não pode ser nulo");
		}
	}

	private void validarNomeCompleto(String nomeCompleto) throws ServiceException {
		if (StringUtils.isEmpty(nomeCompleto)) {
			throw new ServiceException("'Nome Completo' não pode ser nulo");
		}
	}
	

}
