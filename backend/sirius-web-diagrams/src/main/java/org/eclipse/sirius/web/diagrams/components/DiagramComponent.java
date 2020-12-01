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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.eclipse.sirius.web.components.Element;
import org.eclipse.sirius.web.components.IComponent;
import org.eclipse.sirius.web.diagrams.Diagram;
import org.eclipse.sirius.web.diagrams.Node;
import org.eclipse.sirius.web.diagrams.Position;
import org.eclipse.sirius.web.diagrams.Size;
import org.eclipse.sirius.web.diagrams.description.DiagramDescription;
import org.eclipse.sirius.web.diagrams.elements.DiagramElementProps;
import org.eclipse.sirius.web.diagrams.renderer.DiagramRenderingCache;
import org.eclipse.sirius.web.representations.VariableManager;

/**
 * The component used to render the diagram.
 *
 * @author sbegaudeau
 */
public class DiagramComponent implements IComponent {

    private final DiagramComponentProps props;

    public DiagramComponent(DiagramComponentProps props) {
        this.props = props;
    }

    @Override
    public Element render() {
        VariableManager variableManager = this.props.getVariableManager();
        DiagramDescription diagramDescription = this.props.getDiagramDescription();

        String label = diagramDescription.getLabelProvider().apply(variableManager);

        UUID diagramId = diagramDescription.getIdProvider().apply(variableManager);
        String targetObjectId = diagramDescription.getTargetObjectIdProvider().apply(variableManager);

        DiagramRenderingCache cache = new DiagramRenderingCache();

        // @formatter:off
        List<Node> allPrevNodes = this.props.getPrevDiagram()
                .map(Diagram::getNodes)
                .orElse(List.of());
        var nodes = diagramDescription.getNodeDescriptions().stream()
                .map(nodeDescription -> {
                    List<Node> prevNodes = List.of();
                    if (!nodeDescription.isSynchronised()) {
                        prevNodes = allPrevNodes.stream()
                                .filter(node -> Objects.equals(node.getDescriptionId(), nodeDescription.getId()))
                                .collect(Collectors.toList());
                    }
                    var nodeComponentProps = new NodeComponentProps(variableManager, nodeDescription, false, cache, prevNodes);
                    return new Element(NodeComponent.class, nodeComponentProps);
                })
                .collect(Collectors.toList());
        // @formatter:on

        // @formatter:off
        var edges = diagramDescription.getEdgeDescriptions().stream()
                .map(edgeDescription -> {
                    var edgeComponentProps = new EdgeComponentProps(variableManager, edgeDescription, cache);
                    return new Element(EdgeComponent.class, edgeComponentProps);
                })
                .collect(Collectors.toList());
        // @formatter:on

        List<Element> children = new ArrayList<>();
        children.addAll(nodes);
        children.addAll(edges);

        // @formatter:off
        DiagramElementProps diagramElementProps = DiagramElementProps.newDiagramElementProps(diagramId)
                .targetObjectId(targetObjectId)
                .descriptionId(diagramDescription.getId())
                .label(label)
                .position(Position.UNDEFINED)
                .size(Size.UNDEFINED)
                .children(children)
                .build();
        // @formatter:on
        return new Element(DiagramElementProps.TYPE, diagramElementProps);
    }

}
