package com.opencalais.client.model;

public enum ContentType {

	TEXT_XML {
		public String toString() {
			return "TEXT/XML";
		}
	},
	TEXT_TXT {
		public String toString() {
			return "TEXT/TXT";
		}
	},
	TEXT_HTML {
		public String toString() {
			return "TEXT/HTML";
		}
	},
	TEXT_RAW {
		public String toString() {
			return "TEXT/RAW";
		}
	}

}
