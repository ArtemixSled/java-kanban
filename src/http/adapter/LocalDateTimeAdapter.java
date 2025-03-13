package http.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value != null) {
            out.value(value.toString());
        } else {
            out.nullValue();
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        String value = in.nextString();
        if (value == null || value.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(value);
    }
}
