/*
 * Copyright (C) 2017-2019 Dremio Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import PropTypes from 'prop-types';
import clsx from 'clsx';

import MuiDialog from '@material-ui/core/Dialog';

import DialogContext from './DialogContext';
import DialogTitle from './DialogTitle';

import './dialog.scss';

const Dialog = (props) => {
  const {
    classes,
    isCentered,
    onClose,
    onEnter,
    onEntered,
    onExit,
    onExited,
    onExiting,
    open,
    size,
    title,
    children
  } = props;

  const dialogClasses = {
    ...classes,
    root: clsx(['dremioDialog', { [classes.root]: classes.root }]),
    container: clsx('dremioDialog__container', { '--centered': isCentered })
  };

  return <MuiDialog
    classes={dialogClasses}
    maxWidth={size}
    onClose={onClose}
    onEnter={onEnter}
    onEntered={onEntered}
    onExit={onExit}
    onExited={onExited}
    onExiting={onExiting}
    open={open}
  >
    <DialogContext.Provider value={{ onClose }}>
      {title && <DialogTitle onClose={onClose}>
        {title}
      </DialogTitle>}

      { children }
    </DialogContext.Provider>
  </MuiDialog>;
};

Dialog.propTypes = {
  // Dialog Props
  size: PropTypes.oneOf(['xs', 'sm', 'md', 'lg', 'xl']),
  isCentered: PropTypes.bool,
  onClose: PropTypes.func,
  onEnter: PropTypes.func,
  onEntered: PropTypes.func,
  onExit: PropTypes.func,
  onExited: PropTypes.func,
  onExiting: PropTypes.func,
  open: PropTypes.bool.isRequired,
  children: PropTypes.oneOfType([
    PropTypes.node,
    PropTypes.func
  ]),
  title: PropTypes.string,
  classes: PropTypes.object
};

Dialog.defaultProps = {
  isCentered: false,
  size: 'sm',
  classes: {}
};

export default Dialog;
