package kolyat.telegram.domain;

import lombok.Data;
import org.springframework.data.domain.Persistable;
import org.telegram.telegrambots.api.objects.Location;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
public class WeatherCity implements Persistable<Long> {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private Location location;

    @Column(nullable = false)
    private Boolean isEnabledScheduling;

    @SuppressWarnings("unused")
    public WeatherCity() {
    }

    public WeatherCity(Long chatId, Location location) {
        this(chatId, location, false);
    }

    @SuppressWarnings("WeakerAccess")
    public WeatherCity(Long chatId, Location location, Boolean isEnabledScheduling) {
        this.chatId = chatId;
        this.location = location;
        this.isEnabledScheduling = isEnabledScheduling;
    }

    @Override
    public boolean isNew() {
        return id == null;
    }
}
