To start bot, you need:
1. Register bot with [@BotFather](https://t.me/BotFather) and get bot token
2. Install JRE8
3. Create PostgreSQL database
4. Start application with database & bot parameters (see [application-production.yml](src/main/resources/application-production.yml))
```jshelllanguage
java -jar ${BOT_AND_DATABASE_PARAMETERS} telegram-bot-1.0.0.jar 
```