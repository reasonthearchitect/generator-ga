package <%=packageName%>.test.web.rest;

import <%=packageName%>.web.rest.<%=entityClass%>Rest
import <%=packageName%>.facade.I<%=entityClass%>Facade
import <%=packageName%>.facade.dto.<%=entityClass%>Dto
import org.springframework.http.HttpStatus

import spock.lang.Specification

public class <%=entityClass%>RestUnitSpec extends Specification {

	<%=entityClass%>Rest <%=entityInstance%>Rest;

	def setup() {
		this.<%=entityInstance%>Rest = new <%=entityClass%>Rest();
	}

	def "simple unit test for creating an entity through rest"() {

		setup:
		def <%=entityInstance%>Facade = Mock(I<%=entityClass%>Facade)
		this.<%=entityInstance%>Rest.<%=entityInstance%>Facade = <%=entityInstance%>Facade;
		def <%=entityInstance%>Dto = [id:1L] as <%=entityClass%>Dto;

		when:
		def response = this.<%=entityInstance%>Rest.create<%=entityClass%>([] as <%=entityClass%>Dto);

		then: 
		1 * <%=entityInstance%>Facade.save(_) >> <%=entityInstance%>Dto;
		response != null;
		response.getBody().id == 1L;
	}

	def "make sure that we cannot post an entity with an id"() {

		setup:
		def <%=entityInstance%>Facade = Mock(I<%=entityClass%>Facade)
		this.<%=entityInstance%>Rest.<%=entityInstance%>Facade = <%=entityInstance%>Facade;

		when:
		def response = this.<%=entityInstance%>Rest.create<%=entityClass%>([id:1L] as <%=entityClass%>Dto);

		then:
		0 * <%=entityInstance%>Facade.save(_);
		response != null;
		response.getBody() == null;
	}

	def "make sure that we CAN update an entity withOUT an id"() {

		setup:
		def <%=entityInstance%>Facade = Mock(I<%=entityClass%>Facade)
		this.<%=entityInstance%>Rest.<%=entityInstance%>Facade = <%=entityInstance%>Facade;
		def <%=entityInstance%>Dto = [id:1L] as <%=entityClass%>Dto;

		when:
		def response = this.<%=entityInstance%>Rest.update<%=entityClass%>([] as <%=entityClass%>Dto);

		then:
		1 * <%=entityInstance%>Facade.save(_) >> <%=entityInstance%>Dto;
		response != null;
		response.getBody().id == 1L;
	}

	def "make sure that we CAN update an entity with an id"() {

		setup:
		def <%=entityInstance%>Facade = Mock(I<%=entityClass%>Facade)
		this.<%=entityInstance%>Rest.<%=entityInstance%>Facade = <%=entityInstance%>Facade;
		def <%=entityInstance%>Dto = [id:1L] as <%=entityClass%>Dto;

		when:
		def response = this.<%=entityInstance%>Rest.update<%=entityClass%>([] as <%=entityClass%>Dto);

		then:
		1 * <%=entityInstance%>Facade.save(_) >> <%=entityInstance%>Dto;
		response != null;
		response.getBody().id == 1L;
	}

	def "delete an entity through the rest Api"(){

		setup:
		def <%=entityInstance%>Facade = Mock(I<%=entityClass%>Facade)
		this.<%=entityInstance%>Rest.<%=entityInstance%>Facade = <%=entityInstance%>Facade;

		when:
		def result = this.<%=entityInstance%>Rest.delete<%=entityClass%>(1L);

		then:
		1 * <%=entityInstance%>Facade.delete(_)
		result != null;
	}
}