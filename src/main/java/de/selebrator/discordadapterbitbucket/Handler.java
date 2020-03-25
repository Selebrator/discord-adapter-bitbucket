package de.selebrator.discordadapterbitbucket;

import com.sun.net.httpserver.HttpServer;
import de.selebrator.discordadapterbitbucket.model.DiscordMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Handler {

	private final URL webhookUrl;

	public Handler(URL webhookUrl) {
		this.webhookUrl = webhookUrl;
	}

	public static void main(String[] args) throws IOException {
		if (args.length < 1) {
			System.err.println("No discord webhook provided.");
			System.exit(1);
		}
		URL discordWebhookUrl = new URL(args[0]);
		HttpServer server = HttpServer.create(new InetSocketAddress(6668), 0);
		Handler handler = new Handler(discordWebhookUrl);
		server.createContext("/bitbucket-to-discord", exchange -> {
			boolean result = handler.handleRequest(exchange.getRequestBody());
			exchange.sendResponseHeaders(result ? 204 : 400, -1);
			exchange.close();
		});
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	public boolean handleRequest(InputStream inputStream) throws IOException {
		try {
			Mapper mapper = new Mapper();
			String json = this.convertInputStream(inputStream);
			System.out.println("IN  " + json);
			Optional<DiscordMessage> message = mapper.bitbucketToDiscord(json);
			if (message.isPresent()) {
				DiscordConnector connector = new DiscordConnector(this.webhookUrl);
				return connector.postRequest(message.get());
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private String convertInputStream(InputStream inputStream) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
			return br.lines().collect(Collectors.joining(System.lineSeparator()));
		}
	}
}
