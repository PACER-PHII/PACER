package edu.gatech.chai.fhir.fhirfilter.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import edu.gatech.chai.fhir.fhirfilter.model.FilterData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ListFilterData
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-01-16T14:28:58.456247-05:00[America/New_York]")
public class ListFilterData   {
  @JsonProperty("created")
  private String created = null;

  @JsonProperty("count")
  private Integer count = null;

  @JsonProperty("list")
  @Valid
  private List<FilterData> list = null;

  public ListFilterData created(String created) {
    this.created = created;
    return this;
  }

  /**
   * Get created
   * @return created
  **/
  @ApiModelProperty(value = "")

  public String getCreated() {
    return created;
  }

  public void setCreated(String created) {
    this.created = created;
  }

  public ListFilterData count(Integer count) {
    this.count = count;
    return this;
  }

  /**
   * Get count
   * @return count
  **/
  @ApiModelProperty(value = "")

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }

  public ListFilterData list(List<FilterData> list) {
    this.list = list;
    return this;
  }

  public ListFilterData addListItem(FilterData listItem) {
    if (this.list == null) {
      this.list = new ArrayList<FilterData>();
    }
    this.list.add(listItem);
    return this;
  }

  /**
   * Get list
   * @return list
  **/
  @ApiModelProperty(value = "")
  @Valid
  public List<FilterData> getList() {
    return list;
  }

  public void setList(List<FilterData> list) {
    this.list = list;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ListFilterData listFilterData = (ListFilterData) o;
    return Objects.equals(this.created, listFilterData.created) &&
        Objects.equals(this.count, listFilterData.count) &&
        Objects.equals(this.list, listFilterData.list);
  }

  @Override
  public int hashCode() {
    return Objects.hash(created, count, list);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ListFilterData {\n");
    
    sb.append("    created: ").append(toIndentedString(created)).append("\n");
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    list: ").append(toIndentedString(list)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
