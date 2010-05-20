package com.opencalais.client.model;

import java.util.HashMap;
import java.util.Map;

public class Metadata {

	private Map<String, MetadataElement> metadataElements = new HashMap<String, MetadataElement>();

	private Map<String, Topic> topics = new HashMap<String, Topic>();

	public void setMetadataElement(String name, MetadataElement metadataElement) {
		metadataElements.put(name, metadataElement);
	}

	public Map<String, MetadataElement> getMetadataElements() {
		return metadataElements;
	}

	public void setMetadataElements(
			Map<String, MetadataElement> metadataElements) {
		this.metadataElements = metadataElements;
	}

	public void setTopic(String name, Topic topic) {
		topics.put(name, topic);
	}

	public Map<String, Topic> getTopics() {
		return topics;
	}

	public void setTopics(Map<String, Topic> topics) {
		this.topics = topics;
	}

}
