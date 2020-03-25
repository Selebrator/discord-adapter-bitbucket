package de.selebrator.discordadapterbitbucket.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DiscordMessageEmbed implements Serializable {
	private String title;
	private String description;
	private String url;
	private int color;
	private DiscordMessageFooter footer;
}
