package fr.groom;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.groom.configuration.InstrumenterConfiguration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class JsonConfigurationParser {
	private InputStream jsonStream;

	private JsonConfigurationParser(InputStream jsonStream) {
		this.jsonStream = jsonStream;
	}

	public static JsonConfigurationParser fromStream(InputStream inputStream) {
		JsonConfigurationParser jcp = new JsonConfigurationParser(inputStream);
		return jcp;
	}

	public static JsonConfigurationParser fromFile(String filename) throws FileNotFoundException {
		FileInputStream inputStream = new FileInputStream(filename);
		return fromStream(inputStream);
	}

	public InstrumenterConfiguration parse() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(this.jsonStream, InstrumenterConfiguration.class);
	}
}
