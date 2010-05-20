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
public class ExtractionServiceTextImpl implements ExtractionService {

	@Override
	public ExtractedContent extractContent(String url) {

		InputStream stream = HttpUtils.get(url);
		String text = StringUtils.convertStreamToString(stream); // HtmlUtils.stripTags(html);

		// Count line characters
		Map<Integer, Integer> lineCountMap = new HashMap<Integer, Integer>();
		Integer largestLineCount = 0;

		String[] lines = text.split("\n");
		for (int i = 0; i < lines.length; i++) {
			Integer lineCount = lines[i].length();
			if (lineCount > largestLineCount) {
				largestLineCount = lineCount;
			}
			lineCountMap.put(i, lineCount);
		}

		// Calculate line density
		Map<Integer, Double> lineDensityMap = new HashMap<Integer, Double>();
		Double largestLineDensity = 0.0d;
		Double averageLineDensity = 0.0d;

		for (int i = 0; i < lines.length; i++) {

			Double lineDensity = 0.0d;

			for (int j = 0; j < lines.length; j++) {

				lineDensity += lineCountMap.get(j).doubleValue()
						/ largestLineCount.doubleValue()
						/ ((Math.abs(i - j) + 1));
			}

			if (lineDensity > largestLineDensity) {
				largestLineDensity = lineDensity;
			}

			averageLineDensity += lineDensity;
			lineDensityMap.put(i, lineDensity);
		}

		// TODO: remove outlier from the average line density calculation

		// Calculate average line density
		averageLineDensity = averageLineDensity / (double) lines.length;

		// Remove outliers
		// Double high = averageLineDensity * 1.5;
		// Double low = averageLineDensity * 0.5;
		//
		// List<Integer> linesToRemove = new ArrayList<Integer>();
		//
		// for (int i = 0; i < lines.length; i++) {
		//
		// if (lineDensityMap.get(i) < low) {
		// System.out.println(lineDensityMap.get(i));
		// linesToRemove.add(i);
		// }
		// }
		//
		// for (Integer lineNumber : linesToRemove) {
		// lineDensityMap.remove(lineNumber);
		// }
		//
		// // Recalculate average
		// averageLineDensity = average(lineDensityMap.values());

		System.out.println("avg line dens: " + averageLineDensity);

		// Return lines greater than average line density
		StringBuilder sb = new StringBuilder();

		for (Integer lineNumber : lineDensityMap.keySet()) {

			Double lineDensity = lineDensityMap.get(lineNumber);

			System.out.println(lineDensity + "\t" + lines[lineNumber]);

			if (lineDensity >= averageLineDensity) {
				System.out.println("OK!");
				sb.append(lines[lineNumber] + "\n");
			}
		}

		String data = sb.toString();

		// Line Density: lineCount / largestLineCount / abs(lineRowNumber -
		// otherRowNumber)

		// determine text density
		//		
		// plot densities on a x vs. y graph
		//		
		// count characters on each line
		//		
		// line with most characters is baseline
		//	
		// determine placement density
		//		
		// line density = chars_on_line / MOST_CHARS_LINE
		// relative line density = for each line add up ...( line density /
		// lines away from line in question )

		ExtractedContent ec = new ExtractedContent();
		ec.setContentType(ContentType.Text);
		ec.setData(data.getBytes());
		ec.setDescription("[Description]");
		ec.setPublishedDate(new Date());
		ec.setTitle("[Title]");

		return ec;
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
