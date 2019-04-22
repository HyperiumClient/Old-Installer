/*
 *     Copyright (C) 2018  Hyperium <https://hyperium.cc/>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published
 *     by the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cc.hyperium.installer.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonHolder {
    private JsonObject object = new JsonObject();

    public JsonHolder(JsonObject object) {
        this.object = object;
    }

    public JsonHolder(String raw) {
        if (raw == null || raw.isEmpty()) {
            object = new JsonObject();
            return;
        }
        try {
            this.object = new JsonParser().parse(raw).getAsJsonObject();
        } catch (Exception e) {
            this.object = new JsonObject();
            e.printStackTrace();
        }
    }

    public JsonHolder() {
        this(new JsonObject());
    }

    @Override
    public String toString() {
        if (object != null)
            return object.toString();
        return "{}";
    }

    public JsonHolder put(String key, boolean value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonHolder put(String key, String value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonHolder put(String key, int value) {
        object.addProperty(key, value);
        return this;
    }

    public JsonHolder optJSONObject(String key, JsonObject fallBack) {
        try {
            return new JsonHolder(object.get(key).getAsJsonObject());
        } catch (Exception e) {
            return new JsonHolder(fallBack);
        }
    }

    public JsonArray optJSONArray(String key, JsonArray fallback) {
        try {
            return object.get(key).getAsJsonArray();
        } catch (Exception e) {
            return fallback;
        }
    }

    public JsonArray optJSONArray(String key) {
        return optJSONArray(key, new JsonArray());
    }

    public boolean has(String key) {
        return object.has(key);
    }

    public boolean optBoolean(String key, boolean fallback) {
        try {
            return object.get(key).getAsBoolean();
        } catch (Exception e) {
            return fallback;
        }
    }

    public boolean optBoolean(String key) {
        return optBoolean(key, false);
    }

    public JsonHolder optJSONObject(String key) {
        return optJSONObject(key, new JsonObject());
    }

    public String optString(String key, String fallBack) {
        try {
            return object.get(key).getAsString();
        } catch (Exception e) {
            return fallBack;
        }
    }

    public String optString(String key) {
        return optString(key, "");
    }

    public List<String> getKeys() {
        return object.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public JsonObject getObject() {
        return object;
    }

    public JsonHolder put(String values, JsonHolder values1) {
        return put(values, values1.getObject());
    }

    public JsonHolder put(String values, JsonObject object) {
        this.object.add(values, object);
        return this;
    }

    public JsonHolder put(String key, JsonArray value) {
        this.object.add(key, value);
        return this;
    }
}
