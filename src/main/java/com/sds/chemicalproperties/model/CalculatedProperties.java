package com.sds.chemicalproperties.model;

import java.util.ArrayList;
import java.util.List;

import com.sds.domain.Issue;
import com.sds.domain.Property;;

public class CalculatedProperties {
    protected List<Issue> Issues = new ArrayList<Issue>();
    protected List<Property> Properties = new ArrayList<Property>();

    public CalculatedProperties(List<Issue> issues, List<Property> properties) {
        Issues = issues;
        Properties = properties;
    }

    public List<Issue> getIssues() {
        return Issues;
    }

    public List<Property> getProperties() {
        return Properties;
    }
}
