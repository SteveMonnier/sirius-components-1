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
package org.eclipse.sirius.web.diagrams.components;

import java.util.Objects;

import org.eclipse.sirius.web.components.IProps;
import org.eclipse.sirius.web.diagrams.description.EdgeDescription;
import org.eclipse.sirius.web.diagrams.renderer.DiagramRenderingCache;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * Properties of the edge component.
 *
 * @author sbegaudeau
 */
public class EdgeComponentProps implements IProps {

    private final VariableManager variableManager;

    private final EdgeDescription edgeDescription;

    private final DiagramRenderingCache cache;

    public EdgeComponentProps(VariableManager variableManager, EdgeDescription edgeDescription, DiagramRenderingCache cache) {
        this.variableManager = Objects.requireNonNull(variableManager);
        this.edgeDescription = Objects.requireNonNull(edgeDescription);
        this.cache = Objects.requireNonNull(cache);
    }

    public VariableManager getVariableManager() {
        return this.variableManager;
    }

    public EdgeDescription getEdgeDescription() {
        return this.edgeDescription;
    }

    public DiagramRenderingCache getCache() {
        return this.cache;
    }
}
