package com.machopiggies.gameloaderapi.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.chat.BaseComponent;

public class ChatUtil {

    public static String getAsTitleJson(BaseComponent... baseComponents) {
        JsonArray array = new JsonArray();
        for (BaseComponent baseComponent : baseComponents) {
            if (baseComponent == null) continue;
            JsonObject object = new JsonObject();
            object.addProperty("text", baseComponent.toPlainText() != null ? baseComponent.toPlainText() : "");
            object.addProperty("color", baseComponent.getColor() != null ? baseComponent.getColor().name().toLowerCase() : "white");
            if (baseComponent.isBold()) {
                object.addProperty("bold", baseComponent.isBold());
            }
            if (baseComponent.isItalic()) {
                object.addProperty("italic", baseComponent.isItalic());
            }
            if (baseComponent.isUnderlined()) {
                object.addProperty("underlined", baseComponent.isUnderlined());
            }
            if (baseComponent.isStrikethrough()) {
                object.addProperty("strikethrough", baseComponent.isStrikethrough());
            }
            if (baseComponent.isObfuscated()) {
                object.addProperty("obfuscated", baseComponent.isObfuscated());
            }
            array.add(object);
        }
        return array.toString();
    }
}
