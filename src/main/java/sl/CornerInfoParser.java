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
 * @author Shep Liu(syberspase@gmail.com)
 *
 */
public class CornerInfoParser {
    /**
     * Record interface. Parser has a list of Records with implemented isValid().
     */
    @FunctionalInterface
    public static interface InfoRecord {
        public boolean isValid(String value);
    }

    /**
     * Boolean wrapper for stream iteration.
     */
    private class BoolWrapper {
        public boolean value = true;
    }

    private static final Logger LOGGER = Logger.getLogger(CornerInfoParser.class.getName());
    private List<InfoRecord> records = new ArrayList<InfoRecord>();

    /**
     * Adds record to csv parser, in order of csv columns.
     * @param record
     * @return
     */
    public CornerInfoParser addRecord(InfoRecord record) {
        records.add(record);
        return this;
    }

    /**
     * Validates a csv by list of record specifications.
     * @param csvUrl
     * @return
     * @throws CornerValidationException
     */
    public boolean isValid(URL csvUrl) throws CornerValidationException {
        final BoolWrapper isValid = new BoolWrapper();
        try (Stream<String> csv = Files.lines(Paths.get(csvUrl.toURI()))) {
            csv.forEach((line) -> {
                // Ignore empty lines
                if (line.trim().length() != 0) {
                    String[] fieldValues = line.split(",");
                    for (int i = 0; i < fieldValues.length; i++) {
                        if (!records.get(i).isValid(fieldValues[i].trim())) {
                            LOGGER.severe("Invalid csv file because of " + fieldValues[i].trim());
                            isValid.value = false;
                            break;
                        }
                    }
                }
            });
        } catch (URISyntaxException | IOException e) {
            throw new CornerValidationException(e);
        }
        return isValid.value;
    }
}
