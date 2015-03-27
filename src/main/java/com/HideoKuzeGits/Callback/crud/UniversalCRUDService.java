package com.HideoKuzeGits.Callback.crud;

import com.HideoKuzeGits.Callback.UsefulStaticMethods;
import com.HideoKuzeGits.Callback.domain.AbstractUserOwn;
import com.HideoKuzeGits.Callback.domain.CallbackUser;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by root on 19.10.14.
 */
@Service
@Transactional
public class UniversalCRUDService implements BeanFactoryAware {


    public static final String DOMAIN_OBJECTS_PACKAGE = "com.HideoKuzeGits.Callback.domain.";
    @PersistenceContext
    private EntityManager em;
    private BeanFactory beanFactory;


    /// -------------------- CREATE ---------------------///
    @PreAuthorize("hasRole('ROLE_USER')")
    public void create(User activeUser, String json) throws
            ClassNotFoundException, CrudOperationException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {

        String email = activeUser.getUsername();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        Class<?> domainObjectClass = getTargetClassFromJson(jsonObject);

        removeExcludedFields(jsonObject, domainObjectClass);
        json = null;

        if (!AbstractUserOwn.class.isAssignableFrom(domainObjectClass))
            throw new CrudOperationException("Type is not user property.");

        AbstractUserOwn abstractUserOwn = (AbstractUserOwn) new Gson().fromJson(jsonObject, domainObjectClass);

        if (em.find(domainObjectClass, abstractUserOwn.getId()) != null)
            throw new CrudOperationException("Object is already present in database.");

        setOwner(abstractUserOwn, email);
        wirePropertiesFromDatabase(abstractUserOwn, activeUser);
        em.persist(abstractUserOwn);

    }

    private Class<?> getTargetClassFromJson(JsonObject jsonObject) throws ClassNotFoundException, CrudOperationException {
        JsonElement jsonElement = jsonObject.get("type");
        if (jsonElement == null)
            throw new CrudOperationException("Type parameter was not specified.");
        String className = jsonElement.getAsString();
        jsonObject.remove("type");
        return getClassByName(className);
    }

    private void wirePropertiesFromDatabase(AbstractUserOwn abstractUserOwn, User activeUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, CrudOperationException {

        Class abstractUserOwnType = abstractUserOwn.getClass();
        Field[] fields = getAllFields(abstractUserOwnType);

        for (Field field : fields) {

            Class<?> type = field.getType();

            if (AbstractUserOwn.class.isAssignableFrom(type))
                wirePropertyFromDatabase(abstractUserOwn, field, activeUser);

            if (isFieldTypeUserOwnList(field))
                wireListPropertyFromDatabase(abstractUserOwn, field, activeUser);

        }
    }

    private void setOwner(AbstractUserOwn abstractUserOwn, String email) {

        CallbackUser callbackUser = em.find(CallbackUser.class, email);
        abstractUserOwn.setOwner(callbackUser);
    }

    /// -------------------- READ ---------------------///
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<AbstractUserOwn> read(User activeUser, String json) throws ClassNotFoundException, CrudOperationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        String email = activeUser.getUsername();

        IdAndType idAndType = new Gson().fromJson(json, IdAndType.class);
        String className = idAndType.getType();

        if (className == null)
            throw new CrudOperationException("Type don`t specified.");

        Class<?> domainObjectClass = getClassByName(className);
        String id = idAndType.getId();
        List<AbstractUserOwn> resultList = null;

        if (!AbstractUserOwn.class.isAssignableFrom(domainObjectClass))
            throw new CrudOperationException("Type is not user property.");

        if (idAndType.getId() != null) {
            resultList = new ArrayList<AbstractUserOwn>();
            AbstractUserOwn domainObject = getDomainObject(className, id, activeUser);
            resultList.add(domainObject);
        } else {

            Query query = em.createQuery("SELECT DISTINCT p from " + className + " p " +
                    "INNER JOIN p.owner c WHERE c.email = :email");
            query.setParameter("email", email);
            resultList = query.getResultList();
        }

        for (AbstractUserOwn result : resultList) {
            em.detach(result);
            removeCircularReferences(result);
            removeExcludedFields(result);
        }

        return resultList;
    }

    public void removeCircularReferences(Object targetObject) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {


        Field[] fields = getAllFields(targetObject.getClass());

        for (Field field : fields) {

            Object fieldValue = PropertyUtils.getProperty(targetObject, field.getName());

            if (isDomainObject(field.getType()))
                removePropertiesOfType(fieldValue, targetObject.getClass());

            else if (isDomainObjectList(field))
                for (java.lang.Object objectInList : (List) fieldValue)
                    removePropertiesOfType(objectInList, targetObject.getClass());

        }
    }

