package kolyat.telegram.repository;

import kolyat.telegram.domain.ChatParameters;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatParametersRepository extends CrudRepository<ChatParameters, Long> {

    List<ChatParameters> findAllByIsEnabledScheduling(boolean isEnabledScheduling);

    ChatParameters findByChatId(Long chatId);

    boolean existsByChatId(Long chatId);
}
