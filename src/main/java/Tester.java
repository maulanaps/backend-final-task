import com.backend.task.constant.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Locale;

public class Tester {
    public static void main(String[] args) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String date = "2023-01-24";

        LocalDate localDate = LocalDate.parse(date);

        System.out.println(localDate.equals(localDateTime.toLocalDate()));
    }


}