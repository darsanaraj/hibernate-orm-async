package org.hibernate.jpa.test.async;

import org.hibernate.jpa.test.pack.asyncjar.AsyncPerson;
import org.hibernate.jpa.test.packaging.PackagingTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.async.AsyncEntityManager;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Tests for AsyncEntityManager.
 */
public class AsyncEntityManagerTest extends PackagingTestCase {

    @Test
    public void testBasic() throws Exception {
        File testPackage = buildAsyncJar();
        addPackageToClasspath( testPackage );

        // run the test
        EntityManagerFactory emf = Persistence.createEntityManagerFactory( "asyncjar", new HashMap() );
        insertDataSynchronously(emf);

        AsyncEntityManager aem = emf.createAsyncEntityManager();
        List<AsyncPerson> asyncPersons = aem.createQuery("select a from AsyncPerson a where a.name in (:name)", AsyncPerson.class)
                .setParameter("name", Arrays.asList("jakob", "jakob1"))
                .setFirstResult(0)
                .setMaxResults(10)
                .getResultList().get();
        for (AsyncPerson asyncPerson : asyncPersons) {
            System.out.println(asyncPerson);
        }

        QueryRow row = aem.createQuery("select new " + QueryRow.class.getName() + "(a.id, a.name) from AsyncPerson a where a.name in (:name)", QueryRow.class)
                .setParameter("name", Arrays.asList("jakob", "jakob1"))
                .getSingleResult().get();
        System.out.println(row);

        Integer updateCnt = aem.createQuery("update AsyncPerson a set a.name = :newName where a.name = :oldName", Void.class)
                .setParameter("newName", "steffi")
                .setParameter("oldName", "jakob")
                .executeUpdate().get();
        System.out.println("UPDATED " + updateCnt);

        QueryRow rowNew = aem.createQuery("select new " + QueryRow.class.getName() + "(a.id, a.name) from AsyncPerson a where a.name = :name", QueryRow.class)
                .setParameter("name", "steffi")
                .getSingleResult().get();
        System.out.println(rowNew);

        Integer deleteCnt = aem.createQuery("delete from AsyncPerson a where a.name = :name", Void.class)
                .setParameter("name", "steffi")
                .executeUpdate().get();
        System.out.println("DELETED " + deleteCnt);

        aem.close();

        emf.close();
    }

    private void insertDataSynchronously(EntityManagerFactory emf) {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            em.persist(new AsyncPerson("jakob"));
            em.flush();
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public static class QueryRow {
        private final Integer id;
        private final String name;

        public QueryRow(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return "QueryRow{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }
    }


}
