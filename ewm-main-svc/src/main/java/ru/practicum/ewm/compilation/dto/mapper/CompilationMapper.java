package ru.practicum.ewm.compilation.dto.mapper;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.Objects;
import java.util.Set;

public class CompilationMapper {

    public static Compilation toCompilation(NewCompilationDto newCompilationDto, Set<Event> events) {
        return Compilation.builder()
                .events(events)
                .pinned(!Objects.isNull(newCompilationDto.getPinned()) && newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .build();
    }

    public static CompilationDto toCompilationDto(Compilation compilation, Set<EventShortDto> events) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(events)
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation toCompilation(UpdateCompilationRequest newRequest, Compilation compilationFromDb, Set<Event> events) {
        return Compilation.builder()
                .id(compilationFromDb.getId())
                .events(events)
                .pinned(Objects.isNull(newRequest.getPinned()) ? compilationFromDb.getPinned() : newRequest.getPinned())
                .title(Objects.isNull(newRequest.getTitle()) ? compilationFromDb.getTitle() : newRequest.getTitle())
                .build();
    }
}
