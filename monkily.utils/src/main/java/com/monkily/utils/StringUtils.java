package com.monkily.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

public class StringUtils {

	public static String[][] parseTSV(String data) {

		String[] lines = data.split("\n");
		String[][] rows = new String[lines.length][];
		for (int i = 0; i < lines.length; i++) {
			rows[i] = lines[i].split("\t");
		}

		return rows;
	}

	public static String convertStreamToString(InputStream is) {

		StringBuilder out = new StringBuilder();

		try {
			final char[] buffer = new char[0x10000];
			Reader in = new InputStreamReader(is, "UTF-8");
			int read;
			do {
				read = in.read(buffer, 0, buffer.length);
				if (read > 0) {
					out.append(buffer, 0, read);
				}
			} while (read >= 0);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return out.toString();
	}

	public static Integer countWords(String str) {

		Integer wordCount = 0;

		try {
			BufferedReader br = new BufferedReader(new StringReader(str));

			String line = null;

			while ((line = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				wordCount += st.countTokens();
			}
			br.close();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return wordCount;
	}
}
