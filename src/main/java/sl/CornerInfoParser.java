package sl;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

import sl.CornerRecordSpec.RecordSpecInJavascript;


/**
 * Write a verification tool that reads a csv file and reports whether each row
 * contains the following expected records: The "process" record is an
 * alphanumeric string that should be of length >= 3, the "voltage" record is a
 * measurement that should be given in double precision floating point format,
 * and the "temperature" record should be an integer value. These records are
 * commonly used in characterizing electrical designs (known collectively as the
 * "PVT" or "corner"), and so it's important to ensure hypothetical design
 * engineers can have full certainty in your solution working, even with future
 * enhancements being continuously integrated.
 * 
 * Copyright text file is from
 * https://raw.githubusercontent.com/InsightSoftwareConsortium/ITK/master/Modules/ThirdParty/KWSys/src/KWSys/Copyright.txt
 * 
 * @author Shep Liu
 *
 */
public class CornerInfoParser {
	@FunctionalInterface
	public static interface InfoRecord {
		public boolean isValid(String value);
	}

	private class BoolWrapper {
		public boolean value = true;
	}


    private static final Logger LOGGER = Logger.getLogger(CornerInfoParser.class.getName());
	private List<InfoRecord> fields = new ArrayList<InfoRecord>();

	public CornerInfoParser addRecord(InfoRecord field) {
		fields.add(field);
		return this;
	}

	public boolean isValid(URL csvUrl) throws CornerValidationException {
		final BoolWrapper isValid = new BoolWrapper();
		try (Stream<String> csv = Files.lines(Paths.get(csvUrl.toURI()))) {
			csv.forEach((line) -> {
				// Ignore empty lines
				if (line.trim().length() != 0) {
					String[] fieldValues = line.split(",");
					for (int i = 0; i < fieldValues.length; i++) {
						if (!fields.get(i).isValid(fieldValues[i].trim())) {
							LOGGER.severe("Invalid csv file because of "+fieldValues[i].trim());
							isValid.value = false;
							break;
						}
					}
				}
			});
		} catch (URISyntaxException e) {
			throw new CornerValidationException(e);
		} catch (IOException e) {
			throw new CornerValidationException(e);
		}
		return isValid.value;
	}

	public static void main(String[] args) {
		CornerInfoParser parser = new CornerInfoParser();
		// Adds fields in order.
		parser.addRecord((value) -> {
			try {
				RecordSpecInJavascript spec = new RecordSpecInJavascript();
				URL url = ClassLoader.getSystemClassLoader().getResource("process.spec.js");
				spec.setSpec(url);
				return spec.validate(value);
			} catch (CornerValidationException e1) {
				return false;
			}
		}); // Record process
		parser.addRecord((value) -> { // Record voltage
			try {
				RecordSpecInJavascript spec = new RecordSpecInJavascript();
				URL url = ClassLoader.getSystemClassLoader().getResource("voltage.spec.js");
				spec.setSpec(url);
				return spec.validate(value);
			} catch (CornerValidationException e1) {
				return false;
			}
		});
		parser.addRecord((value) -> { // Record temperature
			try {
				RecordSpecInJavascript spec = new RecordSpecInJavascript();
				URL url = ClassLoader.getSystemClassLoader().getResource("temperature.spec.js");
				spec.setSpec(url);
				return spec.validate(value);
			} catch (CornerValidationException e1) {
				return false;
			}
		});
		try {
			URL url = ClassLoader.getSystemClassLoader().getResource("/src/test/resources/CornerInfo.csv");
			LOGGER.info(""+parser.isValid(url));
		} catch (CornerValidationException e) {
			LOGGER.severe(e.getMessage());
		}
	}
}