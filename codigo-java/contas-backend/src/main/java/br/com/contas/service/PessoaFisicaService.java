package br.com.contas.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.contas.dao.PessoaFisicaDAO;
import br.com.contas.domain.rdbs.PessoaFisica;
import br.com.contas.service.exception.ServiceException;

@Service
@Transactional
public class PessoaFisicaService {

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
	public Long criar(String cpf, String nomeCompleto, String dataNascimento) throws ServiceException {

		try {
			validarCamposCriacao(cpf, nomeCompleto, dataNascimento);
			
			Long idTipoPessoa = tipoPessoaService.recuperarIdPessoaFisica();
			Long idPessoa = pessoaService.criar(idTipoPessoa);

			if (idPessoa == null) {
				throw new ServiceException("Não foi possívelcriar a pessoa associada a um tipo");
			}

			PessoaFisica pessoaFisica = configurarPessoaFisica(cpf, nomeCompleto, dataNascimento, idPessoa);

			return (Long) dao.save(pessoaFisica);
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException("Não foi possível criar pessoa física", e);
		}

	}

	private PessoaFisica configurarPessoaFisica(String cpf, String nomeCompleto, String dataNascimento, Long idPessoa)
			throws ServiceException {

		Date date = stringToDate(dataNascimento);

		PessoaFisica pessoaFisica = new PessoaFisica();
		pessoaFisica.setCpf(cpf);
		pessoaFisica.setNomeCompleto(nomeCompleto);
		pessoaFisica.setIdPessoa(idPessoa);
		pessoaFisica.setDataNascimento(date);
		return pessoaFisica;
	}

	private Date stringToDate(String dataNascimento) throws ServiceException {

		try {
			return new SimpleDateFormat("dd/MM/yyyy").parse(dataNascimento);
		} catch (ParseException e) {
			throw new ServiceException("Não foi possível cadastrar data de nascimento com um formato válido. Formato deve ser dd/MM/yyyy");
		}

	}

	public PessoaFisica recuperarPorCpf(String cpf) throws ServiceException {

		try {
			validarCpf(cpf);
			return dao.recuperarPorCpf(cpf);
		} catch (Exception e) {

			throw new ServiceException("Falha ao recuperar pessoa física por cpf", e);
		}
	}
	
	public Long recuperarIdPorCpf(String cpf) throws ServiceException  {
		
		try {
			PessoaFisica pessoaFisica = recuperarPorCpf(cpf);
			return pessoaFisica.getIdPessoa();
		} catch (ServiceException e) {
		
			throw new ServiceException("Falha ao recuperar id da pessoa física por cpf", e);
		}
	}

	/*
	 * Validações utilizadas pelos méotodos do serviço
	 */

	private void validarCamposCriacao(String cpf, String nomeCompleto, String dataNascimento) throws ServiceException {
		validarCpf(cpf);

		if (StringUtils.isEmpty(nomeCompleto)) {
			throw new ServiceException("'Nome Completo' não pode ser nulo");
		}

		if (StringUtils.isEmpty(dataNascimento)) {
			throw new ServiceException("'Data de Nascimento' não pode ser nulo");
		}

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
			throw new ServiceException("Não foi possível realizar a consulta", e);
		}
	}



}
