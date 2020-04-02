package de.selebrator.discordadapterbitbucket;

import de.selebrator.discordadapterbitbucket.model.DiscordMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		try (OutputStream os = connection.getOutputStream()) {
			os.write(json.getBytes());
			os.flush();
		} catch (IOException ex) {
			System.err.println("REQUEST ERROR");
			ex.printStackTrace();
			System.out.println("END OF ERROR");
			return (false);
		}
		try {
			boolean ok = connection.getResponseCode() / 100 == 2;
			if (!ok) {
				System.err.println("RESPONSE ERROR");
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
					System.err.println("Input Stream: " + reader.lines());
				} catch (Exception e) {
					e.printStackTrace();
				}
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
					System.err.println("Error Stream : " + reader.lines());
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.out.println("END OF ERROR");
			}
			return ok;
		} catch (IOException ex) {
			return (false);
		}
	}
}
