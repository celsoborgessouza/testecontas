package br.com.contas.service;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import br.com.contas.dao.PessoaJuridicaDAO;
import br.com.contas.domain.rdbs.PessoaFisica;
import br.com.contas.domain.rdbs.PessoaJuridica;
import br.com.contas.service.exception.ServiceException;

@Service
@Transactional
public class PessoaJuridicaService {
	
	@Autowired
	private PessoaJuridicaDAO dao;

	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private TipoPessoaService tipoPessoaService;

	@Transactional(rollbackFor = Exception.class)
	public Long criar(String cnpj, String razaoSocial, String nomeFantasia) throws Exception {

		try {
			validarCamposCriacao(cnpj, razaoSocial, nomeFantasia);
			
			Long idTipoPessoa = tipoPessoaService.recuperarIdPessoaJuridica();
			Long idPessoa = pessoaService.criar(idTipoPessoa);

			if (idPessoa == null) {
				throw new ServiceException("Não foi possível criar a pessoa associada a um tipo");
			}

			PessoaJuridica pessoaJuridica = configurarPessoaJuridica(cnpj, razaoSocial, nomeFantasia, idPessoa);

			return (Long) dao.save(pessoaJuridica);
		} catch (ServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException("Não foi possível criar pessoa juridica", e);
		}

	}

	private PessoaJuridica configurarPessoaJuridica(String cnpj, String razaoSocial, String nomeFantasia,
			Long idPessoa) {
		
		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		pessoaJuridica.setCnpj(cnpj);
		pessoaJuridica.setRazaoSocial(razaoSocial);
		pessoaJuridica.setNomeFantasia(nomeFantasia);
		pessoaJuridica.setIdPessoa(idPessoa);
		
		return pessoaJuridica;
	}

	private void validarCamposCriacao(String cnpj, String razaoSocial, String nomeFantasia) throws ServiceException {
		
		validarCnpj(cnpj);

		if (StringUtils.isEmpty(razaoSocial)) {
			throw new ServiceException("'Razão Social' não pode ser nulo");
		}
		
		if (StringUtils.isEmpty(nomeFantasia)) {
			throw new ServiceException("'Nome Fantasia' não pode ser nulo");
		}
		
		
	}

	private void validarCnpj(String cnpj) throws ServiceException {
		if (StringUtils.isEmpty(cnpj)) {
			throw new ServiceException("'CNPJ' não pode ser nulo");
		}
	}
	
	public PessoaFisica recuperarPorCnpj(String cnpj) throws ServiceException {

		try {
			validarCnpj(cnpj);
			return dao.recuperarPorCnpj(cnpj);
		} catch (Exception e) {

			throw new ServiceException("Falha ao recuperar pessoa física por cnpj", e);
		}
	}
	
	public Long recuperarIdPorCnpj(String cnpj) throws ServiceException  {
		
		try {
			PessoaFisica pessoaFisica = recuperarPorCnpj(cnpj);
			return pessoaFisica.getIdPessoa();
		} catch (ServiceException e) {
		
			throw new ServiceException("Falha ao recuperar id da pessoa física por cnpj", e);
		}
	}
	
	public List<PessoaJuridica> recuperarTodos() throws ServiceException {
		
		try {
			return dao.recuperarTodos();
		} catch (Exception e) {
			throw new ServiceException("Não foi possível realizar a consulta", e);
		}
	}


}
