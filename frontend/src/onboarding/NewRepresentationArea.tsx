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
import { useMutation } from 'common/GraphQLHooks';
import { LinkButton } from 'core/linkbutton/LinkButton';
import { Select } from 'core/select/Select';
import gql from 'graphql-tag';
import { NewRepresentation } from 'icons';
import PropTypes from 'prop-types';
import React, { useEffect, useState } from 'react';
import { AreaContainer } from './AreaContainer';
import styles from './NewRepresentationArea.module.css';

const createRepresentationMutation = gql`
  mutation createRepresentation($input: CreateRepresentationInput!) {
    createRepresentation(input: $input) {
      __typename
      ... on CreateRepresentationSuccessPayload {
        representation {
          id
          label
          __typename
        }
      }
      ... on ErrorPayload {
        message
      }
    }
  }
`.loc.source.body;

const propTypes = {
  maxDisplay: PropTypes.number.isRequired,
  projectId: PropTypes.string.isRequired,
  representationDescriptions: PropTypes.array.isRequired,
  selections: PropTypes.array.isRequired,
  setSelections: PropTypes.func.isRequired,
  disabled: PropTypes.bool,
};

export const NewRepresentationArea = ({
  projectId,
  representationDescriptions,
  selections,
  maxDisplay,
  setSelections,
  disabled,
}) => {
  const initialState = {
    message: undefined,
  };
  const [state, setState] = useState(initialState);
  const { message } = state;

  let selection;
  if (selections && selections.length === 1) {
    selection = selections[0];
  }

  // Representation creation
  const [createRepresentation, result] = useMutation(createRepresentationMutation, {}, 'createRepresentation');
  useEffect(() => {
    if (!result.loading) {
      const { createRepresentation } = result.data.data;
      if (createRepresentation.representation) {
        const { id, label, __typename } = createRepresentation.representation;
        setSelections([{ id, label, kind: __typename }]);
      } else if (createRepresentation.__typename === 'ErrorPayload') {
        setState((prevState) => {
          const newState = { ...prevState };
          newState.message = createRepresentation.message;
          return newState;
        });
      }
    }
  }, [result, setSelections]);
  const onCreateRepresentation = (representationDescriptionId) => {
    const selected = representationDescriptions.find((candidate) => candidate.id === representationDescriptionId);
    const objectId = selection.id;
    const input = {
      projectId,
      objectId,
      representationDescriptionId,
      representationName: selected.label,
    };
    createRepresentation({ input });
  };

  // Representation Descriptions list
  let newRepresentationButtons =
    representationDescriptions.length > 0
      ? representationDescriptions.slice(0, maxDisplay).map((representationDescription) => {
          return (
            <LinkButton
              key={representationDescription.id}
              label={representationDescription.label}
              data-testid={representationDescription.id}
              onClick={() => {
                onCreateRepresentation(representationDescription.id);
              }}>
              <NewRepresentation title="" className={styles.icon} />
            </LinkButton>
          );
        })
      : [];

  // More select
  const moreName = 'moreRepresentationDescriptions';
  const moreLabel = 'More representations types...';
  let moreSelect =
    representationDescriptions.length > maxDisplay ? (
      <Select
        onChange={(event) => {
          onCreateRepresentation(event.target.value);
        }}
        name={moreName}
        options={[{ id: moreLabel, label: moreLabel }, representationDescriptions.slice(maxDisplay)].flat()}
        data-testid={moreName}
      />
    ) : null;

  let title = 'Create a new Representation';
  if (disabled) {
    return <AreaContainer title={title} subtitle="You need edit access to create representations" />;
  } else {
    let subtitle =
      representationDescriptions.length > 0
        ? 'Select the representation to create on ' + selection.label
        : 'There are no representations available for the current selection';
    return (
      <AreaContainer title={title} subtitle={subtitle} banner={message}>
        {newRepresentationButtons}
        {moreSelect}
      </AreaContainer>
    );
  }
};

NewRepresentationArea.propTypes = propTypes;
