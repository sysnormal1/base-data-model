package com.sysnormal.data.base_data_model.helpers;

import com.sysnormal.commons.core.utils_core.ReflectionUtils;
import com.sysnormal.commons.core.utils_core.TextUtils;
import com.sysnormal.commons.spring.spring_data_utils.JpaReflectionUtils;
import com.sysnormal.data.base_data_model.entities.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * extends this class in your application, annotate it as component, and override entityManagerFactory property and runNativeInsert method with annotations
 */
public abstract class NativeMultiTransactionalStatementHelper {

    private static final Logger logger = LoggerFactory.getLogger(NativeMultiTransactionalStatementHelper.class);

    /*
    for multiple connections, don't use persitencecontext, here create new entitymanger, differente of caller transaction, causing error on detect if is same transaction

    DON`T GET ENTITYMANAGER AS BELLOW:
    @PersistenceContext(unitName = "sysnormal")
    private EntityManager entityManager;*/


    /*
    CREATE this parameter on your derived class and inject with correct qualifier, then return it in implementation of getEntityManagerFactory bellow:

    @Autowired <- annotate in your override property
    @Qualifier("myEntityManagerFactory") <- annotate in your override property
    private EntityManagerFactory entityManagerFactory;
    */
    public abstract EntityManagerFactory getEntityManagerFactory();

    /**
     * this method provides get the correct transaction, same in use by caller
     * @return
     */
    private EntityManager getTransactionalEntityManager() {
        EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(getEntityManagerFactory());
        if (em == null) {
            throw new IllegalStateException("No transactional EntityManager bound to current transaction for entityManagerFactory");
        }
        return em;
    }

    public HashMap<String, Object> getFieldsToInsert(BaseEntity entity) throws InvocationTargetException, IllegalAccessException {
        logger.debug("INIT {}.{}",this.getClass().getSimpleName(),"getFieldsToInsert");
        HashMap<String, Object> result = new HashMap<String, Object>();
        ArrayList<Field> fields = ReflectionUtils.getAllFields(entity.getClass());
        ArrayList<Method> methods = ReflectionUtils.getAllMethods(entity.getClass());
        for(Field field : fields) {
            Column columnAnnotation = field.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name();
                if (!TextUtils.hasText(columnName)) {
                    columnName = field.getName();
                }
                Object value = null;
                Method foundedGetMethod = null;
                for(int i = 0; i < methods.size(); i++) {
                    if (methods.get(i).getName().equalsIgnoreCase("get"+field.getName()) && methods.get(i).getParameterCount() == 0) {
                        foundedGetMethod = methods.get(i);
                        break;
                    }
                }
                if (foundedGetMethod != null) {
                    value = foundedGetMethod.invoke(entity);
                } else {
                    field.setAccessible(true);
                    value = field.get(entity);
                }
                if (value != null) {
                    result.put(columnName, value);
                }
            }
        }
        logger.debug("END {}.{}",this.getClass().getSimpleName(),"getFieldsToInsert");
        return result;
    }

    /**
     *
     * @param entity
     * @param fields
     * @return
     */
    public Query mountNativeSqlInsert(BaseEntity entity, HashMap<String, Object> fields) {
        logger.debug("INIT {}.{}",this.getClass().getSimpleName(),"mountNativeSqlInsert");
        Query result = null;
        String strQquery = "INSERT INTO "+ JpaReflectionUtils.resolveTableName(entity.getClass())+" (";
        strQquery += fields.keySet()
                .stream()
                .collect(Collectors.joining(","));
        strQquery += ") values (";
        strQquery += ":"+fields.keySet()
                .stream()
                .collect(Collectors.joining(",:"));
        strQquery += ")";
        EntityManager entityManager = getTransactionalEntityManager(); // <--- garante EM correto
        result = entityManager.createNativeQuery(strQquery);
        for (String key : fields.keySet()) {
            result.setParameter(key, fields.get(key));
        };
        logger.debug("END {}.{}",this.getClass().getSimpleName(),"mountNativeSqlInsert");
        return result;
    }

    /**
     * override this method and annotate it as transaction with same qualifier of entityManager and call this super method
     *
     * @param entity
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    //@Transactional("myTransactionManager") <- annotate in your override method
    public int runNativeInsert(BaseEntity entity) throws InvocationTargetException, IllegalAccessException {
        logger.debug("INIT {}.{}",this.getClass().getSimpleName(),"runNativeInsert");
        HashMap<String, Object> fields = getFieldsToInsert(entity);
        Query query = mountNativeSqlInsert(entity, fields);
        org.hibernate.query.NativeQuery<?> nQuery = query.unwrap(org.hibernate.query.NativeQuery.class);
        logger.debug("running query {}",nQuery.getQueryString());
        EntityManager entityManager = getTransactionalEntityManager(); // <--- garante EM correto
        logger.debug("EM hash dentro de runNativeInsert imediatamente antes de query.executeUpdate {}", System.identityHashCode(entityManager));
        logger.debug("END {}.{}",this.getClass().getSimpleName(),"runNativeInsert");
        return query.executeUpdate();
    }

}