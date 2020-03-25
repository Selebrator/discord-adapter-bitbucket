package de.selebrator.discordadapterbitbucket.model;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

@Data
public class DiscordMessage implements Serializable {
	private String content = "";
	private String username;
	List<DiscordMessageEmbed> embeds;

	public void addEmbed(DiscordMessageEmbed embed) {
		if (this.embeds == null) {
			this.embeds = new ArrayList<>();
		}
		this.embeds.add(embed);
	}
}
