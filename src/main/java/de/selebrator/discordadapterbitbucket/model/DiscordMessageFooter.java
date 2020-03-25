package de.selebrator.discordadapterbitbucket.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DiscordMessageFooter implements Serializable {
	private String text;
}
