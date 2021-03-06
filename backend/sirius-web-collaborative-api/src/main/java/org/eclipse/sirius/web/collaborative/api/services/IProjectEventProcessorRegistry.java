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
package org.eclipse.sirius.web.collaborative.api.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.sirius.web.services.api.Context;
import org.eclipse.sirius.web.services.api.dto.IInput;
import org.eclipse.sirius.web.services.api.dto.IPayload;

/**
 * Registry of all the project event handlers.
 *
 * @author sbegaudeau
 */
public interface IProjectEventProcessorRegistry {
    List<IProjectEventProcessor> getProjectEventProcessors();

    Optional<IPayload> dispatchEvent(UUID projectId, IInput input, Context context);

    Optional<IProjectEventProcessor> getOrCreateProjectEventProcessor(UUID projectId);

    void dispose(UUID projectId);
}
