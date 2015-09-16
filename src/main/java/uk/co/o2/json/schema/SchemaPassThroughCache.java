package uk.co.o2.json.schema;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.co.o2.json.message.Messages;

import com.fasterxml.jackson.core.JsonFactory;

// TODO: Auto-generated Javadoc
/**
 * The Class SchemaPassThroughCache.
 */
public class SchemaPassThroughCache {

    /** The registered schemas. */
    final ConcurrentMap<String, JsonSchema> registeredSchemas = new ConcurrentHashMap<>();
    
    /** The schema compiler factory. */
    private SchemaCompilerFactory schemaCompilerFactory;
    
    private Messages msgs;

    /**
     * Instantiates a new schema pass through cache.
     *
     * @param factory the factory
     */
	public SchemaPassThroughCache(JsonFactory factory, Messages messages) {
        this.schemaCompilerFactory = new SchemaCompilerFactory(this, factory,messages);
        this.msgs = messages;
    }

    /*
        Uses setter injection only because of the circular reference
     */
    /**
     * Sets the schema compiler factory.
     *
     * @param schemaCompilerFactory the new schema compiler factory
     */
    void setSchemaCompilerFactory(SchemaCompilerFactory schemaCompilerFactory) {
        this.schemaCompilerFactory = schemaCompilerFactory;
    }

    /**
     * Gets the schema.
     *
     * @param schemaLocation the schema location
     * @return the schema
     */
    public JsonSchema getSchema(URL schemaLocation) {
        if (hasSchema(schemaLocation)) {
            return registeredSchemas.get(schemaLocation.toString());
        }
        return schemaCompilerFactory.create().parse(schemaLocation);
    }

    /**
     * Checks for schema.
     *
     * @param schemaLocation the schema location
     * @return true, if successful
     */
    public boolean hasSchema(URL schemaLocation) {
        return registeredSchemas.containsKey(schemaLocation.toString());
    }

    /**
     * Register schema.
     *
     * @param schemaLocation the schema location
     * @param schema the schema
     */
    void registerSchema(URL schemaLocation, JsonSchema schema) {
        registeredSchemas.putIfAbsent(schemaLocation.toString(), schema);
    }
    
    /**
     * A factory for creating SchemaCompiler objects.
     */
    static class SchemaCompilerFactory {

        /** The cache. */
        private final SchemaPassThroughCache cache;
        
        /** The factory. */
        private final JsonFactory factory;
        
        private Messages msgs;

        /**
         * Instantiates a new schema compiler factory.
         *
         * @param cache the cache
         * @param factory the factory
         */
        SchemaCompilerFactory(SchemaPassThroughCache cache, JsonFactory factory, Messages messages) {
            this.cache = cache;
            this.factory = factory;
            this.msgs = messages;
        }

        /**
         * Creates the.
         *
         * @return the schema compiler
         */
        SchemaCompiler create() {
            return new SchemaCompiler(cache, factory,msgs);
        }
    }
}