    private boolean isDomainObjectList(Field field) {

        Class<?> type = field.getType();

        if (List.class.isAssignableFrom(type)) {
            Class genericTypeOfList = getListGenericType(field);
            return AbstractUserOwn.class.isAssignableFrom(genericTypeOfList);
        }

        return false;
    }

    private void removePropertiesOfType(Object targetObject, Class<? extends Object> type) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {


        if (targetObject == null)
            return;
        em.detach(targetObject);


        Field[] fields = getAllFields(targetObject.getClass());

        for (Field field : fields) {

            Class fieldType = field.getType();

            if (isDomainObjectList(field))
                fieldType = getListGenericType(field);

            if (type.isAssignableFrom(fieldType))
                PropertyUtils.setProperty(targetObject, field.getName(), null);
        }
    }

    /// -------------------- UPDATE ---------------------///
    @PreAuthorize("hasRole('ROLE_USER')")
    public String update(User activeUser, String json) throws CrudOperationException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        String email = activeUser.getUsername();

        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        Class<?> domainObjectClass = getTargetClassFromJson(jsonObject);

        removeExcludedFields(jsonObject, domainObjectClass);
        json = null;

        if (!AbstractUserOwn.class.isAssignableFrom(domainObjectClass))
            throw new CrudOperationException("Type is not user property.");

        AbstractUserOwn userOwn = (AbstractUserOwn) new Gson().fromJson(jsonObject, domainObjectClass);

        String name = domainObjectClass.getSimpleName();
        String id = userOwn.getId();
        AbstractUserOwn userOwnFromDatabase
                = getDomainObject(name, id, activeUser);

        if (userOwnFromDatabase == null)
            throw new CrudOperationException("There is not targetObject in database.");

        mergeUserOwns(userOwnFromDatabase, userOwn, activeUser);

