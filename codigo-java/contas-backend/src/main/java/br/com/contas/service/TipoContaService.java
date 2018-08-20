package br.com.contas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.contas.dao.TipoContaDAO;
import br.com.contas.service.exception.ServiceException;

@Service
public class TipoContaService {

	@Autowired
	private TipoContaDAO dao;

	public Long recuperarIdContaMatriz() throws ServiceException {

		try {

			return dao.recuperarPorNome("MATRIZ");
		} catch (Exception e) {

			throw new ServiceException("Falha ao recuperar identificador do tipo de conta matriz", e);
		}
	}

	public Long recuperarIdContaFilial() throws ServiceException {

		try {

			return dao.recuperarPorNome("FILIAL");
		} catch (Exception e) {

			throw new ServiceException("Falha ao recuperar tipo da conta", e);
		}
	}

}
