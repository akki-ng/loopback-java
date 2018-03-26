package com.flipkart.sample.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.Relation;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.NoArgsConstructor;

/**
 * Created by akshaya.sharma on 22/03/18
 */
@Entity(name = "temp_roles")
@NoArgsConstructor
public class TempRoles extends PersistedModel<TempRoles, ModelConfigurationManager> {

  @JsonProperty("id")
  @Id //signifies the primary key
  @Column(name = "id", nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @JsonProperty("organization")
  @Column(name = "org", nullable = false, length = 50)
  private String org;

  private Integer tempId;

  private Integer roleId;

  @Override
  protected List<Relation> getRelations() {
    return null;
  }

  @Override
  public Serializable getId() {
    return id;
  }
}
