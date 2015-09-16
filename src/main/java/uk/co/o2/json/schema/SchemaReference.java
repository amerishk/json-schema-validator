package uk.co.o2.json.schema;

import com.fasterxml.jackson.databind.JsonNode;

import java.net.URL;
import java.util.List;

public class SchemaReference implements JsonSchema {
    private final SchemaPassThroughCache registry;
    private final URL schemaLocation;

    public SchemaReference(SchemaPassThroughCache registry, URL schemaLocation) {
        this.registry = registry;
        this.schemaLocation = schemaLocation;
    }

    @Override
    public List<ErrorMessage> validate(JsonNode jsonDocumentToValidate) {
        return getReferencedSchema().validate(jsonDocumentToValidate);
    }

    @Override
    public String getDescription() {
        return getReferencedSchema().getDescription();
    }

    @Override
    public boolean isAcceptableType(JsonNode jsonDocument) {
        return getReferencedSchema().isAcceptableType(jsonDocument);
    }

    private JsonSchema getReferencedSchema() {
        return registry.getSchema(schemaLocation);
    }

    public SchemaPassThroughCache getRegistry() {
        return registry;
    }

    public URL getSchemaLocation() {
        return schemaLocation;
    }
}
