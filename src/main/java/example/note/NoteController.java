package example.note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.swing.text.html.Option;
import java.net.URI;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping("/notes")
public class NoteController {
    private final NoteRepository noteRepository;

    private NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping("/{requestedId}")
    private ResponseEntity<Note> findById(@PathVariable Long requestedId, Principal principal) {
        Optional<Note> noteOptional = Optional.ofNullable(noteRepository.findByIdAndOwner(requestedId, principal.getName()));

        if (noteOptional.isPresent()) {

            return ResponseEntity.ok(noteOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    private ResponseEntity<Void> createNote(@RequestBody Note newNoteRequest, UriComponentsBuilder ucb, Principal principal) {
        Note noteWithOwner = new Note(null, newNoteRequest.getTitle(),newNoteRequest.getDescription(), principal.getName());
        Note savedNote = noteRepository.save(noteWithOwner);
        URI locationOfNewNote = ucb.path("notes/{id}").buildAndExpand(savedNote.getId()).toUri();
        return ResponseEntity.created(locationOfNewNote).build();
    }

    @GetMapping()
    private ResponseEntity<Iterable<Note>> findAll(Pageable pageable,Principal principal) {
        Page<Note> page = noteRepository.findByOwner(principal.getName(), PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                pageable.getSort()));

        return ResponseEntity.ok(page.getContent());
    }
    @PutMapping("/{requestedId}")
    private  ResponseEntity<Void> putNote(@PathVariable Long requestedId, @RequestBody Note noteUpdate, Principal principal){
        Note note = noteRepository.findByIdAndOwner(requestedId, principal.getName());
        Note updatedNote = new Note(note.getId(),noteUpdate.getTitle(),noteUpdate.getDescription(), principal.getName());
        noteRepository.save(updatedNote);
        return ResponseEntity.noContent().build();
    }
}
