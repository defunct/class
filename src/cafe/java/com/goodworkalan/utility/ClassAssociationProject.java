package com.goodworkalan.utility;

import com.goodworkalan.cafe.ProjectModule;
import com.goodworkalan.cafe.builder.Builder;
import com.goodworkalan.cafe.outline.JavaProject;

/**
 * Builds the project definition for Class Association.
 *
 * @author Alan Gutierrez
 */
public class ClassAssociationProject implements ProjectModule {
    /**
     * Build the project definition for Class Association.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.class/class-association/0.1.0.1")
                .depends()
                    .development("org.testng/testng-jdk15/5.10")
                    .end()
                .end()
            .end();
    }
}
