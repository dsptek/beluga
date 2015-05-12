
package org.opencloudengine.garuda.controller.mesos.marathon.model.apps.createapp.req;

import com.fasterxml.jackson.annotation.*;

import javax.annotation.Generated;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"type",
	"docker",
	"volumes"
})
public class Container {

	@JsonProperty("type")
	private String type;
	@JsonProperty("docker")
	private Docker docker;
	@JsonProperty("volumes")
	private List<Volume> volumes;
	@JsonIgnore
	private Map<String, Object> additionalProperties;

	/**
	 * @return The type
	 */
	@JsonProperty("type")
	public String getType() {
		return type;
	}

	/**
	 * @param type The type
	 */
	@JsonProperty("type")
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return The docker
	 */
	@JsonProperty("docker")
	public Docker getDocker() {
		return docker;
	}

	/**
	 * @param docker The docker
	 */
	@JsonProperty("docker")
	public void setDocker(Docker docker) {
		this.docker = docker;
	}

	/**
	 * @return The volumes
	 */
	@JsonProperty("volumes")
	public List<Volume> getVolumes() {
		return volumes;
	}

	/**
	 * @param volumes The volumes
	 */
	@JsonProperty("volumes")
	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
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
