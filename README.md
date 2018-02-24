To start bot, you need:
1. Register bot with [@BotFather](https://t.me/BotFather) and get bot token
2. Create PostgreSQL database
3. Start application with bot and database parameters (see [application-production.yml](src/main/resources/application-production.yml))
```
java -jar ${BOT_AND_DATABASE_PARAMETERS} telegram-bot-0.3.0.jar 
```