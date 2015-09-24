package <%=packageName%>.domain;
<%
var importJsonignore = false;
for (relationshipId in relationships) {
    if (relationships[relationshipId].relationshipType == 'one-to-many') {
        importJsonignore = true;
    } else if (relationships[relationshipId].relationshipType == 'one-to-one' && relationships[relationshipId].ownerSide == false) {
        importJsonignore = true;
    } else if (relationships[relationshipId].relationshipType == 'many-to-many' && relationships[relationshipId].ownerSide == false) {
        importJsonignore = true;
    }
}
if (importJsonignore) { %>
import com.fasterxml.jackson.annotation.JsonIgnore;<% } %><% if (fieldsContainCustomTime == true) { %>
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;<% } %><% if (fieldsContainLocalDate == true) { %>
import <%=packageName%>.domain.util.CustomLocalDateSerializer;
import <%=packageName%>.domain.util.ISO8601LocalDateDeserializer;<% } %><% if (fieldsContainDateTime == true) { %>
import <%=packageName%>.domain.util.CustomDateTimeDeserializer;
import <%=packageName%>.domain.util.CustomDateTimeSerializer;<% } %><% if (fieldsContainCustomTime == true && sql == 'true') { %>
import org.hibernate.annotations.Type;<% } %><% if (fieldsContainLocalDate == true) { %>
import org.joda.time.LocalDate;<% } %><% if (fieldsContainDateTime == true) { %>
import org.joda.time.DateTime;<% } %><% if (mongo == 'yes') { %>
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;<% } %><% if (elastic == 'yes') { %>
import org.springframework.data.elasticsearch.annotations.Document;<% } %>
<% if (sql == 'yes') { %>
import javax.persistence.*;<% } %><% if (validation) { %>
import javax.validation.constraints.*;<% } %>
import java.io.Serializable;<% if (fieldsContainBigDecimal == true) { %>
import java.math.BigDecimal;<% } %><% if (fieldsContainDate == true) { %>
import java.util.Date;<% } %><% if (relationships.length > 0) { %>
import java.util.HashSet;
import java.util.Set;<% } %>
import java.util.Objects;<%
var uniqueEnums = {};
for (fieldId in fields) {
    if (fields[fieldId].fieldIsEnum && (
            !uniqueEnums[fields[fieldId].fieldType] || (uniqueEnums[fields[fieldId].fieldType] && fields[fieldId].fieldValues.length !== 0))) {
        uniqueEnums[fields[fieldId].fieldType] = fields[fieldId].fieldType;
    }
}
Object.keys(uniqueEnums).forEach(function(element) { %>

import <%=packageName%>.domain.enumeration.<%= element %>;<% }); %>
import lombok.Data;

/**
 * A <%= entityClass %>.
 */<% if (sql == 'yes') { %>
@Data
@Entity
@Table(name = "<%= entityTableName %>")<% } %><% if (mongo == 'yes') { %>
@Document(collection = "<%= entityTableName %>")<% } %><% if (elastic == 'yes') { %>
@Document(indexName="<%= entityInstance.toLowerCase() %>")<% } %>
public class <%= entityClass %> implements Serializable {
<% if (sql == 'yes') { %>
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;<% } %><% if (mongo == 'yes') { %>
    @Id
    private String id;<% } %>
<% for (fieldId in fields) { %><% if (fields[fieldId].fieldValidate == true) {
    var required = false;
    if (fields[fieldId].fieldValidate == true && fields[fieldId].fieldValidateRules.indexOf('required') != -1) {
        required = true;
    } %>
    <%- include field_validators -%>
    <% } -%>
    <% if (sql == 'yes') { %><% if (fields[fieldId].fieldIsEnum) { %>
    @Enumerated(EnumType.STRING)<% } %><% if (fields[fieldId].fieldType == 'byte[]') { %>
    @Lob<% } %><% if (fields[fieldId].fieldType == 'DateTime') { %>
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "<%=fields[fieldId].fieldNameUnderscored %>"<% if (required) { %>, nullable = false<% } %>)<% } else if (fields[fieldId].fieldType == 'LocalDate') { %>
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "<%=fields[fieldId].fieldNameUnderscored %>"<% if (required) { %>, nullable = false<% } %>)<% } else if (fields[fieldId].fieldType == 'BigDecimal') { %>
    @Column(name = "<%=fields[fieldId].fieldNameUnderscored %>", precision=10, scale=2<% if (required) { %>, nullable = false<% } %>)<% } else { %>
    @Column(name = "<%=fields[fieldId].fieldNameUnderscored %>"<% if (fields[fieldId].fieldValidate == true) { %><% if (fields[fieldId].fieldValidateRules.indexOf('maxlength') != -1) { %>, length = <%= fields[fieldId].fieldValidateRulesMaxlength %><% } %><% if (required) { %>, nullable = false<% } %><% } %>)<% } } %><% if (mongo == 'yes') { %><% if (fields[fieldId].fieldType == 'DateTime') { %>
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)<% } else if (fields[fieldId].fieldType == 'LocalDate') { %>
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)<% } %>
    @Field("<%=fields[fieldId].fieldNameUnderscored %>")<% } %>
    private <%= fields[fieldId].fieldType %> <%= fields[fieldId].fieldName %>;
