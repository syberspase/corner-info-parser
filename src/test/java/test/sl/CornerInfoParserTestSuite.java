/**
 * 
 */
package test.sl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import sl.CornerInfoParser;
import sl.CornerRecordSpec.RecordSpec;
import sl.CornerRecordSpec.RecordSpecInJavascript;
import sl.CornerRecordSpec.RecordSpecProcess;
import sl.CornerRecordSpec.TemperatureRecordSpec;
import sl.CornerRecordSpec.VoltageRecordSpec;
import sl.CornerValidationException;

/**
 * @author lius
 *
 */
public class CornerInfoParserTestSuite {
	ClassLoader classloader;
	CornerInfoParser parser;
	RecordSpec specProcess;
	RecordSpec specVoltage;
	RecordSpec specTemperature;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		classloader = ClassLoader.getSystemClassLoader();
		parser = new CornerInfoParser();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
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
	
			// Adds fields in order.
			parser.addRecord((value) -> {
				try {
					return specProcess.validate(value);
				} catch (CornerValidationException e1) {
					return false;
				}
			}); // Record process
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
			
			// Adds fields in order.
			parser.addRecord((value) -> {
				try {
					return specProcess.validate(value);
				} catch (CornerValidationException e1) {
					return false;
				}
			}); // Record process
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
			
			assertTrue(parser.isValid(classloader.getResource("CornerInfoGood.csv")));
			assertFalse(parser.isValid(classloader.getResource("CornerInfoBadProcess.csv")));
			assertFalse(parser.isValid(classloader.getResource("CornerInfoBadVoltage.csv")));
			assertFalse(parser.isValid(classloader.getResource("CornerInfoBadTemperature.csv")));
		} catch (CornerValidationException e) {
			fail("Unexpected exception:" + e.getMessage()); 
		}
	}

}
