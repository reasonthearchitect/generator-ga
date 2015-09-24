package <%=packageName%>.repository;

import <%=packageName%>.domain.<%=entityClass%>;
import org.springframework.data.jpa.repository.*;<% if (fieldsContainOwnerManyToMany==true) { %>
import org.springframework.data.repository.query.Param;<% } %>

import java.util.List;

/**
 * Spring Data JPA repository for the <%= entityClass %> entity.
 */
public interface I<%=entityClass%>Repository extends JpaRepository<<%=entityClass%>,Long> {<% for (relationshipId in relationships) { %><% if (relationships[relationshipId].relationshipType == 'many-to-one' && relationships[relationshipId].otherEntityName == 'user') { %>

    @Query("select <%= entityInstance %> from <%= entityClass %> <%= entityInstance %> where <%= entityInstance %>.<%= relationships[relationshipId].relationshipFieldName %>.login = ?#{principal.username}")
    List<<%= entityClass %>> findBy<%= relationships[relationshipId].relationshipNameCapitalized %>IsCurrentUser();<% } } %>
<% if (fieldsContainOwnerManyToMany==true) { %>
    @Query("select distinct <%= entityInstance %> from <%= entityClass %> <%= entityInstance %><% for (relationshipId in relationships) {
    if (relationships[relationshipId].relationshipType == 'many-to-many' && relationships[relationshipId].ownerSide == true) { %> left join fetch <%=entityInstance%>.<%=relationships[relationshipId].relationshipFieldName%>s<%} }%>")
    List<<%=entityClass%>> findAllWithEagerRelationships();

    @Query("select <%= entityInstance %> from <%= entityClass %> <%= entityInstance %><% for (relationshipId in relationships) {
    if (relationships[relationshipId].relationshipType == 'many-to-many' && relationships[relationshipId].ownerSide == true) { %> left join fetch <%=entityInstance%>.<%=relationships[relationshipId].relationshipFieldName%>s<%} }%> where <%=entityInstance%>.id =:id")
    <%=entityClass%> findOneWithEagerRelationships(@Param("id") Long id);
<% } %>
}