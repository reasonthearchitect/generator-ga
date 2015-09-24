package <%=packageName%>.facade;

import <%=packageName%>.facade.dto.<%=entityClass%>Dto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface for the <%=entityClass%> facade pattern.
 */
public interface I<%=entityClass%>Facade {

    <%=entityClass%>Dto save(<%=entityClass%>Dto <%=entityInstance%>Dto);

    <%=entityClass%>Dto findOne(Long id);

    void delete(<%=entityClass%>Dto <%=entityInstance%>Dto);

    Page<<%=entityClass%>Dto> findAll(Pageable pageable);
}