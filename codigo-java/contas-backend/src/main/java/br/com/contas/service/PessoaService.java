package br.com.contas.service;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.contas.dao.PessoaDAO;
import br.com.contas.domain.rdbs.Pessoa;

@Service
public class PessoaService {
	
	@Autowired
	private PessoaDAO dao;
	
	public List<Pessoa> getPessoas() {
		return dao.getPessoas();
	}
	
	@Transactional(rollbackOn = Exception.class)
	public Long criar(Long idTipoPessoa) {
		
		try {
			Pessoa pessoa = new Pessoa();
			pessoa.setIdTipoPessoa(idTipoPessoa);
			return (Long) dao.save(pessoa);
		} catch (Exception e) {
			throw new ServiceException("Flha ao criar pessoa", e);
		}
	}
	
}
