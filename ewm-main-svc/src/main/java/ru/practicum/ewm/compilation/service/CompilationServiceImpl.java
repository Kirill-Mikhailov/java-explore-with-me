package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.dto.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.storage.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.event.storage.EventRepository;
import ru.practicum.ewm.exception.CompilationNotFoundException;
import ru.practicum.ewm.exception.EventNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return compilationRepository.getCompilations(pinned, pageable).stream()
                .map(comp -> CompilationMapper.toCompilationDto(comp,
                        eventService.toEventShortDtoList(comp.getEvents()))).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation with id=" + compId + " was not found"));
        return CompilationMapper.toCompilationDto(compilation, eventService.toEventShortDtoList(compilation.getEvents()));
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        Set<Event> events = new HashSet<>();
        if (!Objects.isNull(newCompilationDto.getEvents()) && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findByIdIn(newCompilationDto.getEvents());
            int countOfSkipped = newCompilationDto.getEvents().size() - events.size();
            if (countOfSkipped != 0) {
                throw new EventNotFoundException("From the provided list of events " + countOfSkipped
                        + "events are not found");
            }
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(
                CompilationMapper.toCompilation(newCompilationDto, events)), eventService.toEventShortDtoList(events));
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new CompilationNotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilationFromBd = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Compilation with id=" + compId + " was not found"));
        Set<Event> events;
        if (Objects.isNull(updateCompilationRequest.getEvents())) {
            events = compilationFromBd.getEvents();
        } else if (!updateCompilationRequest.getEvents().isEmpty()) {
            events = eventRepository.findByIdIn(updateCompilationRequest.getEvents());
            int countOfSkipped = updateCompilationRequest.getEvents().size() - events.size();
            if (countOfSkipped != 0) {
                throw new EventNotFoundException("From the provided list of events " + countOfSkipped
                        + "events are not found");
            }
        } else {
            events = Collections.emptySet();
        }
        return CompilationMapper.toCompilationDto(compilationRepository.save(CompilationMapper.toCompilation(
                updateCompilationRequest, compilationFromBd, events)), eventService.toEventShortDtoList(events));
    }
}
