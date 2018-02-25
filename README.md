To start bot, you need:
1. Register bot with [@BotFather](https://t.me/BotFather) and get bot token
2. Install JRE8
3. Create PostgreSQL database
4. Start application with database & bot parameters (see [application-production.yml](src/main/resources/application-production.yml))
```jshelllanguage
java -jar ${BOT_AND_DATABASE_PARAMETERS} telegram-bot-1.0.0.jar 
```

Plan: 
- [ ] Sending an error report to me in PM
- [ ] Change geolocation using keyboard
- [ ] Add support for multiple languages
- [ ] In addition to the degrees show and other weather information, for example "sunny", "rainstorm". Add the appropriate emoji
- [ ] Selecting the time when to send the weather forecast
- [ ] To make the user time zone dependent