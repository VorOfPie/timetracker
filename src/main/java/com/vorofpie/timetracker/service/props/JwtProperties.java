package com.vorofpie.timetracker.service.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Класс для хранения конфигурационных свойств JWT.
 * <p>
 * Аннотирован с помощью @ConfigurationProperties, что позволяет автоматически
 * привязывать значения из файла конфигурации (application.properties или application.yml)
 * к этому классу. Предполагается, что свойства начинаются с префикса "application.security.jwt".
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "application.security.jwt")
public class JwtProperties {

    /**
     * Секретный ключ для подписи и проверки JWT.
     */
    private String secret;

    /**
     * Время истечения срока действия access token в миллисекундах.
     */
    private long access;

    /**
     * Время истечения срока действия refresh token в миллисекундах.
     */
    private long refresh;
}
