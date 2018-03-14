package com.flipkart.sample.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.model.PersistedModel;
import java.io.Serializable;
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
  private Long id;

  @JsonProperty("BigName")
  private String name;

  @JsonProperty("OldName")
  private String oldName;

  private String inVisible;

  @Override
  public Serializable getId() {
    return id;
  }
}