package entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.model.PersistedModel;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.NoArgsConstructor;


@Entity(name = "temp")
@NoArgsConstructor
public class TempModel extends PersistedModel<TempModel, ModelConfigurationManager> {


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
  public Object getId() {
    return id1;
  }

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id")
  private TestModel test;
}
