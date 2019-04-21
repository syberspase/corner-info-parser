package sl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Package of corner record validation spec.
 * @author Shep Liu(syberspase@gmail.com)
 *
 */
public class CornerRecordSpec {
    /**
     * Interface of corner record spec for a csv record validation.
     */
    public static interface RecordSpec {
        public boolean validate(String value) throws CornerValidationException;
    }

    /**
     * Abstract corner record spec in Java for a csv record validation.
     */
    public static abstract class RecordSpecInJava implements RecordSpec {
    }

    /**
     * Corner record spec in Java for a 'process' record validation.
     * A record is valid when string value length >=2.
     */
    public static class RecordSpecProcess extends RecordSpecInJava {
        @Override
        public boolean validate(String value) throws CornerValidationException {
            return value.length() > 2;
        }
    }

    /**
     * Corner record spec in Java for a 'voltage' record validation.
     * A record is valid when value type is double.
     */
    public static class VoltageRecordSpec extends RecordSpecInJava {
        @Override
        public boolean validate(String value) throws CornerValidationException {
            try {
                Double.valueOf(value);
            } catch (NumberFormatException e) {
                throw new CornerValidationException(e);
            }
            return true;
        }
    }

    /**
     * Corner record spec in Java for a 'process' record validation.
     * A record is valid when value type is integer.
     */
    public static class TemperatureRecordSpec extends RecordSpecInJava {
        @Override
        public boolean validate(String value) throws CornerValidationException {
            try {
                Integer.valueOf(value);
            } catch (NumberFormatException e) {
                throw new CornerValidationException(e);
            }
            return true;
        }
    }

    /**
     * Corner record spec in Javascript for a csv record validation.
     */
    public static class RecordSpecInJavascript implements RecordSpec {
        // Javascript variable to pass in from Java
        private String specIn = "value";
        
        // Javascript variable to pass out to Java
        private String specOut = "isValid";

        // Script engine
        private ScriptEngine engine;
        
        // Javascript contents
        private String script = null;

        /**
         * Sets Javascript spec which is read from a js file.
         * @param specUrl
         * @throws CornerValidationException
         */
        public void setSpec(URL specUrl) throws CornerValidationException {
            ScriptEngineManager manager = new ScriptEngineManager();
            engine = manager.getEngineByName("JavaScript");
            try {
                script = new String(Files.readAllBytes(Paths.get(specUrl.toURI())));
            } catch (IOException | URISyntaxException e) {
                throw new CornerValidationException(e);
            }
        }

        /**
         * Validates value by loaded Javascript spec.
         */
        public boolean validate(String value) throws CornerValidationException {
            engine.put(specIn, value);
            try {
                engine.eval(script);
            } catch (ScriptException e) {
                throw new CornerValidationException(e);
            }
            return (boolean) engine.get(specOut);
        }

        /**
         * Sets Javascript spec and validates value by loaded the spec.
         * @param specUrl
         * @param value
         * @return
         * @throws CornerValidationException
         */
        public boolean validate(URL specUrl, String value) throws CornerValidationException {
            setSpec(specUrl);
            return validate(value);
        }
    }
}
