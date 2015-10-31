package com.automation.crawler;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseExportedData {

    private List<ParseCloudSchema> results = new ArrayList<ParseCloudSchema>();
    
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The results
     */
    public List<ParseCloudSchema> getRecipeInfo() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setRecipeInfo(List<ParseCloudSchema> results) {
        this.results = results;
    }

    public Map<String, Object> getAdditionalProperty() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static class ParseCloudSchema implements Serializable{
    	public String category;
    	public int recipeinfo_id;
    	public String cooking_time;
        public String description;
        public String title;
        public String serving;
        }
}
