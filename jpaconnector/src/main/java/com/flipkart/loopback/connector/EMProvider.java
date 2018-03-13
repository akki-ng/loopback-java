package com.flipkart.loopback.connector;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by akshaya.sharma on 13/03/18
 */

public class EMProvider {
  private static Logger log = LoggerFactory.getLogger(EMProvider.class);
  private static Map<String, EntityManagerFactory> factories = new ConcurrentHashMap<String,
      EntityManagerFactory>();
  private static Map<String, ThreadLocal<EntityManager>> emStore = new ConcurrentHashMap<String,
      ThreadLocal<EntityManager>>();

  private static EntityManagerFactory getEMFactory(final String persistenceUnit) {
    try {
      if(!factories.containsKey(persistenceUnit)) {
        factories.put(persistenceUnit, Persistence.createEntityManagerFactory(persistenceUnit));
      }
      return factories.get(persistenceUnit);
    } catch (Exception e) {
      log.error("Error in creating entity manager factory ", e);
      throw new RuntimeException("Could not create entity manager factory");
    }
  }

  public static EntityManager getEm(final String persistenceUnit) {
    if(!emStore.containsKey(persistenceUnit) || emStore.get(persistenceUnit) == null) {
      EntityManagerFactory emf = getEMFactory(persistenceUnit);
      ThreadLocal<EntityManager> localEm = new ThreadLocal<EntityManager>();
      localEm.set(emf.createEntityManager());
      emStore.put(persistenceUnit, localEm);
    }

    ThreadLocal<EntityManager> localEm = emStore.get(persistenceUnit);
    if(!localEm.get().isOpen()) {
      localEm.remove();
      EntityManagerFactory emf = getEMFactory(persistenceUnit);
      EntityManager em = emf.createEntityManager();
      localEm.set(em);
      return em;
    }
    return localEm.get();
  }

  public static void clear(final String persistenceUnit) {
    if(emStore.containsKey(persistenceUnit) || emStore.get(persistenceUnit) != null) {
      emStore.get(persistenceUnit).remove();
    }
  }

  /**
   * close the EM Factories and EntityManagers
   */
  public static void tearDown() {
    factories.values()
        .forEach(emf -> {
          if (emf != null && emf.isOpen()) {
            emf.close();
          }
        });
    factories.clear();
  }

  private EMProvider() {
    // only static methods
  }
}
