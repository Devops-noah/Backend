package fr.parisnanterre.noah.DTO;

import lombok.Data;

@Data
public class GoogleCalendarEventDto {
    private String summary;
    private String start;
    private String end;
    private String htmlLink; // include the getter/setter too


    public GoogleCalendarEventDto(String summary, String start, String end, String htmlLink) {
        this.summary = summary;
        this.start = start;
        this.end = end;
        this.htmlLink = htmlLink;

    }


    public GoogleCalendarEventDto() {

    }
}

