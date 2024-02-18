package example.note;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

interface NoteRepository extends CrudRepository<Note, Long>, PagingAndSortingRepository<Note, Long> {
    Note findByIdAndOwner(Long id, String owner);
    Page<Note> findByOwner(String owner, PageRequest pageRequest);
}
