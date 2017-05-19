package service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pojo.dbpediaspotlight.Annotation;

public class SpotlightReport {

	private final String outputPath;
	private final String header;
	private List<String> reportBody;
	private static final Logger LOG = LoggerFactory.getLogger(SpotlightReport.class);

	public SpotlightReport(String outputPath) {
		this.outputPath = outputPath;
		this.header = "Tweet|Confidence|Annotation|URI|Classes|Classes e-Commerce|Request Time(ms)";
		reportBody = new ArrayList<>();
	}

	public void append(Annotation annotation, long durationInMs) {

		annotation.getResources()
				.forEach(resources -> resources.getTypes().forEach((String namespace, Set<String> classSet) -> {
					for (String classType : classSet) {
						// Tweet,Confidence,Annotation,URI,Classes,Time To
						// Request
						final String annotatedData = String.format(Locale.US, "%s|%.2f|%s|%s|%s|%s|%d\n",
								annotation.getText(), annotation.getConfidence(), resources.getSurfaceForm(),
								resources.getURI(), namespace + ":" + classType, verifyECommerce(namespace, classType),
								durationInMs);
						reportBody.add(annotatedData);
					}
				}));
	}

	private String verifyECommerce(String namespace, String classType) {
		// TODO verificar na whitelist
		// return namespace + ":" + classType;
		return "";
	}

	public final int getBodyLineSize() {
		return reportBody.size();
	}

	public void reporting(String fileName) {
		FileWriter writer = null;
		File outputFilePath = new File(outputPath + "/" + fileName);
		try {
			if (outputFilePath.getParentFile().mkdirs()) {
				LOG.info("Directory '" + outputFilePath.getParentFile() + "' created.");
			}
			writer = new FileWriter(outputFilePath);
			writer.write(header + "\n");
			writer.write(reportBody.stream().collect(Collectors.joining("")));
		} catch (IOException e) {
			LOG.error("Error to report '" + outputFilePath + "' file.", e);
			e.printStackTrace();
		} finally {
			try {
				assert writer != null;
				writer.close();
			} catch (IOException e) {
				LOG.error("Unknown exception.", e);
				e.printStackTrace();
			}
		}
		LOG.info("Reported file '" + outputFilePath.getPath() + "' created.");
	}

}