        return "";
    }

    private void mergeUserOwns(AbstractUserOwn userOwnFromDatabase, AbstractUserOwn userOwn, User activeUser) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException, CrudOperationException {

        Field[] declaredFields = getAllFields(userOwnFromDatabase.getClass());

        for (Field field : declaredFields) {

            if (field.isAnnotationPresent(Updatable.class)) {

                Object property = PropertyUtils.getProperty(userOwn, field.getName());
                BeanUtils.copyProperty(userOwnFromDatabase, field.getName(), property);


                Class<?> type = field.getType();

                if (AbstractUserOwn.class.isAssignableFrom(type))
                    wirePropertyFromDatabase(userOwnFromDatabase, field, activeUser);

                if (isFieldTypeUserOwnList(field))
                    wireListPropertyFromDatabase(userOwnFromDatabase, field, activeUser);
            }

        }
    }

    /// -------------------- DELETE ---------------------///
    @PreAuthorize("hasRole('ROLE_USER')")
    public void delete(User activeUser, String json) throws NoSuchMethodException, CrudOperationException, ClassNotFoundException {


        IdAndType idAndType = new Gson().fromJson(json, IdAndType.class);
        String className = idAndType.getType();
        String id = idAndType.getId();

        AbstractUserOwn domainObject = getDomainObject(className, id, activeUser);

        em.remove(domainObject);

    }

    private AbstractUserOwn getDomainObject(String className, String id, User activeUser) throws ClassNotFoundException, NoSuchMethodException, CrudOperationException {

        if (Strings.isNullOrEmpty(id))
            throw new CrudOperationException("Id not specified");

        String email = activeUser.getUsername();

        Class<?> domainObjectClass = getClassByName(className);

        if (!AbstractUserOwn.class.isAssignableFrom(domainObjectClass))
            throw new CrudOperationException("Type is not user property/");

        AbstractUserOwn domainObject = (AbstractUserOwn) em.find(domainObjectClass, id);

        if (domainObject == null)
            throw new CrudOperationException("{\"status\" :  0,  \"message\" : '" + className + " don`t exist'.}");

        CallbackUser owner = domainObject.getOwner();

        if (!owner.getEmail().equals(email))
            throw new CrudOperationException("Not enough permissions.");


        return domainObject;
    }

    private Class<?> getClassByName(String className) throws ClassNotFoundException {
        return Class.forName(DOMAIN_OBJECTS_PACKAGE + className);
    }

    private boolean isFieldTypeUserOwnList(Field field) {

        Class<?> type = field.getType();

        if (List.class.isAssignableFrom(type)) {
            Class genericTypeOfList = getListGenericType(field);
            return AbstractUserOwn.class.isAssignableFrom(genericTypeOfList);
        }

        return false;
    }

    private void wireListPropertyFromDatabase(AbstractUserOwn abstractUserOwn, Field field, User activeUser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, CrudOperationException, ClassNotFoundException {


        List<AbstractUserOwn> userOwnsListProperty
                = (List<AbstractUserOwn>) PropertyUtils.getProperty(abstractUserOwn, field.getName());

        List<AbstractUserOwn> propertyFromDatabase = new ArrayList<AbstractUserOwn>();

        if (userOwnsListProperty != null)
            for (AbstractUserOwn userOwn : userOwnsListProperty) {

                String className = userOwn.getClass().getSimpleName();
                String id = userOwn.getId();
                AbstractUserOwn valueFromDatabase = getDomainObject(className, id, activeUser);
                propertyFromDatabase.add(valueFromDatabase);
            }

        org.apache.commons.beanutils.BeanUtils.
                setProperty(abstractUserOwn, field.getName(), propertyFromDatabase);
    }

    private void wirePropertyFromDatabase(AbstractUserOwn abstractUserOwn, Field field, User activeUser) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, CrudOperationException, ClassNotFoundException {

        String className = field.getClass().getSimpleName();
        String id = abstractUserOwn.getId();
        Object propertyFromDatabase
                = getDomainObject(className, id, activeUser);
        org.apache.commons.beanutils.BeanUtils.
                setProperty(abstractUserOwn, field.getName(), propertyFromDatabase);
    }

    private void removeExcludedFields(JsonObject jsonObject, Class<?> domainObjectClass) {

        ArrayList<Class> checkedClasses = new ArrayList<Class>();
        removeExcludedFields(jsonObject, domainObjectClass, checkedClasses);
    }

    private void removeExcludedFields(JsonObject jsonObject,
                                      Class<?> domainObjectClass,
                                      List<Class> checkedClasses) {


        if (!isDomainObject(domainObjectClass)) return;
        if (checkedClasses.contains(domainObjectClass)) return;
        if (jsonObject == null) return;
        if (jsonObject.isJsonNull()) return;


        checkedClasses.add(domainObjectClass);

        Field[] fields = getAllFields(domainObjectClass);

        for (Field field : fields) {

            if (field.isAnnotationPresent(ExcludeFromCrudOperations.class))
                jsonObject.remove(field.getName());

            Class<?> fieldType = field.getType();
            String fieldName = field.getName();

            JsonElement jsonElement = jsonObject.get(fieldName);


            if (jsonElement != null && jsonElement.isJsonObject()) {
                jsonObject = jsonElement.getAsJsonObject();
                removeExcludedFields(jsonObject, fieldType, checkedClasses);
            }


        }


    }

    public void removeExcludedFields(Object domainObject) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        if (domainObject == null)
            return;

        Field[] fields = getAllFields(domainObject.getClass());

        for (Field field : fields)
            if (field.isAnnotationPresent(ExcludeFromCrudOperations.class))
                PropertyUtils.setProperty(domainObject, field.getName(), null);
            else if (!field.getType().isPrimitive() &&
                    field.getType().getPackage().toString().contains("com.HideoKuzeGits.Callback.domain")) {
                Object property = PropertyUtils.getProperty(domainObject, field.getName());
                removeExcludedFields(property);
            }

    }

    private boolean isDomainObject(Class clazz) {

        return clazz.isAnnotationPresent(Entity.class) ||
                clazz.isAnnotationPresent(Embedded.class) ||
                clazz.isAnnotationPresent(MappedSuperclass.class);
    }

    private Class getListGenericType(Field field) {
        ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
        return (Class) parameterizedType.getActualTypeArguments()[0];
    }

    public UniversalCRUDService getRealisationForType(String json) {

        try {
            JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
            String className = jsonObject.get("type").getAsString();
            String beanName = className + "CRUDService";
            beanName = UsefulStaticMethods.firstLetterToLowerCase(beanName);
            return beanFactory.getBean(beanName, UniversalCRUDService.class);
        } catch (Exception e) {
            return this;
        }
    }

    private Field[] getAllFields(Class type) {


        ArrayList<Field> fields = new ArrayList<Field>();

        while (type != null) {
            Field[] declaredFields = type.getDeclaredFields();
            fields.addAll(Arrays.asList(declaredFields));
            type = type.getSuperclass();
        }
        return fields.toArray(new Field[fields.size()]);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

        this.beanFactory = beanFactory;
    }

}
