package test.sl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import sl.CornerInfoParser;
import sl.CornerRecordSpec.RecordSpec;
import sl.CornerRecordSpec.RecordSpecInJavascript;
import sl.CornerRecordSpec.RecordSpecProcess;
import sl.CornerRecordSpec.TemperatureRecordSpec;
import sl.CornerRecordSpec.VoltageRecordSpec;
import sl.CornerValidationException;

/**
 * CornerInfoParser TestSuite
 * @author Shep Liu(syberspase@gmail.com)
 *
 */
public class CornerInfoParserTestSuite {
    // Classloader and parser for tests
    private ClassLoader classloader;
    private CornerInfoParser parser;
    
    // Record specs
    private RecordSpec specProcess;
    private RecordSpec specVoltage;
    private RecordSpec specTemperature;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        classloader = ClassLoader.getSystemClassLoader();
        parser = new CornerInfoParser();
    }

    /**
     * Test method for {@link sl.CornerInfoParser#isValid(java.net.URL)}.
     */
    @Test
    public final void testInJavascriptSpec() {
        try {
            specProcess = new RecordSpecInJavascript();
            ((RecordSpecInJavascript) specProcess).setSpec(classloader.getResource("process.spec.js"));

            specVoltage = new RecordSpecInJavascript();
            ((RecordSpecInJavascript) specVoltage).setSpec(classloader.getResource("voltage.spec.js"));

            specTemperature = new RecordSpecInJavascript();
            ((RecordSpecInJavascript) specTemperature).setSpec(classloader.getResource("temperature.spec.js"));

            // Adds records in csv column order.
            parser.addRecord((value) -> { // Record process
                try {
                    return specProcess.validate(value);
                } catch (CornerValidationException e1) {
                    return false;
                }
            }); 
            parser.addRecord((value) -> { // Record voltage
                try {
                    return specVoltage.validate(value);
                } catch (CornerValidationException e1) {
                    return false;
                }
            });
            parser.addRecord((value) -> { // Record temperature
                try {
                    return specTemperature.validate(value);
                } catch (CornerValidationException e1) {
                    return false;
                }
            });

            // Tests different csv files, good and bad.
            assertTrue(parser.isValid(classloader.getResource("CornerInfoGood.csv")));
            assertFalse(parser.isValid(classloader.getResource("CornerInfoBadProcess.csv")));
            assertFalse(parser.isValid(classloader.getResource("CornerInfoBadVoltage.csv")));
            assertFalse(parser.isValid(classloader.getResource("CornerInfoBadTemperature.csv")));
        } catch (CornerValidationException e) {
            fail("Unexpected exception:" + e.getMessage());
        }
    }

    /**
     * Test method for {@link sl.CornerInfoParser#isValid(java.net.URL)}.
     */
    @Test
    public final void testInJavaSpec() {
        try {
            specProcess = new RecordSpecProcess();
            specVoltage = new VoltageRecordSpec();
            specTemperature = new TemperatureRecordSpec();

            // Adds records in csv column order.
            parser.addRecord((value) -> { // Record process
                try {
                    return specProcess.validate(value);
                } catch (CornerValidationException e1) {
                    return false;
                }
            }); 
            parser.addRecord((value) -> { // Record voltage
                try {
                    return specVoltage.validate(value);
                } catch (CornerValidationException e1) {
                    return false;
                }
            });
            parser.addRecord((value) -> { // Record temperature
                try {
                    return specTemperature.validate(value);
                } catch (CornerValidationException e1) {
                    return false;
                }
            });

            // Tests different csv files, good and bad.
            assertTrue(parser.isValid(classloader.getResource("CornerInfoGood.csv")));
            assertFalse(parser.isValid(classloader.getResource("CornerInfoBadProcess.csv")));
            assertFalse(parser.isValid(classloader.getResource("CornerInfoBadVoltage.csv")));
            assertFalse(parser.isValid(classloader.getResource("CornerInfoBadTemperature.csv")));
        } catch (CornerValidationException e) {
            fail("Unexpected exception:" + e.getMessage());
        }
    }

}
