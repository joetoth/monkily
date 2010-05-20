package com.monkily.utils;

import java.util.UUID;

public abstract class GUID {

	/**
	 * Generates a Google App Engine compatible UUID. The first character cannot
	 * be a number. If the UUID class generates a number first it is replaced
	 * with a character.
	 */
	public static String generate() {
		String uuid = UUID.randomUUID().toString();

		char firstChar = uuid.charAt(0);

		if (Character.isDigit(firstChar)) {
			switch (firstChar) {
			case '0':
				firstChar = 'z';
				break;
			case '1':
				firstChar = 'y';
				break;
			case '2':
				firstChar = 'x';
				break;
			case '3':
				firstChar = 'w';
				break;
			case '4':
				firstChar = 'v';
				break;
			case '5':
				firstChar = 'u';
				break;
			case '6':
				firstChar = 't';
				break;
			case '7':
				firstChar = 's';
				break;
			case '8':
				firstChar = 'r';
				break;
			case '9':
				firstChar = 'q';
				break;
			}
		}

		return firstChar + uuid.substring(1);
	}

	public static void main(String[] args) {
		System.out.println(generate());
	}

}
