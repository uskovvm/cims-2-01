'use strict';

define(['react', 'react-dom', 'ui/ui_elements', 'ui/ui_modal_confirm', 'core/auth'], function (React, ReactDOM, Elements, ModalConfirm, Auth) {
  return React.createClass({
    getInitialState: function () {
      return {
        delDialogOpen: false
      };
    },
    onCloseModal: function () {
      this.setState({
        delDialogOpen: false
      });
    },
    onOpenDelDialog: function () {
      this.setState({ delDialogOpen: true });
    },
    onDel: function () {
      this.props.onClick('delete', this.onCloseModal);
    },
    render: function () {
      let self = this;

      let btnAddData = {
        className: 'button-add',
        dataTooltip: 'Добавить',
        onClick: function () {
          self.props.onClick('add');
        }
      };

      let btnEditData = {
        className: 'button-edit',
        dataTooltip: 'Изменить',
        onClick: function () {
          self.props.onClick('edit');
        }
      };

      let btnDelData = {
        className: 'button-delete',
        dataTooltip: 'Удалить',
        onClick: this.onOpenDelDialog
      };

      let buttons = [React.createElement(Elements.ControlButton, { data: btnAddData })];

      if (this.props.data.selectedConnection) {
        buttons.push(React.createElement(Elements.ControlButton, { data: btnEditData }), React.createElement(Elements.ControlButton, { data: btnDelData }));
      }

      return React.createElement(
        'div',
        { className: 'control-panel' },
        buttons,
        React.createElement(ModalConfirm, {
          isOpen: this.state.delDialogOpen,
          onRequestClose: this.onCloseModal,
          onRequestOk: this.onDel })
      );
    }
  });
});