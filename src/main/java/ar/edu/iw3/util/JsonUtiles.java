package ar.edu.iw3.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JsonUtiles {
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ObjectMapper getObjectMapper(Class clazz, StdSerializer ser, String dateFormat) {
        ObjectMapper mapper = new ObjectMapper();
        String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
        if (dateFormat != null)
            defaultFormat = dateFormat;
        SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
        SimpleModule module = new SimpleModule();
        if (ser != null) {
            module.addSerializer(clazz, ser);
        }
        mapper.setDateFormat(df);
        mapper.registerModule(module);
        return mapper;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static ObjectMapper getObjectMapper(Class clazz, StdDeserializer deser, String dateFormat) {
        ObjectMapper mapper = new ObjectMapper();
        String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
        if (dateFormat != null)
            defaultFormat = dateFormat;
        SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
        SimpleModule module = new SimpleModule();
        if (deser != null) {
            module.addDeserializer(clazz, deser);
        }
        mapper.setDateFormat(df);
        mapper.registerModule(module);
        return mapper;
    }

    /**
     * Obtiene una cadena con la siguiente lógica:
     * 1) Busca en cada uno de los atributos definidos en el arreglo "attrs",
     *    el primero que encuentra será el valor retornado.
     * 2) Si no se encuentra ninguno de los atributos del punto 1), se
     *    retorna "defaultValue".
     * Ejemplo: supongamos que "node" represente: {"code":"c1, "codigo":"c11", "stock":true}
     *   getString(node, String[]{"codigo","cod"},"-1") retorna: "cl1"
     *   getString(node, String[]{"cod_prod","c_prod"},"-1") retorna: "-1"
     * @param node
     * @param attrs
     * @param defaultValue
     * @return
     */

    public static String getString(JsonNode node, String[] attrs, String defaultValue) {
        String r = null;
        for (String attr : attrs) {
            if (node.get(attr) != null) {
                r = node.get(attr).asText();
                break;
            }
        }
        if (r == null)
            r = defaultValue;
        return r;
    }

    public static double getDouble(JsonNode node, String[] attrs, double defaultValue) {
        Double r = null;
        for (String attr : attrs) {
            if (node.get(attr) != null && node.get(attr).isDouble()) {
                r = node.get(attr).asDouble();
                break;
            }
        }
        if (r == null)
            r = defaultValue;
        return r;
    }

    public static boolean getBoolean(JsonNode node, String[] attrs, boolean defaultValue) {
        Boolean r = null;
        for (String attr : attrs) {
            if (node.get(attr) != null && node.get(attr).isBoolean()) {
                r = node.get(attr).asBoolean();
                break;
            }
        }
        if (r == null)
            r = defaultValue;
        return r;
    }

    private static final List<String> DATE_FORMATS = List.of(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd",
            "MM/dd/yyyy",
            "dd-MM-yyyy",
            "yyyy-MM-dd'T'HH:mm:ss"
    );

    public static Date getDate(JsonNode node, String[] attrs, String defaultValue) {
        for (String attr : attrs) {
            if (node.has(attr) && !node.get(attr).isNull()) {
                String dateStr = node.get(attr).asText();
                Date date = parseDate(dateStr);
                if (date != null) {
                    return date;
                }
            }
        }
        // Si no se encuentra una fecha válida en los atributos, intentar con el valor predeterminado
        return parseDate(defaultValue);
    }

    private static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) return null;

        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false); // Desactiva el análisis laxo
                return sdf.parse(dateStr);
            } catch (ParseException ignored) {
            }
        }
        return null; // Devuelve null si no se encuentra un formato coincidente
    }

    public static long getLong(JsonNode node, String[] orderNumber, long defaultValue) {
        for (String key : orderNumber) {
            if (node.has(key) && !node.get(key).isNull()) {
                try {
                    // Intentar extraer el valor flotante y convertir a long
                    return Math.round(node.get(key).asDouble());
                } catch (NumberFormatException e) {
                    // Maneja el caso de formato incorrecto si ocurre
                    System.err.println("Error: el valor del nodo no es un número válido.");
                }
            }
        }

        // Intentar convertir el valor predeterminado en caso de que no se encuentre el atributo
        try {
            return defaultValue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor predeterminado no válido: " + defaultValue);
        }
    }

    public static float getFloat(JsonNode node, String[] orderNumber, float defaultValue) {
        for (String key : orderNumber) {
            if (node.has(key) && !node.get(key).isNull()) {
                try {
                    // Extrae el valor y lo convierte a float
                    return (float) node.get(key).asDouble();
                } catch (NumberFormatException e) {
                    System.err.println("Error: el valor del nodo no es un número válido para la clave '" + key + "'.");
                }
            }
        }

        // Convertir el valor predeterminado si no se encontró un valor válido en los atributos
        try {
            return defaultValue;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor predeterminado no válido: " + defaultValue);
        }
    }

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T getObject(JsonNode node, String[] keys, Class<T> type) {
        for (String key : keys) {
            if (node.has(key) && !node.get(key).isNull()) {
                try {
                    // Intenta convertir el nodo JSON al tipo especificado
                    return objectMapper.treeToValue(node.get(key), type);
                } catch (Exception e) {
                    System.err.println("Error al convertir el valor de '" + key + "' al tipo " + type.getSimpleName() + ": " + e.getMessage());
                }
            }
        }
        // Si no se encontró un valor válido, devuelve el valor predeterminado
        return null;
    }

}
