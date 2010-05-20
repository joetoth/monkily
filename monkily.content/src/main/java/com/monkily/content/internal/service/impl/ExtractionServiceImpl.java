package com.monkily.content.internal.service.impl;

import java.io.InputStream;
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
public class ExtractionServiceImpl implements ExtractionService {

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
		String html = StringUtils.convertStreamToString(stream);
//		html = clean(html);

		Map<Integer, HtmlStatistic> htmlMap = new HashMap<Integer, HtmlStatistic>();

		String[] lines = html.split("\n");
		for (int i = 0; i < lines.length; i++) {

			HtmlStatistic stat = parseHtmlLine(lines[i]);

			htmlMap.put(i, stat);
		}

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<Integer, HtmlStatistic> stat : htmlMap.entrySet()) {

			Double referenceDensity = calculateReferenceDensity(stat.getKey(),
					htmlMap);

			if (referenceDensity > 0.5) {
				sb.append(stat.getValue().getText());
			}
		}

		html = sb.toString();
		System.out.println(html);

		ExtractedContent ec = new ExtractedContent();
		ec.setContentType(ContentType.Text);
		ec.setData(html.getBytes());
		ec.setDescription("[Description]");
		ec.setPublishedDate(new Date());
		ec.setTitle("[Title]");

		return ec;
	}

	private Double calculateReferenceDensity(Integer line,
			Map<Integer, HtmlStatistic> htmlMap) {
		Double density = 0.0d;
		Integer lineNumber = line - 1;

		for (int i = 0; i < 3; i++) {
			if (lineNumber >= 0 && lineNumber < htmlMap.size()) {
				HtmlStatistic stat = htmlMap.get(lineNumber);
				density += stat.getDensity();
			}

			lineNumber++;
		}

		return density / 3d;
	}

	private HtmlStatistic parseHtmlLine(String line) {
		line = line.trim();

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

		if (stat.getTextChars().equals(0)) {
			stat.setDensity(0d);
		} else if (stat.getHtmlChars().equals(0)) {
			stat.setDensity(1d);
		} else {
			stat.setDensity((double) stat.getTextChars()
					/ (double) chars.length);
		}

		// System.out.println(line);
		// System.out.println(stat.getDensity());

		return stat;
	}

//	private String clean(String sourceText) {
//
//		// create an instance of HtmlCleaner
//		HtmlCleaner cleaner = new HtmlCleaner();
//
//		// take default cleaner properties
//		CleanerProperties props = cleaner.getProperties();
//
//		// customize cleaner's behaviour with property setters
//		// props.setXXX(...);
//
//		// Clean HTML taken from simple string, file, URL, input stream,
//		// input source or reader. Result is root node of created
//		// tree-like structure. Single cleaner instance may be safely used
//		// multiple times.
//		String html = null;
//
//		try {
//			TagNode node = cleaner.clean(sourceText);
//			removeFromTree(node, "script");
//			removeFromTree(node, "object");
//			removeFromTree(node, "applet");
//			removeFromTree(node, "style");
//
//			html = new PrettyXmlSerializer(props).getXmlAsString(node);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//
//		return html;
//	}
//
//	private void removeFromTree(TagNode node, String tagName) {
//		TagNode[] nodes = node.getElementsByName(tagName, true);
//		for (int i = 0; i < nodes.length; i++) {
//			nodes[i].removeFromTree();
//		}
//	}

}