package br.com.contas.dao;

import org.springframework.stereotype.Repository;

import br.com.contas.domain.rdbs.Aporte;

@Repository
public class AporteDAO extends HibernateDAO<Aporte> {

	public AporteDAO() {
		super(Aporte.class);
	}


}
