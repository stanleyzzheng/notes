package example.note;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

import org.assertj.core.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class NoteJsonTest {

    @Autowired
    private JacksonTester<Note> json;

    @Autowired
    JacksonTester<Note[]> jsonList;
    private Note[] notes;

    @BeforeEach
    void setUp() {
        notes = Arrays.array(new Note(99L, "First Note", "Hello World!", "sarah1"), new Note(100L, "Second Note", "Hello " +
                "World!", "sarah1"), new Note(101L, "Third Note", "Hello World!", "sarah1"));
    }

    @Test
    void noteDeserializationTest() throws IOException {
        String expected = """
                {"id": 99,
                "title": "First Note",
                "description": "Hello World!",
                "owner":"sarah1"}
                """;
//        Note actualNote = json.parseObject(expected).read();

        assertThat(json.parse(expected)).usingRecursiveComparison().isEqualTo(new Note(99L, "First Note", "Hello " +
                "World!", "sarah1"));
        assertThat(json.parseObject(expected).getId()).isEqualTo(99);
        assertThat(json.parseObject(expected).getDescription()).isEqualTo("Hello World!");
    }

    @Test
    void noteSerializationTest() throws IOException {
        Note note = new Note(99L, "First Note", "Hello World!", "sarah1");
        assertThat(json.write(note)).isStrictlyEqualToJson("single.json");
        assertThat(json.write(note)).hasJsonPathNumberValue("@.id");

        assertThat(json.write(note)).extractingJsonPathNumberValue("@.id").isEqualTo(99);

        assertThat(json.write(note)).hasJsonPathStringValue("@.title");

        assertThat(json.write(note)).extractingJsonPathStringValue("@.title").isEqualTo("First Note");
        assertThat(json.write(note)).extractingJsonPathStringValue("@.description");

        assertThat(json.write(note)).extractingJsonPathStringValue("@.description").isEqualTo("Hello World!");
    }

    @Test
    void noteListSerializationTest() throws IOException {
        assertThat(jsonList.write(notes)).isStrictlyEqualToJson("list.json");
    }

    @Test
    void noteListDeserializationTest() throws IOException {
        String expected = """
                [
                {"id":99, "title":"First Note", "description": "Hello World!", "owner":"sarah1"},
                {"id":100, "title":"Second Note", "description": "Hello World!", "owner":"sarah1"},
                {"id":101, "title":"Third Note", "description": "Hello World!", "owner":"sarah1"}
                ]
                """;
        assertThat(jsonList.parse(expected)).usingRecursiveComparison().isEqualTo(notes);
    }


}
