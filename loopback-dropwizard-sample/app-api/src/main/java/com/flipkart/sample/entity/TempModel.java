package com.flipkart.sample.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.constants.RelationType;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.Relation;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.NoArgsConstructor;


@Entity(name = "temp")
@NoArgsConstructor
public class TempModel extends PersistedModel<TempModel, ModelConfigurationManager> {
  private static List<Relation> relations = Lists.<Relation>newArrayList(
      Relation.builder().name("test").fromPropertyName("testId").toPropertyName(
          "id").relatedModelClass(TestModel.class).relationType(RelationType.HAS_ONE).build(),
      Relation.builder().name("many").fromPropertyName("id").toPropertyName(
          "tempId").relatedModelClass(HasManyModel.class).relationType(
          RelationType.HAS_MANY).build(),
      Relation.builder().name("roles").fromPropertyName("id").toThroughPropertyName("tempId")
      .fromThroughPropertyName("roleId").toPropertyName("id").relatedModelClass(RoleMasterModel
          .class).relationType(RelationType.HAS_MANY_THROUGH).throughModelClass(TempRoles.class)
          .build()
      );

  @JsonProperty("id")
  @Id //signifies the primary key
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id1;

  @JsonProperty("akshay")
  @Column(name = "FIRST_NAME", nullable = false, length = 50)
  private String firstName;

  @JsonProperty("lastName")
  @Column(name = "LAST_NAME", nullable = false, length = 50)
  private String lastName;

  @Override
  protected java.util.List<Relation> getRelations() {
    return relations;
  }

  @Override
  public Serializable getId() {
    return id1;
  }

//  @OneToOne(fetch = FetchType.LAZY)
//  @JoinColumn(name = "test_id")
//  @JsonProperty("test_id")
//  @JsonIgnore
//  private TestModel test;

  @JsonProperty("testId")
  @Column(name = "test_id", nullable = true, length = 50)
  private Long testId;

  public static void main(String[] args) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    TempModel m = new TempModel();
    Map<String, Serializable> modelAsMap = mapper.convertValue(m,
        new TypeReference<Map<String, Serializable>>() {
        });
    Map<String, Serializable> data = new HashMap<>();
    data.put("id", 11);
    modelAsMap.putAll(data);

    m = mapper.convertValue(modelAsMap, m.getClass());
    System.out.println(m.getId());
  }
}