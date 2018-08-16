package br.com.contas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.contas.dao.TipoPessoaDAO;
import br.com.contas.service.exception.ServiceException;

@Service
public class TipoPessoaService {

	@Autowired
	private TipoPessoaDAO dao;

	public Long recuperarIdPessoaFisica() throws ServiceException {

		try {

			return dao.recuperarIdPorNome("FISICA");
		} catch (Exception e) {

			throw new ServiceException("Falha ao recuperar identificador do tipo de pessoa física", e);
		}
	}

	public Long recuperarIdPessoaJuridica() throws Exception {

		try {

			return dao.recuperarIdPorNome("JURIDICA");
		} catch (Exception e) {

			throw new Exception("Falha ao recuperar identificador do tipo de pessoa jurídica", e);
		}
	}

}
