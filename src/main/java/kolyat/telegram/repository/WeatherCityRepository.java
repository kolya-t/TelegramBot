package kolyat.telegram.repository;

import kolyat.telegram.domain.WeatherCity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WeatherCityRepository extends CrudRepository<WeatherCity, Long> {

    List<WeatherCity> findAllByIsEnabledScheduling(boolean isEnabledScheduling);

    WeatherCity findByChatId(Long chatId);

    boolean existsByChatId(Long chatId);
}
