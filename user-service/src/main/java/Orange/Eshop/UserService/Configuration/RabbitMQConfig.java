package Orange.Eshop.UserService.Configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String USER_REGISTERD_QUEUE = "user.registered";

    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(USER_REGISTERD_QUEUE, true);
    }
}
