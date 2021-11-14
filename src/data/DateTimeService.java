/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

import annotations.Injectable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Manel
 */
@Injectable
public class DateTimeService {

    public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm:ss";

    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }

    public String getCurrentDateTimeString() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(LocalDateTime.now());
    }

    public String getStringFromDateTime(LocalDateTime localDateTime) {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT).format(localDateTime);
    }
}
