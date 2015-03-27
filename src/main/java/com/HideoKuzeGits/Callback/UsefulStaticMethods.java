package com.HideoKuzeGits.Callback;


import com.google.common.collect.ArrayListMultimap;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by root on 08.08.14.
 */
public class UsefulStaticMethods {


    //Including begin and end
    public static String removeBetween(String source, String begin, String end) {

        int beginIndex = source.indexOf(begin);
        int endIndex = source.indexOf(end, beginIndex);

        if (endIndex == -1)
            endIndex = source.length() - 1;

        String substring = source.substring(beginIndex, endIndex + 1);

        return source.replace(substring, "");

    }

    //Not including begin and end
    public static String getStringBetween(String source, String begin, String end) {

        int beginIndex = source.indexOf(begin);
        int endIndex = source.indexOf(end, beginIndex);

        if (beginIndex == -1 || endIndex == -1)
            throw new StringIndexOutOfBoundsException();

        beginIndex += begin.length();

        return source.substring(beginIndex, endIndex);
    }

    public static String getUrlParameter(String url, String parameterName) {
        return getStringBetween(url + "&", parameterName + "=", "&");
    }

    public static String transliterateString(String s) {

        s = s.toLowerCase();


        s = s.replaceAll("а", "a");
        s = s.replaceAll("б", "b");
        s = s.replaceAll("в", "v");
        s = s.replaceAll("г", "g");
        s = s.replaceAll("д", "d");
        s = s.replaceAll("е", "e");
        s = s.replaceAll("ё", "yo");
        s = s.replaceAll("ж", "j");
        s = s.replaceAll("з", "z");
        s = s.replaceAll("и", "i");
        s = s.replaceAll("й", "yi");
        s = s.replaceAll("к", "k");
        s = s.replaceAll("л", "l");
        s = s.replaceAll("м", "m");
        s = s.replaceAll("н", "n");
        s = s.replaceAll("о", "o");
        s = s.replaceAll("п", "p");
        s = s.replaceAll("р", "r");
        s = s.replaceAll("с", "s");
        s = s.replaceAll("т", "t");
        s = s.replaceAll("у", "u");
        s = s.replaceAll("ф", "f");
        s = s.replaceAll("х", "k");
        s = s.replaceAll("ц", "c");
        s = s.replaceAll("ч", "ch");
        s = s.replaceAll("ш", "sh");
        s = s.replaceAll("щ", "sch");
        s = s.replaceAll("ъ", "");
        s = s.replaceAll("ы", "i");
        s = s.replaceAll("ь", "");
        s = s.replaceAll("э", "e");
        s = s.replaceAll("ю", "yu");
        s = s.replaceAll("я", "ya");

        s = s.replaceAll("\\[", "K");
        s = s.replaceAll("]", "K");
        s = s.replaceAll("\"\"", "A");
        s = s.replaceAll("\"", "A");
        s = s.replaceAll("[ ]+", "_");

        s = s.replaceAll("[^a-z,\\[,\\],!,+,.,\\,,_,0-9,,A,K]+", "");

        return s;
    }

    public static String getYesterdayDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        return dateFormat.format(calendar.getTime());
    }

    public static String getYesterdayDateTimeString() {

        String yesterdayDateString = getYesterdayDateString();

        return yesterdayDateString + "T00:00:00";
    }

    public static String removeUrlParameter(String url, String parameterName) {


        while (true) {
            try {
                url = removeBetween(url, parameterName + "=", "&");
            } catch (StringIndexOutOfBoundsException e) {
                return url;
            }

        }

    }


    public static <K, V> Map<K, V> convertToMap(List<V> list, String fieldName) throws IllegalAccessException, NoSuchFieldException {

        Map<K, V> map = new HashMap<K, V>();

        Class<?> clazz = list.get(0).getClass();

        Field field = getField(clazz, fieldName);


        field.setAccessible(true);

        for (V value : list) {

            K key = (K) field.get(value);
            map.put(key, value);
        }

        return map;
    }

    public static <K, V> ArrayListMultimap<K, V> convertToMultiMap(List<V> list, String fieldName) throws IllegalAccessException, NoSuchFieldException {

        ArrayListMultimap<K, V> multimap = ArrayListMultimap.create();

        Class<?> clazz = list.get(0).getClass();

        Field field = getField(clazz, fieldName);


        field.setAccessible(true);

        for (V value : list) {

            K key = (K) field.get(value);
            multimap.put(key, value);
        }

        return multimap;
    }


    private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {

        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null)
                return getField(superclass, fieldName);
            else
                throw e;
        }

    }

    public static boolean isNullOrEmpty(Collection coll) {

        if (coll == null)
            return true;
        else
            return coll.isEmpty();
    }

    public static String fileToString(String pathname) {

        InputStream in = UsefulStaticMethods.class.getResourceAsStream(pathname);
        return new Scanner(in).useDelimiter("\\A").next();


    }

    public static String firstLetterToLowerCase(String beanName) {
        return Character.toLowerCase(beanName.charAt(0)) + (beanName.length() > 1 ? beanName.substring(1) : "");
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static <M> void merge(M originBeen, M newBeen) throws Exception {

        BeanInfo beanInfo = Introspector.getBeanInfo(originBeen.getClass());


        for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
            if (descriptor.getWriteMethod() != null) {
                Object originalValue = descriptor.getReadMethod().invoke(originBeen);
                Object newValue = descriptor.getReadMethod().invoke(newBeen);
                if (newValue != null && !"".equals(newValue))
                    descriptor.getWriteMethod().invoke(originBeen, newValue);

            }
        }
    }

}

