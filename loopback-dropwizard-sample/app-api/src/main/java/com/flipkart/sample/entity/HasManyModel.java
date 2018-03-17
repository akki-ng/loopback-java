package com.flipkart.sample.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.constants.RelationType;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.Relation;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.NoArgsConstructor;

/**
 * Created by akshaya.sharma on 15/03/18
 */
@Entity(name = "many")
@NoArgsConstructor
public class HasManyModel extends PersistedModel<HasManyModel, ModelConfigurationManager> {


  @JsonProperty("id")
  @Id //signifies the primary key
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @JsonProperty("bookName")
  @Column(name = "book", nullable = false, length = 50)
  private String book;

  @JsonProperty("tempId")
  @Column(name = "temp_id", nullable = false, length = 50)
  private Long tempId;

  @Override
  protected List<Relation> getRelations() {
    return null;
  }

  @Override
  public Serializable getId() {
    return id;
  }
}
