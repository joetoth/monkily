package com.monkily.content.internal.service.impl;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.monkily.content.model.ContentType;
import com.monkily.content.model.ExtractedContent;
import com.monkily.content.service.ExtractionService;
import com.monkily.utils.HttpUtils;
import com.monkily.utils.StringUtils;

@Service
public class ExtractionServiceHtmlBytesImpl implements ExtractionService {

	public static class HtmlStatistic {
		Integer htmlChars = 0;
		Integer textChars = 0;
		Integer lineLength = 0;
		Double density = 0.0d;
		String text = "";

		public Integer getHtmlChars() {
			return htmlChars;
		}

		public void setHtmlChars(Integer htmlChars) {
			this.htmlChars = htmlChars;
		}

		public Integer getTextChars() {
			return textChars;
		}

		public void setTextChars(Integer textChars) {
			this.textChars = textChars;
		}

		public Integer getLineLength() {
			return lineLength;
		}

		public void setLineLength(Integer lineLength) {
			this.lineLength = lineLength;
		}

		public Double getDensity() {
			return density;
		}

		public void setDensity(Double density) {
			this.density = density;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

	}

	@Override
	public ExtractedContent extractContent(String url) {

		InputStream stream = HttpUtils.get(url);
		String html = StringUtils.convertStreamToString(stream); // HtmlUtils.stripTags(html);

		System.out.println(html);

		Map<Integer, HtmlStatistic> htmlMap = new HashMap<Integer, HtmlStatistic>();

		String[] lines = html.split("\n");
		for (int i = 0; i < lines.length; i++) {

			HtmlStatistic stat = parseHtmlLine(lines[i]);

			htmlMap.put(i, stat);
		}

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Integer, HtmlStatistic> stat : htmlMap.entrySet()) {
			if (stat.getValue().getDensity() > 0.5) {
				sb.append(stat.getValue().getText());
			}
		}

		System.out.println(sb.toString());

		ExtractedContent ec = new ExtractedContent();
		ec.setContentType(ContentType.Text);
		ec.setData(html.getBytes());
		ec.setDescription("[Description]");
		ec.setPublishedDate(new Date());
		ec.setTitle("[Title]");

		return ec;
	}

	private static HtmlStatistic parseHtmlLine(String line) {

		HtmlStatistic stat = new HtmlStatistic();

		char[] chars = line.toCharArray();
		boolean insideTag = false;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '<') {
				insideTag = true;
				stat.setHtmlChars(stat.getHtmlChars() + 1);
			} else if (chars[i] == '>') {
				insideTag = false;
				stat.setHtmlChars(stat.getHtmlChars() + 1);
			} else {
				if (insideTag) {
					stat.setHtmlChars(stat.getHtmlChars() + 1);
				} else {
					stat.setText(stat.getText() + chars[i]);
					stat.setTextChars(stat.getTextChars() + 1);
				}
			}
		}

		stat.setDensity((double) stat.getTextChars() / (double) chars.length);

		System.out.println(line);
		System.out.println(stat.getDensity());

		return stat;
	}

	private Double average(Collection<Double> numbers) {

		Double average = 0.0d;

		for (Double number : numbers) {
			average += number;
		}

		average = average / numbers.size();

		return average;
	}

}
