package <%=packageName%>.business.impl;

import <%=packageName%>.business.I<%=entityClass%>Business;
import <%=packageName%>.domain.<%=entityClass%>;
import <%=packageName%>.repository.I<%=entityClass%>Repository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class <%=entityClass%>Business implements I<%=entityClass%>Business {

	@Autowired(required = true)
	private I<%=entityClass%>Repository <%=entityInstance%>Repository;

	@Override
	public <%=entityClass%> findOne(Long id) {
		return this.<%=entityInstance%>Repository.findOne(id);
	}

	@Override
	public <%=entityClass%> save( <%=entityClass%> <%=entityInstance%>) {
		return this.<%=entityInstance%>Repository.save(<%=entityInstance%>);
	}

	@Override
	public Page<<%=entityClass%>> findAll(Pageable pageable) {
		return this.<%=entityInstance%>Repository.findAll(pageable);
	}

	@Override
	public void delete(<%=entityClass%> <%=entityInstance%>) {
		this.<%=entityInstance%>Repository.delete(<%=entityInstance%>);
	}
}