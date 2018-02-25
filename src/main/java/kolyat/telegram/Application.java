package kolyat.telegram;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .web(false)
                .bannerMode(Banner.Mode.OFF)
                .sources(Application.class)
                .main(Application.class)
                .run(args);
    }
}
