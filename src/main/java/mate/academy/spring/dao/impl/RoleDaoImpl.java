package mate.academy.spring.dao.impl;

import java.util.Optional;
import mate.academy.spring.dao.RoleDao;
import mate.academy.spring.exception.DataProcessingException;
import mate.academy.spring.model.Role;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class RoleDaoImpl implements RoleDao {
    private final SessionFactory factory;

    public RoleDaoImpl(SessionFactory factory) {
        this.factory = factory;
    }

    @Override
    public void add(Role role) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = factory.openSession();
            transaction = session.beginTransaction();
            session.save(role);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can't add new role to DB " + role, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<Role> getRoleByName(Role.RoleName roleName) {
        try (Session session = factory.openSession()) {
            return session.createQuery("from Role WHERE name = :name", Role.class)
                    .setParameter("name", roleName)
                    .uniqueResultOptional();
        } catch (Exception e) {
            throw new DataProcessingException(roleName + " role not found", e);
        }
    }
}