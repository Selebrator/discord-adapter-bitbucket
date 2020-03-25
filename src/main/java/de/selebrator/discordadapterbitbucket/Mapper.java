package de.selebrator.discordadapterbitbucket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.selebrator.discordadapterbitbucket.model.DiscordMessage;
import de.selebrator.discordadapterbitbucket.model.DiscordMessageEmbed;

import java.io.IOException;
import java.util.*;

public class Mapper {

	public Optional<DiscordMessage> bitbucketToDiscord(String json) throws IOException {
		JsonNode root = new ObjectMapper().readTree(json);

		if (!root.has("eventKey")) {
			return Optional.empty();
		}
		switch (root.get("eventKey").asText()) {
			case "pr:opened":
				return Optional.of(this.openPr(root));
			case "pr:reviewer:needs_work":
				return Optional.of(this.prNeedsWork(root));
			case "pr:reviewer:approved":
				return prApproved(root);
			case "pr:reviewer:unapproved":
				return prUnapproved(root);
			default:
				return Optional.empty();
		}
	}

	private DiscordMessage openPr(JsonNode root) {
		DiscordMessage message = new DiscordMessage();
		message.setUsername(getUsername(root));
		message.setContent("Ich habe einen neuen PR geöffnet");
		message.addEmbed(getPullRequestEmbed(root, 0x0048ba));

		return message;
	}

	private DiscordMessage prNeedsWork(JsonNode root) {
		DiscordMessage message = new DiscordMessage();
		String author = root.get("pullRequest").get("author").get("user").get("displayName").asText();
		String authorPossession = (author.endsWith("s") || author.endsWith("x") ? author + "'" : author + "s");
		message.setUsername(getUsername(root));
		message.setContent("Ich habe noch Einwände an " + authorPossession + " PR");
		message.addEmbed(getPullRequestEmbed(root, 0xff0000));

		return message;
	}

	private Optional<DiscordMessage> prApproved(JsonNode root) {
		if (countApprovingReviewers(root) < 2) {
			return Optional.empty();
		}

		DiscordMessage message = new DiscordMessage();
		String author = root.get("pullRequest").get("author").get("user").get("displayName").asText();
		message.setUsername(getUsername(root));
		message.setContent("Ein PR von " + author + " ist bereit fürs mergen");
		message.addEmbed(getPullRequestEmbed(root, 0x00ff00));

		return Optional.of(message);
	}

	private Optional<DiscordMessage> prUnapproved(JsonNode root) {
		if (countApprovingReviewers(root) >= 2) {
			return Optional.empty();
		}

		DiscordMessage message = new DiscordMessage();
		String author = root.get("pullRequest").get("author").get("user").get("displayName").asText();
		message.setUsername(getUsername(root));
		message.setContent("Ein PR von " + author + " ist nicht länger bereit fürs mergen");
		message.addEmbed(getPullRequestEmbed(root, 0xff8000));

		return Optional.of(message);
	}

	private String getUsername(JsonNode root) {
		return root.get("actor").get("displayName").asText();
	}

	private DiscordMessageEmbed getPullRequestEmbed(JsonNode root, int color) {
		JsonNode pr = root.get("pullRequest");
		DiscordMessageEmbed embed = new DiscordMessageEmbed();
		embed.setTitle(pr.get("title").asText());
		embed.setUrl(pr.get("links").get("self").get(0).get("href").asText());
		embed.setDescription(getReviewers((root)));
		embed.setColor(color);
		return embed;
	}

	private int countApprovingReviewers(JsonNode root) {
		JsonNode reviewers = root.get("pullRequest").get("reviewers");
		int approving = 0;
		for (int i = 0; i < reviewers.size(); i++) {
			if (reviewers.get(i).get("approved").asBoolean()) {
				approving++;
			}
		}
		return approving;
	}

	private String getReviewers(JsonNode root) {
		JsonNode reviewers = root.get("pullRequest").get("reviewers");
		StringJoiner sj = new StringJoiner(System.lineSeparator());
		for (int i = 0; i < reviewers.size(); i++) {
			JsonNode reviewer = reviewers.get(i);
			String name = reviewer.get("user").get("displayName").asText();
			String status = reviewer.get("status").asText();
			if (status.equals("UNAPPROVED")) status = ":question:";
			if (status.equals("APPROVED")) status = ":white_check_mark:";
			if (status.equals("NEEDS_WORK")) status = ":no_entry:";
			sj.add(name + ": " + status);
		}
		return sj.toString();
	}

	public String toJson(Object obj) throws JsonProcessingException {
		return new ObjectMapper().writeValueAsString(obj);
	}
}