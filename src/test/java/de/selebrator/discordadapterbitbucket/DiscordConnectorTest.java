package de.selebrator.discordadapterbitbucket;

import de.selebrator.discordadapterbitbucket.model.DiscordMessage;
import de.selebrator.discordadapterbitbucket.model.DiscordMessageEmbed;
import de.selebrator.discordadapterbitbucket.model.DiscordMessageFooter;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class DiscordConnectorTest {
	public static void postRequest(String url) throws IOException {
		DiscordConnector conn = new DiscordConnector(new URL(url));
		DiscordMessage message = new DiscordMessage();
		DiscordMessageEmbed embed = new DiscordMessageEmbed();
		DiscordMessageFooter footer = new DiscordMessageFooter();
		message.setContent("Content");
		footer.setText("Footer");
		embed.setFooter(footer);
		embed.setColor(111);
		embed.setUrl("http://www.google.com");
		embed.setDescription("Description Code");
		embed.setTitle("Title");
		message.addEmbed(embed);
		assertTrue(conn.postRequest(message));
	}
}