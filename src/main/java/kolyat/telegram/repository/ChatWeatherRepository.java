package kolyat.telegram.repository;

import kolyat.telegram.domain.ChatWeather;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatWeatherRepository extends CrudRepository<ChatWeather, Long> {

    List<ChatWeather> findAllBySubscribed(boolean subscribed);

    ChatWeather findByChatId(Long chatId);
}
