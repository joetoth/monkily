package com.opencalais.client.model;

public enum OutputFormat {

	RDF {
		public String toString() {
			return "XML/RDF";
		}
	},
	SIMPLE {
		public String toString() {
			return "Text/Simple";
		}
	},
	MICROFORMATS {
		public String toString() {
			return "Text/Microformats";
		}
	},
	JSON {
		public String toString() {
			return "application/json";
		}
	}

}
