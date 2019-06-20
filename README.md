# Jenkins Notifications Plugin for Lark
This plugin allows sending jobs status notification to your work group with Lark, and help you understand the build result.


## How to use
In order to support Jenkins notification configuration and Webhook address configuration, you need to install the official Jenkins plugin

**1. Log in lark，invite Jenkins CI Assistant(this is a chat bot) to join the group.**

**2. @Jenkins CI Assistant in the group, and get the Web Hook URL for receiving the web jobs status notification**

```
@Jenkins CI Assistant get_token
```

**3. Configure the Jenkins plugin**
 - Select the Job in Jenkins and configure the plugin and Webhook address, configure
 -- post-build operations
 -- add post-build steps
 -- select lark notifier configuration

 ![](./static/config_plugin_cn.png?raw=true)

 ![](./static/job_plugin_cn.png?raw=true)

 - Bot sends the build result to the specified group, clicks the card to the applet to view the build details, and the applet records the job list

 ![](./static/card_message.png?raw=true) ![](./static/job_list.png?raw=true)

## License
MIT
