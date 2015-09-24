package <%=packageName%>.facade.dto;
<% if (fieldsContainLocalDate == true) { %>
import org.joda.time.LocalDate;<% } %><% if (fieldsContainDateTime == true) { %>
import org.joda.time.DateTime;<% } %><% if (validation) { %>
import javax.validation.constraints.*;<% } %>
import java.io.Serializable;<% if (fieldsContainBigDecimal == true) { %>
import java.math.BigDecimal;<% } %><% if (fieldsContainDate == true) { %>
import java.util.Date;<% } %><% if (relationships.length > 0) { %>
import java.util.HashSet;
import java.util.Set;<% } %>
import java.util.Objects;<% if (fieldsContainBlob == true) { %>
import javax.persistence.Lob;<% } %>
<% for (fieldId in fields) { if (fields[fieldId].fieldIsEnum == true) { %>
import <%=packageName%>.domain.enumeration.<%= fields[fieldId].fieldType %>;<% } } %>

import lombok.Data;

/**
 * A DTO for the <%= entityClass %> entity.
 */
@Data
public class <%= entityClass %>Dto implements Serializable {

    private Long id;<% for (fieldId in fields) { %>
<% if (fields[fieldId].fieldValidate == true) {
    var required = false;
    if (fields[fieldId].fieldValidate == true && fields[fieldId].fieldValidateRules.indexOf('required') != -1) {
        required = true;
    }
    if (required) { %>
    @NotNull<% } %><% if (fields[fieldId].fieldValidateRules.indexOf('minlength') != -1 && fields[fieldId].fieldValidateRules.indexOf('maxlength') == -1) { %>
    @Size(min = <%= fields[fieldId].fieldValidateRulesMinlength %>)<% } %><% if (fields[fieldId].fieldValidateRules.indexOf('maxlength') != -1 && fields[fieldId].fieldValidateRules.indexOf('minlength') == -1) { %>
    @Size(max = <%= fields[fieldId].fieldValidateRulesMaxlength %>)<% } %><% if (fields[fieldId].fieldValidateRules.indexOf('minlength') != -1 && fields[fieldId].fieldValidateRules.indexOf('maxlength') != -1) { %>
    @Size(min = <%= fields[fieldId].fieldValidateRulesMinlength %>, max = <%= fields[fieldId].fieldValidateRulesMaxlength %>)<% } %><% if (fields[fieldId].fieldValidateRules.indexOf('minbytes') != -1 && fields[fieldId].fieldValidateRules.indexOf('maxbytes') == -1) { %>
    @Size(min = <%= fields[fieldId].fieldValidateRulesMinbytes %>)<% } %><% if (fields[fieldId].fieldValidateRules.indexOf('maxbytes') != -1 && fields[fieldId].fieldValidateRules.indexOf('minbytes') == -1) { %>
    @Size(max = <%= fields[fieldId].fieldValidateRulesMaxbytes %>)<% } %><% if (fields[fieldId].fieldValidateRules.indexOf('minbytes') != -1 && fields[fieldId].fieldValidateRules.indexOf('maxbytes') != -1) { %>
    @Size(min = <%= fields[fieldId].fieldValidateRulesMinbytes %>, max = <%= fields[fieldId].fieldValidateRulesMaxbytes %>)<% } %><% if (fields[fieldId].fieldValidateRules.indexOf('min') != -1) { %>
    @Min(value = <%= fields[fieldId].fieldValidateRulesMin %>)<% } %><% if (fields[fieldId].fieldValidateRules.indexOf('max') != -1) { %>
    @Max(value = <%= fields[fieldId].fieldValidateRulesMax %>)<% } %><% if (fields[fieldId].fieldValidateRules.indexOf('pattern') != -1) { %>
    @Pattern(regexp = "<%= fields[fieldId].fieldValidateRulesPatternJava %>")<% } } %><% if (fields[fieldId].fieldType == 'byte[]') { %>
    @Lob<% } %>
    private <%= fields[fieldId].fieldType %> <%= fields[fieldId].fieldName %>;<% } %><% for (relationshipId in relationships) {
    otherEntityRelationshipName = relationships[relationshipId].otherEntityRelationshipName;%><% if (relationships[relationshipId].relationshipType == 'many-to-many' && relationships[relationshipId].ownerSide == true) { %>
    private Set<<%= relationships[relationshipId].otherEntityNameCapitalized %>DTO> <%= relationships[relationshipId].relationshipFieldName %>s = new HashSet<>();<% } else if (relationships[relationshipId].relationshipType == 'many-to-one' || (relationships[relationshipId].relationshipType == 'one-to-one' && relationships[relationshipId].ownerSide == true)) { %>

    private Long <%= relationships[relationshipId].relationshipFieldName %>Id;<% if (relationships[relationshipId].otherEntityFieldCapitalized !='Id' && relationships[relationshipId].otherEntityFieldCapitalized != '') { %>

    private String <%= relationships[relationshipId].relationshipFieldName %><%= relationships[relationshipId].otherEntityFieldCapitalized %>;<% } } } %>
}
