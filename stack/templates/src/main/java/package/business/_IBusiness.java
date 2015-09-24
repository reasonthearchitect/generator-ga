package <%=packageName%>.business;

import <%=packageName%>.domain.<%=entityClass%>;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface I<%=entityClass%>Business {

	<%=entityClass%> findOne(Long id);

	<%=entityClass%> save(<%=entityClass%> <%=entityInstance%>);

	Page<<%=entityClass%>> findAll(Pageable pageable);

	void delete(<%=entityClass%> <%=entityInstance%>);
}