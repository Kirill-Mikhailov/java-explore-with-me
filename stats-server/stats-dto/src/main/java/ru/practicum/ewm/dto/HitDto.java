package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class HitDto {
    @NotNull(message = "Идентификатор сервиса не может быть пустым")
    private String app;
    @NotNull(message = "URI запроса не может быть пустым")
    private String uri;
    @NotNull(message = "IP-адрес пользователя не может быть пустым")
    private String ip;
    @JsonProperty("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private LocalDateTime hitTime;
}
