package edu.gatech.chai.fhir.fhirfilter.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * FilterData
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2019-01-16T14:28:58.456247-05:00[America/New_York]")
public class FilterData   {
  @JsonProperty("id")
  private Integer id = null;

  @JsonProperty("profileName")
  private String profileName = null;

  @JsonProperty("entryToRemove")
  @Valid
  private List<Object> entryToRemove = null;

  public FilterData id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  **/
  @ApiModelProperty(value = "")

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public FilterData profileName(String profileName) {
    this.profileName = profileName;
    return this;
  }

  /**
   * Get profileName
   * @return profileName
  **/
  @ApiModelProperty(value = "")

  public String getProfileName() {
    return profileName;
  }

  public void setProfileName(String profileName) {
    this.profileName = profileName;
  }

  public FilterData entryToRemove(List<Object> entryToRemove) {
    this.entryToRemove = entryToRemove;
    return this;
  }

  public FilterData addEntryToRemoveItem(Object entryToRemoveItem) {
    if (this.entryToRemove == null) {
      this.entryToRemove = new ArrayList<Object>();
    }
    this.entryToRemove.add(entryToRemoveItem);
    return this;
  }

  /**
   * Get entryToRemove
   * @return entryToRemove
  **/
  @ApiModelProperty(value = "")

  public List<Object> getEntryToRemove() {
    return entryToRemove;
  }

  public void setEntryToRemove(List<Object> entryToRemove) {
    this.entryToRemove = entryToRemove;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FilterData filterData = (FilterData) o;
    return Objects.equals(this.id, filterData.id) &&
        Objects.equals(this.profileName, filterData.profileName) &&
        Objects.equals(this.entryToRemove, filterData.entryToRemove);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, profileName, entryToRemove);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class FilterData {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    profileName: ").append(toIndentedString(profileName)).append("\n");
    sb.append("    entryToRemove: ").append(toIndentedString(entryToRemove)).append("\n");
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
