package example.note;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NoteApplicationTests {


    @Autowired
    TestRestTemplate restTemplate;

    @Test
     void shouldReturnANoteWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/notes/99",
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isEqualTo(99);

        String title = documentContext.read("$.title");
        String description = documentContext.read("$.description");
        assertThat(title).isEqualTo("First Note");
        assertThat(description).isEqualTo("Hello World!");
    }

    @Test
     void shouldNotReturnANoteWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/notes/1000",
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    @DirtiesContext
    void shouldCreateANewNote() {
        Note newNote = new Note(null, "new Note", "hello", null);
        ResponseEntity<Void> createResponse = restTemplate.withBasicAuth("sarah1", "abc123").postForEntity("/notes",
                newNote, Void.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        URI locationOfNewNote = createResponse.getHeaders().getLocation();
//        System.out.println(locationOfNewNote);
        ResponseEntity<String> getResponse =
                restTemplate.withBasicAuth("sarah1", "abc123").getForEntity(locationOfNewNote, String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String title = documentContext.read("$.title");
        String description = documentContext.read("$.description");

        assertThat(id).isNotNull();
        assertThat(description).isEqualTo("hello");
        assertThat(title).isEqualTo("new Note");
    }

    @Test
    void shouldReturnAllNotesWhenListIsRequested() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/notes",
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        int noteCount = documentContext.read("$.length()");
        assertThat(noteCount).isEqualTo(3);

        JSONArray ids = documentContext.read("$..id");
        assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

        JSONArray titles = documentContext.read("$..title");
        assertThat(titles).containsExactlyInAnyOrder("First Note", "Second Note", "Third Note");

        JSONArray descriptions = documentContext.read("$..description");
        assertThat(descriptions).containsExactlyInAnyOrder("Hello World!", "Hello World!", "Hello World!");

    }

    @Test
    void shouldReturnAPageOfNotes() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/notes?page=0" +
                "&size=1", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(1);
    }

    @Test
    void shouldReturnASortedPageOfNotes() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/notes?page=0" +
                "&size=1&sort=id,desc", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray read = documentContext.read("$[*]");
        assertThat(read.size()).isEqualTo(1);

        String title = documentContext.read("$[0].title");
        assertThat(title).isEqualTo("Third Note");


    }

    @Test
    void shouldReturnASortedPageOfNotesWithNoParametersAndUseDefaultValues() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/notes",
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        JSONArray page = documentContext.read("$[*]");
        assertThat(page.size()).isEqualTo(3);

        JSONArray titles = documentContext.read("$..title");
        assertThat(titles).containsExactly("First Note", "Second Note", "Third Note");
    }

    @Test
    void shouldNotReturnANoteWhenUsingBadCredentials() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("BAD-USER", "abc123").getForEntity("/notes/99",
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        response = restTemplate.withBasicAuth("sarah1", "BAD-PASSWORD").getForEntity("/notes/99", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }

    @Test
    void shouldRejectUsersWhoAreNoteCardOwners() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("hank-owns-no-cards", "qrs456").getForEntity(
                "/notes/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void shouldNotAllowAccessToNotesTheyDoNotOwn() {
        ResponseEntity<String> response = restTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/notes/102",
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }
    @Test
    @DirtiesContext
    void shouldUpdateAnExistingNote() {
        Note noteUpdate = new Note(null, "Update Note", "Update content", null);
        HttpEntity<Note> request = new HttpEntity<>(noteUpdate);
        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "abc123").exchange("/notes/99",
                HttpMethod.PUT, request, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/notes/99", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
        Number id = documentContext.read("$.id");
        String title = documentContext.read("$.title");
        assertThat(id).isEqualTo(99);
        assertThat(title).isEqualTo("Update Note");


    }
    @Test
    @DirtiesContext
    void shouldDeleteAnExistingNote(){
        ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1","abc123").exchange("/notes/99", HttpMethod.DELETE,null,Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("sarah1", "abc123").getForEntity("/notes/99",String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteANoteThatDoesNotExist(){
        ResponseEntity<Void> deleteResponse = restTemplate.withBasicAuth("sarah1", "abc123").exchange("/notes/99999", HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowDeletionOfNotesTheyDoNotOwn(){
        ResponseEntity<Void> deleteResponse = restTemplate.withBasicAuth("sarah1", "abc123").exchange("/notes/102",HttpMethod.DELETE, null, Void.class);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ResponseEntity<String> getResponse = restTemplate.withBasicAuth("kumar2", "xyz789").getForEntity("/notes/102", String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }




}
