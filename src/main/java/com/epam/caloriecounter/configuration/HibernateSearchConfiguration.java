package com.epam.caloriecounter.configuration;

import com.epam.caloriecounter.exception.IndexingProcessException;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

@Configuration
public class HibernateSearchConfiguration {

    private final EntityManager entityManager;

    @Autowired
    public HibernateSearchConfiguration(final EntityManagerFactory entityManagerFactory) {
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @PostConstruct
    public void rebuildLuceneIndexes() {

        try {
            FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IndexingProcessException();
        }
    }
}
