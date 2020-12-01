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
package org.eclipse.sirius.web.spring.collaborative.diagrams.handlers;

import java.util.Objects;
import java.util.Optional;

import org.eclipse.sirius.web.collaborative.api.services.EventHandlerResponse;
import org.eclipse.sirius.web.collaborative.api.services.Monitoring;
import org.eclipse.sirius.web.collaborative.diagrams.api.IDiagramDescriptionService;
import org.eclipse.sirius.web.collaborative.diagrams.api.IDiagramEventHandler;
import org.eclipse.sirius.web.collaborative.diagrams.api.IDiagramInput;
import org.eclipse.sirius.web.collaborative.diagrams.api.IDiagramRefreshManager;
import org.eclipse.sirius.web.collaborative.diagrams.api.IDiagramService;
import org.eclipse.sirius.web.collaborative.diagrams.api.dto.DeleteFromDiagramInput;
import org.eclipse.sirius.web.collaborative.diagrams.api.dto.DeleteFromDiagramSuccessPayload;
import org.eclipse.sirius.web.diagrams.Diagram;
import org.eclipse.sirius.web.diagrams.Node;
import org.eclipse.sirius.web.diagrams.description.DiagramDescription;
import org.eclipse.sirius.web.diagrams.description.NodeDescription;
import org.eclipse.sirius.web.representations.VariableManager;
import org.eclipse.sirius.web.services.api.dto.ErrorPayload;
import org.eclipse.sirius.web.services.api.objects.IEditingContext;
import org.eclipse.sirius.web.services.api.objects.IObjectService;
import org.eclipse.sirius.web.services.api.representations.IRepresentationDescriptionService;
import org.eclipse.sirius.web.spring.collaborative.diagrams.messages.ICollaborativeDiagramMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

/**
 * Handle "Delete from Diagram" events.
 *
 * @author pcdavid
 */
@Service
public class DeleteFromDiagramEventHandler implements IDiagramEventHandler {

    private final IObjectService objectService;

    private final IDiagramService diagramService;

    private final IDiagramDescriptionService diagramDescriptionService;

    private final IRepresentationDescriptionService representationDescriptionService;

    private final ICollaborativeDiagramMessageService messageService;

    private final Logger logger = LoggerFactory.getLogger(DeleteFromDiagramEventHandler.class);

    private final Counter counter;

    public DeleteFromDiagramEventHandler(IObjectService objectService, IDiagramService diagramService, IDiagramDescriptionService diagramDescriptionService,
            IRepresentationDescriptionService representationDescriptionService, ICollaborativeDiagramMessageService messageService, MeterRegistry meterRegistry) {
        this.objectService = Objects.requireNonNull(objectService);
        this.diagramService = Objects.requireNonNull(diagramService);
        this.diagramDescriptionService = Objects.requireNonNull(diagramDescriptionService);
        this.representationDescriptionService = Objects.requireNonNull(representationDescriptionService);
        this.messageService = Objects.requireNonNull(messageService);

        // @formatter:off
        this.counter = Counter.builder(Monitoring.EVENT_HANDLER)
                .tag(Monitoring.NAME, this.getClass().getSimpleName())
                .register(meterRegistry);
        // @formatter:on
    }

    @Override
    public boolean canHandle(IDiagramInput diagramInput) {
        return diagramInput instanceof DeleteFromDiagramInput;
    }

    @Override
    public EventHandlerResponse handle(IEditingContext editingContext, IDiagramRefreshManager refreshManager, IDiagramInput diagramInput) {
        this.counter.increment();

        if (diagramInput instanceof DeleteFromDiagramInput) {
            Diagram diagram = refreshManager.getDiagram();
            DeleteFromDiagramInput input = (DeleteFromDiagramInput) diagramInput;
            var optionalNode = this.diagramService.findNodeById(diagram, input.getDiagramElementId());
            if (optionalNode.isPresent()) {
                Node node = optionalNode.get();

                this.invokeDeleteTool(node, editingContext, refreshManager);
                return new EventHandlerResponse(true, representation -> true, new DeleteFromDiagramSuccessPayload(diagram));
            }
        }
        String message = this.messageService.invalidInput(diagramInput.getClass().getSimpleName(), DeleteFromDiagramInput.class.getSimpleName());
        return new EventHandlerResponse(false, representation -> false, new ErrorPayload(message));
    }

    private void invokeDeleteTool(Node node, IEditingContext editingContext, IDiagramRefreshManager refreshManager) {
        Diagram diagram = refreshManager.getDiagram();
        var optionalNodeDescription = this.findNodeDescription(node, diagram);
        if (optionalNodeDescription.isPresent()) {
            NodeDescription nodeDescription = optionalNodeDescription.get();

            var optionalSelf = this.objectService.getObject(editingContext, node.getTargetObjectId());
            if (optionalSelf.isPresent()) {
                Object self = optionalSelf.get();

                VariableManager variableManager = new VariableManager();
                variableManager.put(VariableManager.SELF, self);
                variableManager.put(IDiagramRefreshManager.DIAGRAM_REFRESH_MANAGER, refreshManager);
                nodeDescription.getDeleteHandler().apply(variableManager);

                this.logger.debug("Deleted diagram element {}", node.getId()); //$NON-NLS-1$
            }
        }
    }

    private Optional<NodeDescription> findNodeDescription(Node node, Diagram diagram) {
        // @formatter:off
        return this.representationDescriptionService
                .findRepresentationDescriptionById(diagram.getDescriptionId())
                .filter(DiagramDescription.class::isInstance)
                .map(DiagramDescription.class::cast)
                .flatMap(diagramDescription -> this.diagramDescriptionService.findNodeDescriptionById(diagramDescription, node.getDescriptionId()));
        // @formatter:on
    }

}
