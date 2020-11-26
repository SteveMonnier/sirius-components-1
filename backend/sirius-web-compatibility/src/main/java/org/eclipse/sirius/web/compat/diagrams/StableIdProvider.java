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
package org.eclipse.sirius.web.compat.diagrams;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import org.eclipse.sirius.web.representations.VariableManager;
import org.eclipse.sirius.web.services.api.objects.IObjectService;

/**
 * Provides a stable id to nodes and containers.
 *
 * @author pcdavid
 */
public class StableIdProvider implements Function<VariableManager, String> {
    private final IObjectService objectService;

    public StableIdProvider(IObjectService objectService) {
        this.objectService = Objects.requireNonNull(objectService);
    }

    @Override
    public String apply(VariableManager variableManager) {
        Optional<String> parentViewId = variableManager.get("parentViewId", String.class); //$NON-NLS-1$
        Optional<String> containmentKind = variableManager.get("containmentKind", String.class); //$NON-NLS-1$
        Optional<String> descriptionId = variableManager.get("descriptionId", String.class); //$NON-NLS-1$
        Optional<String> semanticId = variableManager.get(VariableManager.SELF, Object.class).map(this.objectService::getId);
        if (parentViewId.isPresent() && containmentKind.isPresent() && descriptionId.isPresent() && semanticId.isPresent()) {
            String discriminant = parentViewId.get() + containmentKind.get() + descriptionId.get() + semanticId.get();
            return UUID.nameUUIDFromBytes(discriminant.getBytes()).toString();
        } else {
            return UUID.randomUUID().toString();
        }
    }
}
