package com.monkily.content.test;

import com.monkily.content.internal.service.impl.ExtractionServiceImpl;
import com.monkily.content.model.ExtractedContent;
import com.monkily.content.service.ExtractionService;

public class ExtractionServiceTest {

	public static void main(String[] args) {
		ExtractionService extractionService = new ExtractionServiceImpl();
		ExtractedContent extractedContent = extractionService
				.extractContent("http://money.cnn.com/2009/06/01/smallbusiness/digg_this.fsb/index.htm");
		// System.out.println(new String(extractedContent.getData()));
	}

}