<% } %><% for (relationshipId in relationships) {
    var otherEntityRelationshipName = relationships[relationshipId].otherEntityRelationshipName,
    relationshipName = relationships[relationshipId].relationshipName,
    joinTableName = entityTableName + '_'+ getTableName(relationshipName);
    if(prodDatabaseType === 'oracle' && joinTableName.length > 30) {
        joinTableName = getTableName(name.substring(0, 5)) + '_' + getTableName(relationshipName.substring(0, 5)) + '_MAPPING';
    }
    if (otherEntityRelationshipName != null) {
        mappedBy = otherEntityRelationshipName.charAt(0).toLowerCase() + otherEntityRelationshipName.slice(1)
    }
    %><% if (relationships[relationshipId].relationshipType == 'one-to-many') { %>
    @OneToMany(mappedBy = "<%= relationships[relationshipId].otherEntityRelationshipName %>")
    @JsonIgnore<% if (hibernateCache != 'no') { %>
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)<% } %>
    private Set<<%= relationships[relationshipId].otherEntityNameCapitalized %>> <%= relationships[relationshipId].relationshipFieldName %>s = new HashSet<>();<% } else if (relationships[relationshipId].relationshipType == 'many-to-one') { %>
    @ManyToOne
    private <%= relationships[relationshipId].otherEntityNameCapitalized %> <%= relationships[relationshipId].relationshipFieldName %>;<% } else if (relationships[relationshipId].relationshipType == 'many-to-many') { %>
    @ManyToMany<% if (relationships[relationshipId].ownerSide == false) { %>(mappedBy = "<%= relationships[relationshipId].otherEntityRelationshipName %>s")
    @JsonIgnore<% } %><% if (hibernateCache != 'no') { %>
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)<% } %><% if (relationships[relationshipId].ownerSide == true) { %>
    @JoinTable(name = "<%= joinTableName %>",
               joinColumns = @JoinColumn(name="<%= getColumnName(name) %>s_id", referencedColumnName="ID"),
               inverseJoinColumns = @JoinColumn(name="<%= getColumnName(relationships[relationshipId].relationshipName) %>s_id", referencedColumnName="ID"))<% } %>
    private Set<<%= relationships[relationshipId].otherEntityNameCapitalized %>> <%= relationships[relationshipId].relationshipFieldName %>s = new HashSet<>();<% } else { %>
    @OneToOne<% if (relationships[relationshipId].ownerSide == false) { %>(mappedBy = "<%= relationships[relationshipId].otherEntityRelationshipName %>")
    @JsonIgnore<% } %>
    private <%= relationships[relationshipId].otherEntityNameCapitalized %> <%= relationships[relationshipId].relationshipFieldName %>;<% } %>
<% } %>
}
