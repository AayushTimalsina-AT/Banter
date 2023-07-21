package com.banter.Utils;
import com.google.gson.Gson;

public class GsonUtils {

    private static Gson gson = new Gson();

    public static <T> String convertToJson(T object) {
        return gson.toJson(object);
    }

    public static <T> T convertFromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}

