// DONE

package ar.edu.itba.paw.service;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.interfaces.service.TagService;
import ar.edu.itba.paw.interfaces.service.exception.TagNameExistsException;
import ar.edu.itba.paw.model.Tag;
import ar.edu.itba.paw.service.annotation.TransactionalRead;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TagServiceImpl implements TagService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagDao tagDao;

    @Override
    @TransactionalRead
    public Optional<Tag> findByID(final long ID) {
        return tagDao.findByID(ID);
    }

    @Override
    @TransactionalRead
    public Optional<Tag> findByName(final String name) {
        return tagDao.findByName(name);
    }

    @Override
    @TransactionalRead
    public List<Tag> findMultipleTagsByName(List<String> names) {
        return names.stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isBlank())
                .map(String::toLowerCase)
                .map(tagDao::findByName)
                .flatMap(Optional::stream)
                .distinct()
                .toList();
    }

    @Override
    @TransactionalRead
    public List<Tag> findMultipleTagsByID(List<Long> IDs) {
        return IDs.stream()
                .map(tagDao::findByID)
                .flatMap(Optional::stream)
                .distinct()
                .toList();
    }

    @Override
    @TransactionalRead
    public List<Tag> findAll() {
        return tagDao.findAll();
    }

    @Override
    @Transactional
    public Tag create(final String name) {
        LOGGER.debug("Creating tag with name: {}", name);
        
        if (tagDao.existsTagName(name)) {
            LOGGER.warn("Tag creation failed: name {} already exists.", name);
            throw new TagNameExistsException(name);
        }
        
        Tag tag = tagDao.create(name);
        LOGGER.info("Tag created successfully: {} (ID: {}).", name, tag.getTagID());
        return tag;
    }
}
