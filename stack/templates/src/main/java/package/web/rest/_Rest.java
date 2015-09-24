package <%=packageName%>.web.rest;


import com.codahale.metrics.annotation.Timed;
import <%=packageNameGenerated%>.web.rest.util.HeaderUtil;
import <%=packageName%>.facade.dto.<%=entityClass%>Dto;
import <%=packageName%>.facade.I<%=entityClass%>Facade;
import <%=packageNameGenerated%>.web.rest.util.PaginationUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class <%=entityClass%>Rest {

	@Inject
	private I<%=entityClass%>Facade <%=entityInstance%>Facade;

	@RequestMapping(value = "/<%=entityInstance%>s",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<<%=entityClass%>Dto> create<%=entityClass%>(@RequestBody <%=entityClass%>Dto <%=entityInstance%>Dto) throws URISyntaxException {
    	if (<%=entityInstance%>Dto.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new <%=entityInstance%> cannot already have an ID").body(null);
        }
        <%=entityClass%>Dto result = this.<%=entityInstance%>Facade.save(<%=entityInstance%>Dto);
        return ResponseEntity.created(new URI("/api/<%=entityInstance%>s/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert("<%=entityInstance%>", result.getId().toString()))
                .body(result);
    }

    @RequestMapping(value = "/<%=entityInstance%>s",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<<%=entityClass%>Dto> update<%=entityClass%>(@RequestBody <%=entityClass%>Dto <%=entityInstance%>Dto) throws URISyntaxException {
        //log.debug("REST request to update <%=entityClass%> : {}", birdDTO);
        if (<%=entityInstance%>Dto.getId() == null) {
            return create<%=entityClass%>(<%=entityInstance%>Dto);
        }
        <%=entityClass%>Dto result = this.<%=entityInstance%>Facade.save(<%=entityInstance%>Dto);
        return ResponseEntity.ok()
        		.headers(HeaderUtil.createEntityUpdateAlert("<%=entityInstance%>", result.getId().toString()))
                .body(result);
    }

    @RequestMapping(value = "/<%=entityInstance%>s/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> delete<%=entityClass%>(@PathVariable Long id) {
        <%=entityClass%>Dto dto = new <%=entityClass%>Dto();
        dto.setId(id);
        this.<%=entityInstance%>Facade.delete(dto);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("<%=entityInstance%>", id.toString())).build();
    }

    @RequestMapping(value = "/<%=entityInstance%>s/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<<%=entityClass%>Dto> get<%=entityClass%>(@PathVariable Long id) {
        return Optional.ofNullable(new ResponseEntity<>(<%=entityInstance%>Facade.findOne(id),
                HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /cats -> get all the cats.
     */
    @RequestMapping(value = "/<%=entityInstance%>s",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<<%=entityClass%>Dto>> getAll<%=entityClass%>s(Pageable pageable)
            throws URISyntaxException {
        Page<<%=entityClass%>Dto> <%=entityInstance%>s = this.<%=entityInstance%>Facade.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(<%=entityInstance%>s, "/api/<%=entityInstance%>s");
        return new ResponseEntity<>(<%=entityInstance%>s.getContent(), headers, HttpStatus.OK);
    }
}