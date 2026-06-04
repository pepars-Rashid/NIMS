package mapper;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ObjectMapper {

    // ========== READ METHODS ==========

    public <T> T readValue(File file, Class<T> valueType) throws JSONException {
        if (file == null) throw new JSONException("File cannot be null");
        if (valueType == null) throw new JSONException("Class cannot be null");

        try {
            String json = readFile(file);
            return readValue(json, valueType);
        } catch (IOException e) {
            throw new JSONException("Failed to read file: " + file.getPath(), e);
        }
    }

    public <T> T readValue(String json, Class<T> valueType) throws JSONException {
        if (json == null) throw new JSONException("JSON string cannot be null");
        if (valueType == null) throw new JSONException("Class cannot be null");

        try {
            Object parsed = parseJSON(json);
            return convertToBean(parsed, valueType);
        } catch (JSONException e) {
            throw e;
        } catch (Exception e) {
            throw new JSONException("Failed to parse JSON", e);
        }
    }

    public <T> List<T> readValueAsList(File file, Class<T> elementType) throws JSONException {
        if (file == null) throw new JSONException("File cannot be null");
        if (elementType == null) throw new JSONException("Class cannot be null");

        try {
            String json = readFile(file);
            return readValueAsList(json, elementType);
        } catch (IOException e) {
            throw new JSONException("Failed to read file: " + file.getPath(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> readValueAsList(String json, Class<T> elementType) throws JSONException {
        if (json == null) throw new JSONException("JSON string cannot be null");
        if (elementType == null) throw new JSONException("Class cannot be null");

        try {
            Object parsed = parseJSON(json);
            if (!(parsed instanceof List)) {
                throw new JSONException("JSON root is not an array");
            }

            List<Object> rawList = (List<Object>) parsed;
            List<T> result = new ArrayList<>();

            for (Object item : rawList) {
                if (item instanceof Map) {
                    result.add(convertToBean(item, elementType));
                } else if (elementType.isInstance(item)) {
                    result.add(elementType.cast(item));
                } else if (item != null) {
                    result.add(convertPrimitive(item, elementType));
                } else {
                    result.add(null);
                }
            }
            return result;
        } catch (ClassCastException e) {
            throw new JSONException("Expected JSON array", e);
        }
    }

    // ========== WRITE METHODS ==========

    public void writeValue(File file, Object value) throws JSONException {
        if (file == null) throw new JSONException("File cannot be null");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(writeValueAsString(value));
        } catch (IOException e) {
            throw new JSONException("Failed to write to file: " + file.getPath(), e);
        }
    }

    public String writeValueAsString(Object value) throws JSONException {
        try {
            if (value == null) return "null";
            if (value instanceof String) return "\"" + escapeString((String) value) + "\"";
            if (value instanceof Number || value instanceof Boolean) return value.toString();
            if (value instanceof Map) return mapToJson((Map<?, ?>) value);
            if (value instanceof List) return listToJson((List<?>) value);
            if (value.getClass().isArray()) return arrayToJson(value);
            return beanToJson(value);
        } catch (Exception e) {
            throw new JSONException("Failed to serialize object", e);
        }
    }

    // ========== PRIVATE HELPERS ==========

    private String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    private Object parseJSON(String json) throws JSONException {
        JSONTokenizer tokenizer = new JSONTokenizer(json);
        List<JSONTokenizer.Token> tokens = tokenizer.tokenize();
        JSONParser parser = new JSONParser(tokens);
        return parser.parse();
    }

    @SuppressWarnings("unchecked")
    private <T> T convertToBean(Object json, Class<T> clazz) throws JSONException {
        try {
            if (json == null) {
                return null;
            }

            // Handle basic types
            if (clazz == String.class || clazz == Integer.class || clazz == int.class ||
                    clazz == Double.class || clazz == double.class || clazz == Boolean.class ||
                    clazz == boolean.class || clazz == Long.class || clazz == long.class ||
                    clazz == Float.class || clazz == float.class) {
                return convertPrimitive(json, clazz);
            }

            // Handle Map -> Bean
            if (json instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) json;
                T instance = clazz.getDeclaredConstructor().newInstance();

                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);
                    String fieldName = field.getName();

                    if (map.containsKey(fieldName)) {
                        Object value = map.get(fieldName);
                        setFieldValue(instance, field, value);
                    }
                    // If field doesn't exist in JSON, skip it - Java will keep the default value
                }
                return instance;
            }

            // Already correct type
            if (clazz.isInstance(json)) {
                return clazz.cast(json);
            }

            throw new JSONException("Cannot convert to " + clazz.getName());
        } catch (Exception e) {
            throw new JSONException("Failed to convert to " + clazz.getName(), e);
        }
    }

    private void setFieldValue(Object instance, Field field, Object value) throws Exception {
        Class<?> fieldType = field.getType();

        // Handle nested objects
        if (value instanceof Map && !Map.class.isAssignableFrom(fieldType)) {
            value = convertToBean(value, fieldType);
        }
        // Handle lists
        else if (value instanceof List && List.class.isAssignableFrom(fieldType)) {
            value = convertList((List<?>) value, field);
        }
        // Convert primitives
        else if (!fieldType.isInstance(value)) {
            value = convertPrimitive(value, fieldType);
        }

        field.set(instance, value);
    }

    private Object convertList(List<?> rawList, Field field) throws JSONException {
        try {
            Type genericType = field.getGenericType();

            if (!(genericType instanceof ParameterizedType pType)) {
                return rawList;
            }

            Type[] typeArgs = pType.getActualTypeArguments();

            if (typeArgs.length == 0 || !(typeArgs[0] instanceof Class<?> itemClass)) {
                return rawList;
            }

            List<Object> result = new ArrayList<>();

            for (Object item : rawList) {
                if (item instanceof Map) {
                    result.add(convertToBean(item, itemClass));
                } else if (itemClass.isInstance(item)) {
                    result.add(item);
                } else if (item != null) {
                    result.add(convertPrimitive(item, itemClass));
                } else {
                    result.add(null);
                }
            }
            return result;
        } catch (Exception e) {
            throw new JSONException("Failed to convert list for field: " + field.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T convertPrimitive(Object value, Class<T> targetType) throws JSONException {
        try {
            if (targetType == String.class) {
                return (T) value.toString();
            }

            if (value instanceof Number num) {
                if (targetType == Integer.class || targetType == int.class) {
                    return (T) Integer.valueOf(num.intValue());
                } else if (targetType == Double.class || targetType == double.class) {
                    return (T) Double.valueOf(num.doubleValue());
                } else if (targetType == Long.class || targetType == long.class) {
                    return (T) Long.valueOf(num.longValue());
                } else if (targetType == Float.class || targetType == float.class) {
                    return (T) Float.valueOf(num.floatValue());
                }
            }

            if (value instanceof Boolean) {
                if (targetType == Boolean.class || targetType == boolean.class) {
                    return (T) value;
                }
            }

            // Try parsing from string
            String str = value.toString();
            if (targetType == Integer.class || targetType == int.class) {
                return (T) Integer.valueOf(Integer.parseInt(str));
            } else if (targetType == Double.class || targetType == double.class) {
                return (T) Double.valueOf(Double.parseDouble(str));
            } else if (targetType == Boolean.class || targetType == boolean.class) {
                return (T) Boolean.valueOf(Boolean.parseBoolean(str));
            } else if (targetType == Long.class || targetType == long.class) {
                return (T) Long.valueOf(Long.parseLong(str));
            } else if (targetType == Float.class || targetType == float.class) {
                return (T) Float.valueOf(Float.parseFloat(str));
            }

            throw new JSONException("Unsupported type: " + targetType);
        } catch (NumberFormatException e) {
            throw new JSONException("Invalid number format: " + value, e);
        }
    }

    private String beanToJson(Object bean) throws JSONException {
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Field field : bean.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(bean));
            }
            return mapToJson(map);
        } catch (Exception e) {
            throw new JSONException("Failed to serialize bean", e);
        }
    }

    private String mapToJson(Map<?, ?> map) throws JSONException {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;

            sb.append("\"").append(escapeString(entry.getKey().toString())).append("\":");
            sb.append(writeValueAsString(entry.getValue()));
        }

        sb.append("}");
        return sb.toString();
    }

    private String listToJson(List<?> list) throws JSONException {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(writeValueAsString(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    private String arrayToJson(Object array) throws JSONException {
        StringBuilder sb = new StringBuilder("[");
        int length = Array.getLength(array);

        for (int i = 0; i < length; i++) {
            if (i > 0) sb.append(",");
            sb.append(writeValueAsString(Array.get(array, i)));
        }

        sb.append("]");
        return sb.toString();
    }

    private String escapeString(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}