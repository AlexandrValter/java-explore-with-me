package ru.practicum.ExploreWithMe.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ExploreWithMe.exception.*;
import ru.practicum.ExploreWithMe.mapper.EventMapper;
import ru.practicum.ExploreWithMe.model.*;
import ru.practicum.ExploreWithMe.model.dto.*;
import ru.practicum.ExploreWithMe.repository.*;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final EntityManager entityManager;
    private final RestTemplate restTemplate;
    private final RequestRepository requestRepository;
    private final String statUrl;

    public EventServiceImpl(
            EventRepository eventRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            LocationRepository locationRepository,
            EntityManager entityManager,
            RequestRepository requestRepository,
            @Value("${ewm-stat.url}") String statUrl) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.locationRepository = locationRepository;
        this.entityManager = entityManager;
        this.requestRepository = requestRepository;
        this.restTemplate = new RestTemplate();
        this.statUrl = statUrl;
    }

    @Override
    public EventFullDto createEvent(long userId, NewEventDto eventDtoPost) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User with id=%s was not found.", userId)));
        Event event = EventMapper.toEvent(eventDtoPost);
        validateEventDateTime(event, 2L);
        event.setInitiator(initiator);
        Category category = categoryRepository.findById(eventDtoPost.getCategory()).orElseThrow(
                () -> new CategoryNotFoundException(
                        String.format("Category with id=%s was not found.", eventDtoPost.getCategory())
                ));
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.PENDING);
        softSaveLocation(event);
        log.info("Добавлено событие id={}", event.getId());
        return EventMapper.toEventFullDto(event);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<EventShortDto> getAllUserEvents(long userId, int from, int size) {
        if (userRepository.findById(userId).isPresent()) {
            int page = from / size;
            Pageable pageable = PageRequest.of(page, size);
            List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable).getContent();
            log.info("Запрошены все события пользователя id={}", userId);
            if (!events.isEmpty()) {
                return getStats(events).stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
            } else {
                return Collections.emptyList();
            }
        } else {
            throw new UserNotFoundException(String.format("User with id=%s was not found.", userId));
        }
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long userId, UpdateEventRequest updateEventRequest) {
        if (userRepository.findById(userId).isPresent()) {
            Event event = eventRepository.findById(updateEventRequest.getEventId()).orElseThrow(
                    () -> new EventNotFoundException(
                            String.format("Event with id=%s was not found.", updateEventRequest.getEventId()))
            );
            if (event.getInitiator().getId() != userId) {
                throw new EventUpdateException("Only the initiator can change the event");
            }
            Event newEvent = EventMapper.toEvent(updateEventRequest);
            if (event.getState().equals(State.PENDING) ||
                    event.getState().equals(State.CANCELED)) {
                validateEventDateTime(newEvent, 2L);
            } else {
                throw new EventUpdateException("Only pending or cancelled events can be changed");
            }
            updateEventFields(event, newEvent, updateEventRequest);
            log.info("Обновлено событие id={}", updateEventRequest.getEventId());
            return EventMapper.toEventFullDto(eventRepository.save(event));
        } else {
            throw new UserNotFoundException(String.format("User with id=%s was not found.", userId));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(long userId, long eventId) {
        log.info("Запрошено событие id={}", eventId);
        return getStats(List.of(checkUserIsOwnerEvent(userId, eventId))).get(0);
    }

    @Override
    @Transactional
    public EventFullDto cancelEvent(long userId, long eventId) {
        Event event = checkUserIsOwnerEvent(userId, eventId);
        if (event.getState().equals(State.PENDING)) {
            event.setState(State.CANCELED);
            log.info("Пользователь id={} отменил событие id={}", userId, eventId);
            return EventMapper.toEventFullDto(eventRepository.save(event));
        } else {
            throw new EventUpdateException(String.format("State %s cannot be changed", event.getState().toString()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<EventFullDto> searchEventsPrivate(EventParam param, int from, int size) {
        Query query = getQuery(param);
        query.setFirstResult(from);
        query.setMaxResults(size);
        List<Event> result = query.getResultList();
        return getStats(result);
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(long eventId, NewEventDto newEventDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(
                        String.format("Event with id=%s was not found.", eventId))
        );
        Event newEvent = EventMapper.toEvent(newEventDto);
        updateEventFields(event, newEvent, newEventDto);
        softSaveLocation(event);
        log.info("Обновлено событие id={}", eventId);
        return getStats(List.of(event)).get(0);
    }

    @Override
    @Transactional
    public EventFullDto publishEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(
                        String.format("Event with id=%s was not found.", eventId))
        );
        validateEventDateTime(event, 1L);
        if (event.getState().equals(State.PENDING)) {
            event.setState(State.PUBLISHED);
            event.setPublishedOn(LocalDateTime.now());
            log.info("Опубликовано событие id={}", eventId);
            return EventMapper.toEventFullDto(eventRepository.save(event));
        } else {
            throw new EventUpdateException(String.format("State %s cannot be published", event.getState().toString()));
        }
    }

    @Override
    @Transactional
    public EventFullDto cancelEvent(long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new EventNotFoundException(
                        String.format("Event with id=%s was not found.", eventId))
        );
        if (event.getState().equals(State.PENDING)) {
            event.setState(State.CANCELED);
            log.info("Отменено событие id={}", eventId);
            return EventMapper.toEventFullDto(eventRepository.save(event));
        } else {
            throw new EventUpdateException(String.format("State %s cannot be cancel", event.getState().toString()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<EventShortDto> searchEventsPublic(EventParam eventParam,
                                                        EndpointHitDto endpointHitDto,
                                                        int from,
                                                        int size) {
        eventParam.setStates(new String[]{State.PUBLISHED.toString()});
        if (eventParam.getRangeStart() == null && eventParam.getRangeEnd() == null) {
            eventParam.setRangeStart(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        Query query = getQuery(eventParam);
        query.setFirstResult(from);
        query.setMaxResults(size);
        List<Event> events = query.getResultList();
        List<EventShortDto> result = Collections.emptyList();
        if (events.size() > 0) {
            List<EventFullDto> eventFullDtos = getStats(events);
            if (eventParam.getOnlyAvailable()) {
                result = eventFullDtos.stream()
                        .filter(s -> (s.getConfirmedRequests() < s.getParticipantLimit()))
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList());
            } else {
                result = getStats(events).stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList());
            }
        } else {
            return result;
        }
        if (eventParam.getSort() != null && result.size() > 0) {
            sortEvents(eventParam, result);
        }
        addStat(endpointHitDto);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEvent(long id, EndpointHitDto endpointHitDto) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new EventNotFoundException(
                String.format("Event with id=%s was not found.", id)));
        if (event.getState().equals(State.PUBLISHED)) {
            addStat(endpointHitDto);
            log.info("Запрошено событие id={}", id);
            return getStats(List.of(event)).get(0);
        } else {
            throw new RequestEventException(String.format("Event with id=%s was not found", id));
        }
    }

    @Override
    @Transactional
    public EventFullDto addLike(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                String.format("Event with id=%s was not found.", eventId)));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User with id=%s was not found.", userId)));
        if (event.getInitiator().getId() != userId) {
            if (event.getEventDate().isBefore(LocalDateTime.now())) {
                if (requestRepository.findParticipationRequestByEventIdAndRequesterId(eventId, userId)
                        .orElseThrow(() -> new AddLikeException(String.format(
                                "User id=%s did not participate in the event id=%s", userId, eventId)))
                        .getStatus().equals(Status.CONFIRMED)) {
                    event.getLikes().add(user);
                    log.info("Пользователь id={} поставил лайк событию id={}", userId, eventId);
                    return getStats(List.of(eventRepository.save(event))).get(0);
                } else throw new AddLikeException(String.format(
                        "User id=%s did not participate in the event id=%s", userId, eventId));
            } else throw new AddLikeException("It is impossible to evaluate events that have not yet happened");
        } else throw new AddLikeException(String.format("Initiator id=%s can not liked his event id=%s",
                userId, eventId));
    }

    @Override
    @Transactional
    public void deleteLike(long userId, long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                String.format("Event with id=%s was not found.", eventId)));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(
                String.format("User with id=%s was not found.", userId)));
        if (event.getLikes().contains(user)) {
            event.getLikes().remove(user);
            log.info("Пользователь id={} удалил лайк с события id={}", userId, eventId);
            eventRepository.save(event);
        } else {
            throw new DeleteLikeException(String.format(
                    "Can't removed a like from an event id=%s that the user is=%s hasn't rated", eventId, userId));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<EventShortDto> getPopularEvents(int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);
        log.info("Запрошены популярные события");
        return getStats(eventRepository.findPopularEvents(pageable).getContent()).stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    /*
     *Метод заполняет значение поля views (только для опубликованных событий).
     *Заполняет количество одобренных запросов для событий.
     */
    @Transactional(readOnly = true)
    protected List<EventFullDto> getStats(List<Event> events) {
        List<Event> publishEvents = events.stream()
                .filter(s -> s.getState().equals(State.PUBLISHED))
                .collect(Collectors.toList());
        List<EventFullDto> result = events.stream()
                .map(EventMapper::toEventFullDto)
                .peek(s -> s.setConfirmedRequests(requestRepository.getCountConfirmedRequests(
                        s.getId(), Status.CONFIRMED)))
                .collect(Collectors.toList());
        if (publishEvents.size() > 0) {
            ResponseEntity<ViewStatsDto[]> responseEntity =
                    restTemplate.getForEntity(URI.create(createUri(publishEvents)), ViewStatsDto[].class);
            ViewStatsDto[] stats = responseEntity.getBody();
            if (stats != null) {
                for (ViewStatsDto view : stats) {
                    String id = view.getUri().substring(view.getUri().lastIndexOf("/") + 1);
                    result.stream().filter(s -> s.getId() == Long.parseLong(id)).forEach(s -> s.setViews(view.getHits()));
                }
            }
        }
        return result;
    }

    /*
     * Метод сохраняет локацию в БД. В случае когда в БД уже есть локация с такими координатами,
     * то метод присваивает событию уде существующую локацию
     */
    @Transactional
    protected void softSaveLocation(Event event) {
        try {
            eventRepository.save(event);
        } catch (DataIntegrityViolationException e) {
            Location location = getLocation(event);
            event.setLocation(location);
            eventRepository.save(event);
        }
    }

    //    Метод запрашивает существующую локацию для добавления её к событию
    @Transactional(readOnly = true)
    protected Location getLocation(Event event) {
        return locationRepository.findLocationByLatitudeAndLongitude(
                event.getLocation().getLatitude(),
                event.getLocation().getLongitude()
        );
    }

    //    Метод для создания динамического запроса
    private Query getQuery(EventParam param) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.where(createPredicates(root, criteriaBuilder, param));
        return entityManager.createQuery(criteriaQuery);
    }

    //    Метод осуществляет сортировку по дате или по просмотрам
    private void sortEvents(EventParam eventParam, List<EventShortDto> result) {
        try {
            EventSort sort = EventSort.valueOf(eventParam.getSort().toUpperCase());
            if (sort.equals(EventSort.EVENT_DATE)) {
                result.sort(Comparator.comparing(EventShortDto::getEventDate));
            }
            if (sort.equals(EventSort.VIEWS)) {
                result.sort(Comparator.comparing(EventShortDto::getViews));
            }
        } catch (IllegalArgumentException e) {
            throw new RequestEventException(String.format(
                    "Sorting can be only %s or %s", EventSort.EVENT_DATE, EventSort.VIEWS));
        }
    }

    //    Метод создает предикаты в зависимости для динамического запроса в зависимости от входных данных
    private Predicate createPredicates(Root<Event> root, CriteriaBuilder criteriaBuilder, EventParam param) {
        List<Predicate> predicates = new ArrayList<>();
        if (param.getUsers() != null) {
            List<Predicate> predicatesId = new ArrayList<>();
            for (int id : param.getUsers()) {
                predicatesId.add(criteriaBuilder.equal(root.<User>get("initiator").<Long>get("id"), id));
            }
            predicates.add(criteriaBuilder.or(predicatesId.toArray(new Predicate[0])));
        }
        if (param.getStates() != null) {
            List<Predicate> predicatesState = new ArrayList<>();
            for (String state : param.getStates()) {
                State st = State.valueOf(state.toUpperCase());
                predicatesState.add(criteriaBuilder.equal(root.<State>get("state"), st));
            }
            predicates.add(criteriaBuilder.or(predicatesState.toArray(new Predicate[0])));
        }
        if (param.getCategories() != null) {
            List<Predicate> predicatesCategories = new ArrayList<>();
            for (int id : param.getCategories()) {
                predicatesCategories.add(criteriaBuilder.equal(root.<Category>get("category").<Long>get("id"), id));
            }
            predicates.add(criteriaBuilder.or(predicatesCategories.toArray(new Predicate[0])));
        }
        if (param.getRangeStart() != null) {
            try {
                LocalDateTime rangeStart = LocalDateTime.parse(
                        param.getRangeStart(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(criteriaBuilder.greaterThan(root.get("eventDate"), rangeStart));
            } catch (DateTimeParseException e) {
                throw new EventDateTimeException("The date format should be 'yyyy-MM-dd HH:mm:ss'");
            }
        }
        if (param.getRangeEnd() != null) {
            try {
                LocalDateTime rangeEnd = LocalDateTime.parse(
                        param.getRangeEnd(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                predicates.add(criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
            } catch (DateTimeParseException e) {
                throw new EventDateTimeException("The date format should be 'yyyy-MM-dd HH:mm:ss'");
            }
        }
        if (param.getPaid() != null) {
            predicates.add(criteriaBuilder.equal(root.<Boolean>get("paid"), param.getPaid()));
        }
        if (param.getText() != null) {
            predicates.add(criteriaBuilder.or(
                    (criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("annotation")),
                            "%" + param.getText().toLowerCase() + "%")),
                    (criteriaBuilder.like(
                            criteriaBuilder.lower(root.get("description")),
                            "%" + param.getText().toLowerCase() + "%"))));
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    //    Метод проверяет является ли пользователь инициатором события
    private Event checkUserIsOwnerEvent(long userId, long eventId) {
        if (userRepository.findById(userId).isPresent()) {
            Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(
                    String.format("Event with id=%s was not found.", eventId)));
            if (event.getInitiator().getId() == userId) {
                return event;
            } else {
                throw new RequestEventException(
                        String.format("User id=%s is not initiator for event id=%s", userId, eventId));
            }
        } else {
            throw new UserNotFoundException(String.format("User with id=%s was not found.", userId));
        }
    }

    //    Метод проверяет удаленность даты провеедния эвента от текущей даты на заданное количество часов
    private void validateEventDateTime(Event event, long hours) {
        if (event.getEventDate().minusHours(hours).isBefore(LocalDateTime.now())) {
            throw new EventDateTimeException(String.format(
                    "Event date must be no earlier than %s",
                    LocalDateTime.now().plusHours(hours).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            );
        }
    }

    //    Метод обновления полей события
    private void updateEventFields(Event event, Event newEvent, UpdateEventRequest updateEventRequest) {
        updateStandardFields(event, newEvent);
        if (updateEventRequest.getCategory() != event.getCategory().getId()) {
            event.setCategory(categoryRepository.findById(
                    updateEventRequest.getCategory()).orElseThrow(
                    () -> new CategoryNotFoundException(
                            String.format("Category with id=%s was not found.", updateEventRequest.getCategory()))));
        }
        newEvent.setState(State.PENDING);
    }

    //    Метод обновления полей события
    private void updateEventFields(Event event, Event newEvent, NewEventDto newEventDto) {
        updateStandardFields(event, newEvent);
        if (newEventDto.getCategory() != event.getCategory().getId()) {
            event.setCategory(categoryRepository.findById(
                    newEventDto.getCategory()).orElseThrow(
                    () -> new CategoryNotFoundException(
                            String.format("Category with id=%s was not found.", newEventDto.getCategory()))));
        }
        if (newEvent.getLocation() != null) {
            if (event.getLocation().getLatitude() != newEvent.getLocation().getLatitude() ||
                    event.getLocation().getLongitude() != newEvent.getLocation().getLongitude()) {
                event.setLocation(newEvent.getLocation());
            }
        }
        if (newEvent.getRequestModeration() != null) {
            event.setRequestModeration(newEvent.getRequestModeration());
        }
    }

    //    Метод обновления стандартных полей события
    private void updateStandardFields(Event event, Event newEvent) {
        if (newEvent.getAnnotation() != null) {
            event.setAnnotation(newEvent.getAnnotation());
        }
        if (newEvent.getDescription() != null) {
            event.setDescription(newEvent.getDescription());
        }
        if (newEvent.getEventDate() != null) {
            event.setEventDate(newEvent.getEventDate());
        }
        if (newEvent.getPaid() != null) {
            event.setPaid(newEvent.getPaid());
        }
        if (newEvent.getParticipantLimit() != null) {
            event.setParticipantLimit(newEvent.getParticipantLimit());
        }
        if (newEvent.getTitle() != null) {
            event.setTitle(newEvent.getTitle());
        }
    }

    //    Метод формирует URI для запроса статистики
    private String createUri(List<Event> events) {
        String baseUri = statUrl.concat("/stats?uris=");
        StringBuilder sb = new StringBuilder();
        sb.append(baseUri);
        for (int i = 0; i < events.size(); i++) {
            if (i > 0 && i < events.size() - 1) {
                sb.append(",");
            }
            sb.append("/events/").append(events.get(i).getId());
        }
        LocalDateTime startRange = events.stream().min(Comparator.comparing(Event::getPublishedOn)).get().getPublishedOn();
        LocalDateTime endRange = events.stream().max(Comparator.comparing(Event::getEventDate)).get().getEventDate();
        String start = URLEncoder.encode(
                startRange.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                StandardCharsets.UTF_8);
        String end = URLEncoder.encode(
                endRange.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                StandardCharsets.UTF_8);
        return sb.append("&start=").append(start).append("&end=").append(end).toString();
    }

    //    Метод добавляет информацию в сервис статистики
    private void addStat(EndpointHitDto endpointHitDto) {
        String baseUri = statUrl.concat("/hit");
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto);
        restTemplate.postForObject(baseUri, request, EndpointHitDto.class);
    }
}