package com.flipkart.loopback.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flipkart.loopback.configuration.ModelConfiguration;
import com.flipkart.loopback.configuration.manager.ModelConfigurationManager;
import com.flipkart.loopback.constants.OrderType;
import com.flipkart.loopback.filter.Filter;
import com.flipkart.loopback.filter.OrderBy;
import com.flipkart.loopback.filter.WhereFilter;
import com.flipkart.loopback.model.PersistedModel;
import com.flipkart.loopback.relation.Relation;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by akshaya.sharma on 10/03/18
 */

public class QueryGenerator {
  private static QueryGenerator instance;

  private QueryGenerator() {
    System.out.println("Aksjhay");
  }

  public static QueryGenerator getInstance() {
    if (instance == null) {
      instance = new QueryGenerator();
    }
    return instance;
  }

  private <M extends PersistedModel> String buildColumnNames(Class<M> modelClass, Filter filter){
    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(
        modelClass);

    Map<String, Field> properties = PersistedModel.getProperties(modelClass);
    JsonNode fieldsFilter = null;
    if (properties == null || properties.isEmpty()) {
      return "*";
    }

    if (fieldsFilter != null) {
      // TODO
      return "";
    }

    StringBuilder sb = new StringBuilder();

    List<String> columnNames = properties.values().stream().map(field -> {
      return getColumnName(field);
    }).collect(Collectors.toList());

    return Joiner.on(",").join(columnNames);
  }

  public <M extends PersistedModel> String getSelectQuery(Class<M> modelClass, Filter filter) {
//    if (!filter.) {
//      var idNames = this.idNames(model);
//      if (idNames && idNames.length) {
//        filter.order = idNames;
//      }
//    }
    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(
        modelClass);

    StringBuilder sb = new StringBuilder();

    sb.append("SELECT ");
    sb.append(buildColumnNames(modelClass, filter));
    sb.append(" FROM " + configuration.getTableName());

    return sb.toString();

//    if(filter != null) {
//      WhereFilter where = filter.getWhere();
//      if(where != null) {
//        JsonNode w = where.getValue();
//        Iterator<Map.Entry<String, JsonNode>> fieldItr = w.fields();
//
//      }
//    }
//
//
//    CriteriaBuilder cb = em.getCriteriaBuilder();
//    CriteriaQuery<M> query = cb.createQuery(modelClass);
//    Root<M> root = query.from(modelClass);
//
//    List<Predicate> p = new ArrayList<Predicate>();
//
//
//    TypedQuery<M> typedQuery = em.createQuery(query);
//    return typedQuery;
  }

  private String getColumnName(Field field) {
    String columnName = field.getName();
    Column column = field.getAnnotation(Column.class);
    if (column != null && StringUtils.isNotBlank(column.name())) {
      columnName = column.name();
    }
    return columnName;
  }

  public <M extends PersistedModel> TypedQuery<M> getSelectTypedQuery(EntityManager em,
      Class<M> modelClass, Filter filter) {
    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(
        modelClass);

//    if (!filter.) {
//      var idNames = this.idNames(model);
//      if (idNames && idNames.length) {
//        filter.order = idNames;
//      }
//    }

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<M> query = cb.createQuery(modelClass);
    Root<M> root = query.from(modelClass);


    if (filter != null) {
      WhereFilter where = filter.getWhere();
      List<Predicate> predicates = getWherePredicates(root, query, cb, modelClass, where);
      if (predicates.size() > 0) {
        query.where(predicates.toArray(new Predicate[] {}));
      }

      ArrayList<String> selectFields = filter.getFields();
      if (selectFields != null && selectFields.size() > 0) {
//        List multiSelect = selectFields.stream()
//            .map(fieldName -> {
//              Field field = properties.get(fieldName);
//              String columnName = field.getName();
//              return root.get(columnName);
//            })
//            .collect(Collectors.toList());
//        if(multiSelect.size() > 0) {
//          query.select(root).multiselect(multiSelect);
//        }else {
//          query.select(root);
//        }
      }
    }

    query.select(root);

    if(filter != null && filter.getOrder() != null && filter.getOrder().getOrderBy() != null) {
      Map<String, Field> properties = PersistedModel.getProperties(modelClass);
      List<Order> orders = Lists.newArrayList();

      for(int i = 0; i < filter.getOrder().getOrderBy().size(); i++) {
        OrderBy orderBy = filter.getOrder().getOrderBy().get(i);
        Field field = properties.get(orderBy.getProperty());
        String fieldName = field.getName();
        if(orderBy.getType() == OrderType.DESC) {
          orders.add(cb.desc(root.get(fieldName)));
        }else {
          orders.add(cb.asc(root.get(fieldName)));
        }
      }
      if(orders.size() > 0) {
        Order[] orderArray = new Order[orders.size()];
        orderArray = orders.toArray(orderArray);
        query = query.orderBy(orderArray);
      }
    }
    TypedQuery<M> typedQuery = em.createQuery(query);
    return typedQuery;
  }

