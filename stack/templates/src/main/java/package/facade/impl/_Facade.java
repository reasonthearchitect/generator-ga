package <%=packageName%>.facade.impl;

import <%=packageName%>.business.I<%=entityClass%>Business;
import <%=packageName%>.domain.<%=entityClass%>;
import <%=packageName%>.facade.I<%=entityClass%>Facade;
import <%=packageName%>.facade.dto.<%=entityClass%>Dto;
import <%=packageName%>.facade.mapper.<%=entityClass%>Mapper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.stream.Collectors;

/**
 * Facade code for the <%=entityClass%>.
 */
@Service
public class <%=entityClass%>Facade implements I<%=entityClass%>Facade {

    @Inject
    private <%=entityClass%>Mapper <%=entityInstance%>Mapper;

    @Inject
    private I<%=entityClass%>Business <%=entityInstance%>Business;

    @Override
    @Transactional
    public <%=entityClass%>Dto save(<%=entityClass%>Dto <%=entityInstance%>Dto) {
        <%=entityClass%> <%=entityInstance%> = this.<%=entityInstance%>Business.save(this.<%=entityInstance%>Mapper.<%=entityInstance%>DtoTo<%=entityClass%>(<%=entityInstance%>Dto));
        return this.<%=entityInstance%>Mapper.<%=entityInstance%>To<%=entityClass%>Dto(<%=entityInstance%>);
    }

    @Override
    @Transactional(readOnly = true)
    public <%=entityClass%>Dto findOne(Long id){
        <%=entityClass%> <%=entityInstance%> = this.<%=entityInstance%>Business.findOne(id);
        return this.<%=entityInstance%>Mapper.<%=entityInstance%>To<%=entityClass%>Dto(<%=entityInstance%>);
    }

    @Override
    @Transactional
    public void delete(<%=entityClass%>Dto <%=entityInstance%>Dto) {
    	this.<%=entityInstance%>Business.delete(this.<%=entityInstance%>Mapper.<%=entityInstance%>DtoTo<%=entityClass%>(<%=entityInstance%>Dto));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<<%=entityClass%>Dto> findAll(Pageable pageable) {
    	Page<<%=entityClass%>> rpage = this.<%=entityInstance%>Business.findAll(pageable);
        return new PageImpl<>(
                rpage.getContent().stream()
                        .map(<%=entityInstance%>Mapper::<%=entityInstance%>To<%=entityClass%>Dto)
                        .collect(Collectors.toCollection(LinkedList::new)),
                new PageRequest(rpage.getNumber(), rpage.getSize()),
                rpage.getTotalElements());
    }
}
