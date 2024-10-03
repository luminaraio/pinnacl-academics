package io.pinnacl.core;

import io.pinnacl.commons.config.Config;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class TestConfig {

    public static Config getVertxTestConfig(Vertx vertx) {
        var yamlConfig = vertx.fileSystem()
                .readFileBlocking("src/test/resources/application.yml")
                .toString();
        var yaml = new Yaml();
        Map<String, Object> map = yaml.load(yamlConfig);
        var jsonConfig = new JsonObject(map);
        var jsonStore = new ConfigStoreOptions().setType("file")
                .setFormat("yaml")
                .setConfig(new JsonObject().put("path", "application.yml"));
        var configOptions = new ConfigRetrieverOptions();
        configOptions.addStore(jsonStore);
        var retriever = ConfigRetriever.create(vertx, configOptions);

        return Config.create(retriever, jsonConfig);
    }
}