  public <M extends PersistedModel> TypedQuery<Long> getCountTypedQuery(EntityManager em,
      Class<M> modelClass, WhereFilter where) {
    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(
        modelClass);


    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> query = cb.createQuery(Long.class);
    Root<M> root = query.from(modelClass);

    query.select(cb.count(root));

    if (where != null) {
      List<Predicate> predicates = getWherePredicates(root, query, cb, modelClass, where);
      if (predicates.size() > 0) {
        query.where(predicates.toArray(new Predicate[] {}));
      }

    }
    TypedQuery<Long> typedQuery = em.createQuery(query);
    return typedQuery;
  }

  public <M extends PersistedModel> Query getDeleteQuery(EntityManager em, Class<M> modelClass,
      WhereFilter where) {

    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(
        modelClass);


    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaDelete<M> delete = cb.createCriteriaDelete(modelClass);
    Root<M> root = delete.from(modelClass);


    if (where != null) {
      List<Predicate> predicates = getWherePredicates(root, delete, cb, modelClass, where);
      if (predicates.size() > 0) {
        delete.where(predicates.toArray(new Predicate[] {}));
      }

    }
    Query query = em.createQuery(delete);
    return query;
  }

  public <M extends PersistedModel> List<Predicate> getWherePredicates(Root<M> root,
      CommonAbstractCriteria query, CriteriaBuilder cb, Class<M> modelClass, WhereFilter where) {
    List<Predicate> predicates = new ArrayList<Predicate>();
    if (where != null) {

      ObjectMapper mapper = new ObjectMapper();
      ModelConfiguration configuration = ModelConfigurationManager.getInstance()
          .getModelConfiguration(
          modelClass);
      Map<String, Field> properties = PersistedModel.getProperties(modelClass);


      JsonNode w = where.getValue();
      Iterator<Map.Entry<String, JsonNode>> fieldItr = w.fields();
      while (fieldItr.hasNext()) {
        Map.Entry<String, JsonNode> fieldCondition = fieldItr.next();
        if (properties.containsKey(fieldCondition.getKey())) {
          Field field = properties.get(fieldCondition.getKey());
          String columnName = field.getName();
//          getColumnName(field);
          JsonNode value = fieldCondition.getValue();
          if (value.isValueNode()) {

            Object convertedValue = mapper.convertValue(value, field.getType());
            predicates.add(cb.equal(root.get(columnName), convertedValue));
          } else if (value.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> keyValueItr = value.fields();
            while (keyValueItr.hasNext()) {
              Map.Entry<String, JsonNode> fieldEntry = keyValueItr.next();
//                QueryOperator queryOp = QueryOperator.fromValue(fieldEntry.getKey());
//                if(queryOp == null) {
//                  throw new LoopbackException("Invalid query op in where filter");
//                }
//                if(fieldEntry.getValue().isArray() || fieldEntry.getValue().isObject()) {
//                  throw new LoopbackException("Invalid query op value in where filter");
//                }
            }
          }
        } else {
          // TODO no field property with this name
        }
      }
    }
    return predicates;
  }

  public <M extends PersistedModel> TypedQuery getSelectTypedQueryThrough(EntityManager em,
      WhereFilter relationScope,
      Relation relation, Filter throughFilter) {

    ModelConfiguration configuration = ModelConfigurationManager.getInstance()
        .getModelConfiguration(relation.getThroughModelClass());

//    if (!filter.) {
//      var idNames = this.idNames(model);
//      if (idNames && idNames.length) {
//        filter.order = idNames;
//      }
//    }

    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery query = cb.createQuery(relation.getThroughModelClass());
    Root throughtRoot = query.from(relation.getThroughModelClass());
    Root relatedRoot = query.from(relation.getRelatedModelClass());

    Map<String, Field> properties = PersistedModel.getProperties(relation.getThroughModelClass());
    Field joinField = properties.get(relation.getFromThroughPropertyName());

    Join relatedEntity = throughtRoot.join(joinField.getName());


    if (relationScope != null) {
      List<Predicate> predicates = getWherePredicates(throughtRoot, query, cb, relation.getThroughModelClass(),
          relationScope);
      if (predicates.size() > 0) {
        query.where(predicates.toArray(new Predicate[] {}));
      }
    }

    query.multiselect(throughtRoot, relatedRoot);

//    if(filter != null && filter.getOrder() != null && filter.getOrder().getOrderBy() != null) {
//      Map<String, Field> properties = PersistedModel.getProperties(modelClass);
//      List<Order> orders = Lists.newArrayList();
//
//      for(int i = 0; i < filter.getOrder().getOrderBy().size(); i++) {
//        OrderBy orderBy = filter.getOrder().getOrderBy().get(i);
//        Field field = properties.get(orderBy.getProperty());
//        String fieldName = field.getName();
//        if(orderBy.getType() == OrderType.DESC) {
//          orders.add(cb.desc(root.get(fieldName)));
//        }else {
//          orders.add(cb.asc(root.get(fieldName)));
//        }
//      }
//      if(orders.size() > 0) {
//        Order[] orderArray = new Order[orders.size()];
//        orderArray = orders.toArray(orderArray);
//        query = query.orderBy(orderArray);
//      }
//    }

    TypedQuery typedQuery = em.createQuery(query);
    return typedQuery;
  }
}