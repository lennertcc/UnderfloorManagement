/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package underfloormanagement;

/**
 *
 * @author lenne
 */

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"header_identifier",
	"field_identifier",
	"name",
	"unit",
	"unit_code",
	"unitFamily",
	"rootTypeId",
	"value",
	"raw_value"
})
public class VBusLiveSystemData {

	@JsonProperty("header_identifier")
	private String headerIdentifier;
	@JsonProperty("field_identifier")
	private String fieldIdentifier;
	@JsonProperty("name")
	private String name;
	@JsonProperty("unit")
	private String unit;
	@JsonProperty("unit_code")
	private String unitCode;
	@JsonProperty("unitFamily")
	private String unitFamily;
	@JsonProperty("rootTypeId")
	private String rootTypeId;
	@JsonProperty("value")
	private String value;
	@JsonProperty("raw_value")
	private String rawValue;
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<String, Object>();

	@JsonProperty("header_identifier")
	public String getHeaderIdentifier() {
		return headerIdentifier;
	}

	@JsonProperty("header_identifier")
	public void setHeaderIdentifier(String headerIdentifier) {
		this.headerIdentifier = headerIdentifier;
	}

	@JsonProperty("field_identifier")
	public String getFieldIdentifier() {
		return fieldIdentifier;
	}

	@JsonProperty("field_identifier")
	public void setFieldIdentifier(String fieldIdentifier) {
		this.fieldIdentifier = fieldIdentifier;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("unit")
	public String getUnit() {
		return unit;
	}

	@JsonProperty("unit")
	public void setUnit(String unit) {
		this.unit = unit;
	}

	@JsonProperty("unit_code")
	public String getUnitCode() {
		return unitCode;
	}

	@JsonProperty("unit_code")
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	@JsonProperty("unitFamily")
	public String getUnitFamily() {
		return unitFamily;
	}

	@JsonProperty("unitFamily")
	public void setUnitFamily(String unitFamily) {
		this.unitFamily = unitFamily;
	}

	@JsonProperty("rootTypeId")
	public String getRootTypeId() {
		return rootTypeId;
	}

	@JsonProperty("rootTypeId")
	public void setRootTypeId(String rootTypeId) {
		this.rootTypeId = rootTypeId;
	}

	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	@JsonProperty("value")
	public void setValue(String value) {
		this.value = value;
	}

	@JsonProperty("raw_value")
	public String getRawValue() {
		return rawValue;
	}

	@JsonProperty("raw_value")
	public void setRawValue(String rawValue) {
		this.rawValue = rawValue;
	}

	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	@JsonAnySetter
	public void setAdditionalProperty(String name, Object value) {
		this.additionalProperties.put(name, value);
	}

}