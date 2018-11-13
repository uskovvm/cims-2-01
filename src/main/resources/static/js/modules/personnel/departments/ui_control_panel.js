'use strict';

define(['react', 'react-dom', 'core/events', 'ui/ui_elements', 'ui/ui_modal_confirm', './ui_modal_card_designer', 'core/auth'], function (React, ReactDOM, Events, Elements, ModalConfirm, ModalCardDesigner, Auth) {
  return React.createClass({
    getInitialState: function () {
      return {
        delDialogOpen: false,
        cardDesignerDialogOpen: false
      };
    },
    onCloseModal: function () {
      this.setState(this.getInitialState());
    },
    onReports: function () {
      Events.dispatchEvent(Events.EVENT_PAGE_NAVIGATED, { moduleId: 3, pageId: 32 });
    },
    onOpenDelDialog: function () {
      this.setState({ delDialogOpen: true });
    },
    onOpenCardDesignerDialog: function () {
      this.setState({ cardDesignerDialogOpen: true });
    },
    onDepartmentDel: function () {
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

      let btnReportsData = {
        className: 'button-reports',
        dataTooltip: 'Отчёты',
        onClick: this.onReports
      };

      let btnCardData = {
        className: 'button-create-badges',
        dataTooltip: 'Создать карту',
        onClick: this.onOpenCardDesignerDialog
      };

      let buttons = null;

      if (Auth.hasPermission(Auth.Permission.PERM_DEPARTMENTS_MANAGEMENT)) {

        buttons = [React.createElement(Elements.ControlButton, { data: btnAddData })];

        if (this.props.data.selectedDepartment) {
          buttons.push(React.createElement(Elements.ControlButton, { data: btnEditData }), React.createElement(Elements.ControlButton, { data: btnDelData }));
        }
      }

      return React.createElement(
        'div',
        { className: 'control-panel' },
        buttons,
        React.createElement(Elements.ControlButton, { data: btnReportsData }),
        React.createElement(Elements.ControlButton, { data: btnCardData }),
        React.createElement(ModalConfirm, {
          isOpen: this.state.delDialogOpen,
          onRequestClose: this.onCloseModal,
          onRequestOk: this.onDepartmentDel }),
        React.createElement(ModalCardDesigner, {
          isOpen: this.state.cardDesignerDialogOpen,
          onRequestClose: this.onCloseModal,
          selectedDepartment: this.props.data.selectedDepartment })
      );
    }
  });
});