package edu.magnet.interactiveblog.events;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

// alt - ctrl - O
class EventTest {
    @Test
    public void builder() {
        Event event = Event.builder().name("TEST")
                .description("TEST").build()
                ;
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // Given
        String name = "Event";
        String spring = "Spring";

        // When
        Event event=new Event();
        event.setName(name);
        event.setDescription(spring);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(spring);

    }
}