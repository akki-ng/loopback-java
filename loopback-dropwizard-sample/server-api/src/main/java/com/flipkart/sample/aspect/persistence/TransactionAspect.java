package com.flipkart.sample.aspect.persistence;

import com.flipkart.loopback.annotation.Transaction;
import com.flipkart.loopback.connector.Connector;
import com.flipkart.loopback.exception.LoopbackException;
import com.flipkart.loopback.model.Model;
import java.util.Arrays;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by akshaya.sharma on 14/03/18
 */

@Aspect
public class TransactionAspect {
  Logger log = LoggerFactory.getLogger(TransactionAspect.class);

  @Around("execution(@Transaction * * (..)) && @annotation(transactionAnno)")
  public Object encloseMethodWithTransaction(ProceedingJoinPoint thisJoinPoint, Transaction transactionAnno) throws LoopbackException {
    try{
      System.out.println(thisJoinPoint);
      System.out.println("Starting Transaction Aspect");
      Object o = null;
      System.out.println(thisJoinPoint);
      System.out.println(thisJoinPoint.getSignature().toLongString());
      Object target = thisJoinPoint.getTarget();
      System.out.println(target);
      if(target != null) {
        System.out.println("Starting Transaction Aspect2");
        if(target instanceof Model) {
          Connector connector = ((Model) target).getConnector();
          EntityManager em = connector.getEntityManager();
          EntityTransaction tx = connector.getCurrentTransaction();
          boolean newEm = !tx.isActive() || !em.isOpen();
          if (!newEm) {
            em.joinTransaction();
          } else {
            em.getTransaction().begin();
          }
          try
          {
            o = thisJoinPoint.proceed();
            if (newEm && em.getTransaction().isActive()) {
              em.getTransaction().commit();
            }
          }
          catch (Throwable e)
          {
            if (em.getTransaction().isActive()) {
              em.getTransaction().rollback();
            }
            throw e;
          }
          finally
          {
            if (newEm)
            {
              connector.clearEntityManager();
              if (em.isOpen())
                em.close();
            }
          }
        }else {
          o = thisJoinPoint.proceed();
        }

      }else {
        o = thisJoinPoint.proceed();
      }
      return o;
    }catch (LoopbackException e) {
      e.printStackTrace();
      throw e;
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
    return null;
  }
}