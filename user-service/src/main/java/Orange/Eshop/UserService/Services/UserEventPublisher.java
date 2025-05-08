package Orange.Eshop.UserService.Services;

import Orange.Eshop.UserService.config.RabbitMQConfig;
import Orange.Eshop.UserService.DTOs.PasswordResetEvent;
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
        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_REGISTERED_QUEUE, event);
    }

    public void publishPasswordResetEvent(PasswordResetEvent event){
        rabbitTemplate.convertAndSend(RabbitMQConfig.PASSWORD_RESET_QUEUE,event);
    }
}
