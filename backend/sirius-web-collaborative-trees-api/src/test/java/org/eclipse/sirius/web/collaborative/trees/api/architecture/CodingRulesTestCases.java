/*******************************************************************************
 * Copyright (c) 2019, 2020 Obeo.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package org.eclipse.sirius.web.collaborative.trees.api.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

import org.eclipse.sirius.web.annotations.graphql.GraphQLInputObjectType;
import org.eclipse.sirius.web.annotations.graphql.GraphQLObjectType;
import org.eclipse.sirius.web.services.api.dto.IInput;
import org.eclipse.sirius.web.services.api.dto.IPayload;
import org.eclipse.sirius.web.tests.architecture.AbstractCodingRulesTestCases;
import org.junit.Test;

/**
 * General purpose coding rules of the project.
 *
 * @author sbegaudeau
 */
public class CodingRulesTestCases extends AbstractCodingRulesTestCases {

    @Override
    protected String getProjectRootPackage() {
        return ArchitectureConstants.SIRIUS_WEB_COLLABORATIVE_TREES_API_ROOT_PACKAGE;
    }

    @Override
    protected JavaClasses getClasses() {
        return ArchitectureConstants.CLASSES;
    }

    @Test
    @Override
    public void noClassesShouldUseEMF() {
        super.noClassesShouldUseEMF();
    }

    @Test
    @Override
    public void noClassesShouldUseApacheCommons() {
        super.noClassesShouldUseApacheCommons();
    }

    @Test
    public void classesAnnotatedAsInputShouldBeWellStructured() {
        // @formatter:off
        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .areAnnotatedWith(GraphQLInputObjectType.class)
                .should()
                .implement(IInput.class)
                .andShould()
                .haveSimpleNameEndingWith("Input"); //$NON-NLS-1$
        // @formatter:on

        rule.check(this.getClasses());
    }

    @Test
    public void classesImplementingInputShouldBeFinal() {
        // @formatter:off
        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .areAssignableTo(IInput.class)
                .and()
                .areNotInterfaces()
                .should()
                .haveModifier(JavaModifier.FINAL);
        // @formatter:on

        rule.check(this.getClasses());
    }

    @Test
    public void classesImplentingPayloadShouldBeAnnotatedAsPayload() {
        // @formatter:off
        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .areAssignableTo(IPayload.class)
                .and()
                .areNotInterfaces()
                .should()
                .beAnnotatedWith(GraphQLObjectType.class);
        // @formatter:on

        rule.check(this.getClasses());
    }

    @Test
    public void classesImplentingPayloadShouldBeFinal() {
        // @formatter:off
        ArchRule rule = ArchRuleDefinition.classes()
                .that()
                .areAssignableTo(IPayload.class)
                .and()
                .areNotInterfaces()
                .should()
                .haveModifier(JavaModifier.FINAL)
                .andShould()
                .haveOnlyFinalFields();
        // @formatter:on

        rule.check(this.getClasses());
    }
}
