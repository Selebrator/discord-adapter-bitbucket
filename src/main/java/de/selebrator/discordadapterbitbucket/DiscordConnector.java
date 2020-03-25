package de.selebrator.discordadapterbitbucket;

import de.selebrator.discordadapterbitbucket.model.DiscordMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordConnector {

	private final URL webhookUrl;

	public DiscordConnector(URL webhookUrl) {
		this.webhookUrl = webhookUrl;
	}

	public boolean postRequest(DiscordMessage message) throws IOException {
		Mapper mapper = new Mapper();
		String json = mapper.toJson(message);
		System.out.println("OUT " + json);
		HttpURLConnection connection = (HttpURLConnection) this.webhookUrl.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("User-Agent", "discord-adapter-bitbucket");
		connection.setRequestProperty("Content-Type", "application/json;");
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-length", String.valueOf(json.length()));
		try (OutputStream os = connection.getOutputStream()){
			os.write(json.getBytes());
			os.flush();
		} catch (IOException ex) {
			return (false);
		}
		try {
			return connection.getResponseCode() / 100 == 2;
		} catch (IOException ex) {
			return (false);
		}
	}
}
