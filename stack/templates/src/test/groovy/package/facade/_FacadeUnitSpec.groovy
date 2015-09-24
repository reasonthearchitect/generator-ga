package <%=packageName%>.test.facade

import <%=packageName%>.business.I<%=entityClass%>Business
import <%=packageName%>.domain.<%=entityClass%>
import <%=packageName%>.facade.I<%=entityClass%>Facade
import <%=packageName%>.facade.impl.<%=entityClass%>Facade
import <%=packageName%>.facade.dto.<%=entityClass%>Dto
import <%=packageName%>.facade.mapper.<%=entityClass%>Mapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

/**
 * Unit spec for the <%=entityClass%> facade.
 */
class <%=entityClass%>FacadeUnitSpec extends Specification {

    I<%=entityClass%>Facade <%=entityInstance%>Facade;

    def setup() {
        this.<%=entityInstance%>Facade = new <%=entityClass%>Facade();
    }

    def "simple unit test from the generation framework for the save function provided"() {

        setup:
        def <%=entityInstance%>Business = Mock(I<%=entityClass%>Business);
        def <%=entityInstance%>Mapper = Mock(<%=entityClass%>Mapper);
        this.<%=entityInstance%>Facade.<%=entityInstance%>Business = <%=entityInstance%>Business;
        this.<%=entityInstance%>Facade.<%=entityInstance%>Mapper = <%=entityInstance%>Mapper;
        def <%=entityInstance%>Dto = [id: 1L]as <%=entityClass%>Dto;
        def <%=entityInstance%> =[id: 1L]as <%=entityClass%>;

        when:
        def r<%=entityInstance%>Dto = this.<%=entityInstance%>Facade.save(<%=entityInstance%>Dto);

        then:
        1 * <%=entityInstance%>Mapper.<%=entityInstance%>DtoTo<%=entityClass%>(_) >> <%=entityInstance%>;
        1 * <%=entityInstance%>Mapper.<%=entityInstance%>To<%=entityClass%>Dto(_) >> <%=entityInstance%>Dto;
        1 * <%=entityInstance%>Business.save(_) >> <%=entityInstance%>;
    }

    def "simple unit test to find one"(){

        setup:
        def <%=entityInstance%>Business = Mock(I<%=entityClass%>Business);
        def <%=entityInstance%>Mapper = Mock(<%=entityClass%>Mapper);
        this.<%=entityInstance%>Facade.<%=entityInstance%>Business = <%=entityInstance%>Business;
        this.<%=entityInstance%>Facade.<%=entityInstance%>Mapper = <%=entityInstance%>Mapper;
        def r<%=entityInstance%> =[id: 1L]as <%=entityClass%>;

        when:
        def result = this.<%=entityInstance%>Facade.findOne(1L);

        then:
        1 * <%=entityInstance%>Business.findOne(_) >> r<%=entityInstance%>;
        r<%=entityInstance%> != null;
    }

    def "simple unit test for deleting an entity"() {

        setup:
        def <%=entityInstance%>Business = Mock(I<%=entityClass%>Business);
        def <%=entityInstance%>Mapper = Mock(<%=entityClass%>Mapper);
        this.<%=entityInstance%>Facade.<%=entityInstance%>Business = <%=entityInstance%>Business;
        this.<%=entityInstance%>Facade.<%=entityInstance%>Mapper = <%=entityInstance%>Mapper;

        when:
        this.<%=entityInstance%>Facade.delete([] as <%=entityClass%>Dto);

        then:
        1 * <%=entityInstance%>Business.delete(_);
    }

    def "simple unit test for finding all the objects with pagination"() {

        setup:
        def <%=entityInstance%>Business = Mock(I<%=entityClass%>Business);
        def <%=entityInstance%>Mapper = Mock(<%=entityClass%>Mapper);
        this.<%=entityInstance%>Facade.<%=entityInstance%>Business = <%=entityInstance%>Business;
        this.<%=entityInstance%>Facade.<%=entityInstance%>Mapper = <%=entityInstance%>Mapper;
        PageImpl<<%=entityClass%>> page = new PageImpl<<%=entityClass%>>([[id:1L] as <%=entityClass%>, [id:2] as <%=entityClass%>],new PageRequest(2, 2), 12);

        when:
        Page<<%=entityClass%>Dto> <%=entityInstance%>Dtos = this.<%=entityInstance%>Facade.findAll(null)

        then:
        1 * <%=entityInstance%>Business.findAll(_) >> page;
        <%=entityInstance%>Dtos.getContent().size() == 2;
    }
}
