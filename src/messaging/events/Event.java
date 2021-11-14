/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package messaging.events;

import data.DateTimeService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Manel
 */
public class Event {

    public LocalDateTime timestamp;

    public void timestampEvent() {
        timestamp = LocalDateTime.now();
    }

    public String getFormattedTimestamp() {
        return DateTimeFormatter.ofPattern(DateTimeService.DATE_TIME_FORMAT).format(timestamp);
    }
}
