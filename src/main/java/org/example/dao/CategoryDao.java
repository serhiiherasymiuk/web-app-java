package org.example.dao;

import org.example.entities.Category;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class CategoryDao implements Dao<Category> {
    private final SessionFactory sessionFactory;

    public CategoryDao(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<Category> get(int id) {
        try (Session session = sessionFactory.openSession()) {
            return Optional.ofNullable(session.get(Category.class, id));
        }
    }

    @Override
    public List<Category> getAll() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM Category ", Category.class).list();
        }
    }

    @Override
    public void save(Category category) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.save(category);
            tx.commit();
        }
    }

    @Override
    public void update(Category category) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(category);
            tx.commit();
        }
    }

    @Override
    public void delete(Category category) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.delete(category);
            tx.commit();
        }
    }
}
