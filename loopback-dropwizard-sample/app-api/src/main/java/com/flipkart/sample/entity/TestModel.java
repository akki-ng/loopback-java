package com.flipkart.sample.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.Relation;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity(name="test")
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class TestModel extends PersistedModel<TestModel, ModelConfigurationManager> {

  @Id
  @JsonProperty("id")
  private Long id;

  @JsonProperty("BigName")
  private String name;

  @JsonProperty("OldName")
  private String oldName;

  private String inVisible;

  @Override
  protected List<Relation> getRelations() {
    return null;
  }

  @Override
  public Serializable getId() {
    return id;
  }
}