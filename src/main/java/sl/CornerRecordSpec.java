package sl;


import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class CornerRecordSpec {
	public static interface RecordSpec {
		public boolean validate(String value) throws CornerValidationException;
	}
	
	public static abstract class RecordSpecInJava implements RecordSpec {
	}
	
	public static class RecordSpecProcess extends RecordSpecInJava {
		@Override
		public boolean validate(String value) throws CornerValidationException {
			return value.length() > 2;
		}
	}
	
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
	
	public static class RecordSpecInJavascript implements RecordSpec {
		private String specIn = "value";
		private String specOut = "isValid";
		private ScriptEngine engine;
		private String script = null;
		
		public void setSpec(URL specUrl) throws CornerValidationException {
			ScriptEngineManager manager = new ScriptEngineManager();
			engine = manager.getEngineByName("JavaScript");
			try {
				script = new String(Files.readAllBytes(Paths.get(specUrl.toURI())));
			} catch (IOException e) {
				throw new CornerValidationException(e);
			} catch (URISyntaxException e) {
				throw new CornerValidationException(e);
			}
		}
		
		public boolean validate(String value) throws CornerValidationException {
			engine.put(specIn, value);
			try {
				engine.eval(script);
			} catch (ScriptException e) {
				throw new CornerValidationException(e);
			}
			return (boolean)engine.get(specOut);
		}
		
		public boolean validate(URL specUrl, String value) throws CornerValidationException {
			setSpec(specUrl);
			return validate(value);
		}
	}
}
