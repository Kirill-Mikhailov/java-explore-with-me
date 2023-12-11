package ru.practicum.statistics.model;


import ru.practicum.dto.HitDto;

public class HitMapper {

    public static Hit toHit(HitDto hitDto) {
        return Hit.builder()
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .hitTime(hitDto.getHitTime())
                .build();
    }

    public static HitDto toHitDto(Hit hit) {
        return HitDto.builder()
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .hitTime(hit.getHitTime())
                .build();
    }
}
