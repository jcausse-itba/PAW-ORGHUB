// DONE

package ar.edu.itba.paw.interfaces.service;

import ar.edu.itba.paw.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {
    /**
     * Find a Tag via its ID.
     * @param ID              The ID of the Tag to be found.
     * @return                An Optional containing a Tag if it was found. An empty Optional otherwise.
     */
    Optional<Tag> findByID(long ID);

    /**
     * Find a Tag via its unique name.
     * @param name            The name of the Tag to be found.
     * @return                An Optional containing a Tag if it was found. An empty Optional otherwise.
     */
    Optional<Tag> findByName(String name);

    /**
     * Find multiple Tags by their names.
     * @param names           A List of tag names to search for.
     * @return                A List of Tag objects that were found (that might be empty).
     */
    List<Tag> findMultipleTagsByName(List<String> names);

    /**
     * Find multiple Tags by their IDs.
     * @param IDs             A List of tag IDs to search for.
     * @return                A List of Tag objects that were found (that might be empty).
     */
    List<Tag> findMultipleTagsByID(List<Long> IDs);

    /**
     * Get all tags.
     * @return                A List of Tag objects (that might be empty).
     */
    List<Tag> findAll();

    /**
     * Create a new Tag and get the Tag object that represents it.
     * @param name            The tag name. Must be unique and not null.
     * @return                A Tag representing the created tag.
     */
    Tag create(String name);
}
