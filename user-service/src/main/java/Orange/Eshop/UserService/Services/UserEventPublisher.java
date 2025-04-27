package Orange.Eshop.UserService.Services;

import Orange.Eshop.UserService.Configuration.RabbitMQConfig;
import Orange.Eshop.UserService.DTOs.UserRegisterdEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public UserEventPublisher(RabbitTemplate rabbitTemplate)
    {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserRegisteredEvent(UserRegisterdEvent event)
    {
        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_REGISTERD_QUEUE, event);
    }
}
