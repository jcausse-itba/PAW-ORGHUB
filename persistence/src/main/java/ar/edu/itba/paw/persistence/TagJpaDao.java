package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.interfaces.persistence.TagDao;
import ar.edu.itba.paw.model.Tag;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Repository
public class TagJpaDao implements TagDao {
    @PersistenceContext private EntityManager em;

    @Override
    public Optional<Tag> findByID(final long tagID) {
        return Optional.ofNullable(em.find(Tag.class, tagID));
    }

    @Override
    public Optional<Tag> findByName(final String name) {
        final String hql = "FROM Tag t WHERE t.tagName = :name";
        return em.createQuery(hql, Tag.class).setParameter("name", name).getResultList().stream().findFirst();
    }

    @Override
    public List<Tag> findAll() {
        final String hql = "FROM Tag t ORDER BY t.tagName";
        return em.createQuery(hql, Tag.class).getResultList();
    }

    @Override
    public Tag create(final String name) {
        final Tag tag = new Tag(name);
        em.persist(tag);
        return tag;
    }

    @Override
    public boolean existsTagName(final String tagName) {
        final String hql = "SELECT COUNT(t) FROM Tag t WHERE t.tagName = :tagName";
        return em.createQuery(hql, Long.class).setParameter("tagName", tagName).getSingleResult() > 0;
    }
}
