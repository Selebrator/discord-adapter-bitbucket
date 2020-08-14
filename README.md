# discord-adapter-bitbucket
Translates Bitbuckets webhook output into Discords webhook input.
Be aware that you have to host this application somewhere!

Based on [pokenshin/discord-adapter-aws](https://github.com/pokenshin/discord-adapter-aws).

## Setup and configuration
On the Bitbucket web interface go to "Repository settings", then "Webhooks", "Create webhook".
For the URL you have to provide the location where you're hosting this application. For example "http://ems.informatik.uni-oldenburg.de:6668/bitbucket-to-discord".
Currently The categories "Opened", "Approved", "Unapproved" and "Needs work" are supported.

On your Discord server's settings go to "Integrations", "Webhooks", "New Webhook". Copy the URL. Youll need it for configuring this application.

If you build the jar and run it later, you can give the Disocrd webhook URL as the one and only program argument.
You can also run the application from maven, if you replace the placeholder comment `<!-- your discord webhook here -->`.
Then you can start the application with `maven clean exec:java`.
