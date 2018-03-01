import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by akshaya.sharma on 01/03/18
 */

public class DWConfiguration extends Configuration {
  @JsonProperty("swagger")
  public SwaggerBundleConfiguration swaggerBundleConfiguration;

  @NotEmpty
  private String template;
  @NotEmpty
  private String defaultName = "Stranger";
  @JsonProperty
  public String getTemplate() {
    return template;
  }
  @JsonProperty
  public void setTemplate(String template) {
    this.template = template;
  }
  @JsonProperty
  public String getDefaultName() {
    return defaultName;
  }
  @JsonProperty
  public void setDefaultName(String name) {
    this.defaultName = name;
  }
}
