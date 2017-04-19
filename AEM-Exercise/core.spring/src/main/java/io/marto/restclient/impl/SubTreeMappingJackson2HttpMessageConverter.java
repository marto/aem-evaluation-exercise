package io.marto.restclient.impl;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.join;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;

public class SubTreeMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    private final List<String> path;

    public SubTreeMappingJackson2HttpMessageConverter(String ...path) {
        this.path = Collections.unmodifiableList(Arrays.asList(path));
    }

//    @Override
//    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
//        LinkedList<String> path = new LinkedList<String>(this.path);
//        JsonNode node = (JsonNode) super.read(JsonNode.class, contextClass, inputMessage);
//
//        while (node != null && !path.isEmpty()) {
//            node = node.get(path.pop());
//        }
//
//        if (node == null) {
//            throw new HttpMessageNotReadableException(format("Could not read JSON: No object at /%s", join("/",path.toArray(new String[0]))));
//        }
//
//        return getObjectMapper().reader(type.).readValue(node);
//    }


    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        LinkedList<String> path = new LinkedList<String>(this.path);



        JavaType javaType = getJavaType(JsonNode.class, contextClass);
        JsonNode node = (JsonNode) readJavaType(javaType, inputMessage);


        while (node != null && !path.isEmpty()) {
            node = node.get(path.pop());
        }

        if (node == null) {
            throw new HttpMessageNotReadableException(format("Could not read JSON: No object at /%s", join("/",path.toArray(new String[0]))));
        }


        return getObjectMapper().reader((Class<?>)type).readValue(node);
    }

    private Object readJavaType(JavaType javaType, HttpInputMessage inputMessage) {
        try {
            return this.getObjectMapper().readValue(inputMessage.getBody(), javaType);
        }
        catch (IOException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        LinkedList<String> path = new LinkedList<String>(this.path);
        JsonNode node = (JsonNode) super.readInternal(JsonNode.class, inputMessage);

        while (node != null && !path.isEmpty()) {
            node = node.get(path.pop());
        }

        if (node == null) {
            throw new HttpMessageNotReadableException(format("Could not read JSON: No object at /%s", join("/",path.toArray(new String[0]))));
        }

        return getObjectMapper().reader(clazz).readValue(node);


//        catch (IOException ex) {
//            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
//        }

    }
}
