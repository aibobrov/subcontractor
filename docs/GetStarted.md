# Get Started

## Slack App development

1. Create a new [app](https://api.slack.com/apps?new_app=1&ref=bolt_start_hub)

2. Requesting scopes.

Go to your app settings -> OAuth & Permissions and select these scopes:

* `channels:read`
* `chat:write`
* `chat:write.public`
* `commands`
* `groups:read`
* `users:read`
* `users:profile:read`

3. Install your app in your development workspace

4. Edit `.env` file using your app's tokens.

`SLACK_BOT_TOKEN` from OAuth & Permissions page (Bot User OAuth Access Token).
`SLACK_SIGNING_SECRET` from Basic Information page (look for Signing Secret field)

5. Start `ngrok` tunnel
```bash
$ ngrok http 3000
```

6. Create slack commands: `/liquid` and `/rules`. (see Slack commands page). 

Set Request URL as ngrok forwarding address + '/slack/events'.

Example: `http://9954592af85d.ngrok.io/slack/events`

7. Go to Interactivity & Shortcuts page and set Interactivity Request URL as the ngrok forwarding address + '/slack/events'.

Example: `http://9954592af85d.ngrok.io/slack/events`

8. Run server using:

```bash
./gradlew :subcontractor-slack-server:bootRun
```

Or from Intellij IDEA
