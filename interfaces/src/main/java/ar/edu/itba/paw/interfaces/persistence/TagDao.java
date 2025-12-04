// DONE

package ar.edu.itba.paw.interfaces.persistence;

import ar.edu.itba.paw.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagDao {
    /**
     * Find a Tag via its ID.
     * @param tagID            The ID of the Tag to be found.
     * @return                 An Optional containing a Tag if it was found. An empty Optional otherwise.
     */
    Optional<Tag> findByID(long tagID);
    
    /**
     * Find a Tag by its name. Tag names are unique.
     * @param name             The tag name.
     * @return                 An Optional containing a Tag if it was found. An empty Optional otherwise.
     */
    Optional<Tag> findByName(String name);
    
    /**
     * Get all tags.
     * @return                 A List of Tag objects (that might be empty).
     */
    List<Tag> findAll();
    
    /**
     * Create a new Tag.
     * @param name             The tag name. Must not be null.
     * @return                 A Tag representing the created tag.
     */
    Tag create(String name);
    
    /**
     * Check if a tag name exists.
     * @param tagName          The tag name to check.
     * @return                 Boolean indicating result.
     */
    boolean existsTagName(String tagName);
}
