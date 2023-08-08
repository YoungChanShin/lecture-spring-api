package edu.magnet.interactiveblog.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.magnet.interactiveblog.common.RestDocsConfiguration;
import edu.magnet.interactiveblog.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
public class EventControllerTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    ModelMapper modelMapper;


    @Test
    @TestDescription("정상적으로 이벤트를 생성하는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2023,6,18,19,53))
                .closeEnrollmentDateTime(LocalDateTime.of(2023,7,19,19,53))
                .beginEventDateTime(LocalDateTime.of(2023,8,18,19,53))
                .endEventDateTime(LocalDateTime.of(2023,9,18,19,53))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("상공회의소")
                .build();

//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("query-events").description("link to query-events"),
                                linkWithRel("update-event").description("link to update-event"),
                                linkWithRel("profile").description("link to profile")
                        ),
                        requestHeaders (
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields (
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new Event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new Event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new Event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Location header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of new Event"),
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("description of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new Event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new Event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new Event"),
                                fieldWithPath("location").description("location of new Event"),
                                fieldWithPath("basePrice").description("basePrice of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new Event"),
                                fieldWithPath("offline").description("offline of new Event"),
                                fieldWithPath("free").description("free of new Event"),
                                fieldWithPath("eventStatus").description("eventStatus of new Event"),
                                fieldWithPath("_links.self.href").description("self link to new Event"),
                                fieldWithPath("_links.query-events.href").description("query-events link to new Event"),
                                fieldWithPath("_links.update-event.href").description("update-event link to new Event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                         )
        ));
    }


    @Test
    @TestDescription("입력받을 수 없는 값을 사용한 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2023,7,18,19,53))
                .closeEnrollmentDateTime(LocalDateTime.of(2023,7,19,19,53))
                .beginEventDateTime(LocalDateTime.of(2023,7,18,19,53))
                .endEventDateTime(LocalDateTime.of(2023,7,18,19,53))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("상공회의소")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build();

//        Mockito.when(eventRepository.save(event)).thenReturn(event);

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON )
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 에러가 발생하는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2023,7,18,19,53))
                .closeEnrollmentDateTime(LocalDateTime.of(2023,7,19,19,53))
                .beginEventDateTime(LocalDateTime.of(2023,7,18,19,53))
                .endEventDateTime(LocalDateTime.of(2023,7,18,19,53))
                .basePrice(1000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("상공회의소")
                .build();

        this.mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON )
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors[0].objectName").exists())
                .andExpect(jsonPath("errors[0].defaultMessage").exists())
                .andExpect(jsonPath("errors[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 19개씩 두번째 페이지 조회하기")
    public void queryEvents() throws Exception {
        //Given
        IntStream.range(0,30).forEach(this::generateEvent);

        //When
        this.mockMvc.perform(get("/api/events")
                    .param("page","1")
                    .param("size","10")
                    .param("sort", "name,DESC")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventResourceList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("query-events"))
                ;
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception {
        //Given
        Event event = this.generateEvent(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get0-an-event"))
                ;
    }

    @Test
    @TestDescription("없는 이벤트 조회 시 404 응답받기")
    public void getEvent404() throws Exception {
        //Given
//        Event event = this.generateEvent(100);

        // When & Then
        this.mockMvc.perform(get("/api/events/11111"))
        .andExpect(status().isNotFound())
        ;
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        //Given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event"))
        ;
    }

    @Test
    @TestDescription("입력값이 없는 경우 이벤트 수정 실패")
    public void updateEvent400_Empty() throws Exception {
        //Given
        Event event = this.generateEvent(200);

        EventDto eventDto = new EventDto();

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력값이 잘못된 경우 이벤트 수정 실패")
    public void updateEvent400_Wrong() throws Exception {
        //Given
        Event event = this.generateEvent(200);

        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(200);

        // When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }


    @Test
    @TestDescription("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404() throws Exception {
        //Given
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        // When & Then
        this.mockMvc.perform(put("/api/events/999123", event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }


    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("spring")
                .description("REST API")
                .beginEnrollmentDateTime(LocalDateTime.of(2023,6,18,19,53))
                .closeEnrollmentDateTime(LocalDateTime.of(2023,7,19,19,53))
                .beginEventDateTime(LocalDateTime.of(2023,8,18,19,53))
                .endEventDateTime(LocalDateTime.of(2023,9,18,19,53))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("상공회의소")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build()
                ;
        return this.eventRepository.save(event);
    }

}
