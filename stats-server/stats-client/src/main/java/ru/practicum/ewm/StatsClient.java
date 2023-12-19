package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class StatsClient {

    private final RestTemplate rest;

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        rest = builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build();
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder path = new StringBuilder("/stats?start={start}&end={end}");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(dateTimeFormatter));
        parameters.put("end", end.format(dateTimeFormatter));
        if (!Objects.isNull(uris)) {
            path.append("&uris={uris}");
            parameters.put("uris", uris.toArray());
        }
        if (!Objects.isNull(unique)) {
            path.append("&unique={unique}");
            parameters.put("unique", unique);
        }
        return rest.exchange(path.toString(), HttpMethod.GET, new HttpEntity<>(defaultHeaders()),
                new ParameterizedTypeReference<List<StatsDto>>() {}, parameters).getBody();
    }

    public ResponseEntity<Object> saveHit(HitDto hitDto) {
        return rest.exchange("/hit", HttpMethod.POST, new HttpEntity<>(hitDto, defaultHeaders()), Object.class);
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
