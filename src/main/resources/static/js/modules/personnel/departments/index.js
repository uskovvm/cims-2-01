'use strict';

define(['jquery', 'react', 'react-dom', 'ui/ui_pager', './ui_control_panel', './ui_departments', 'utils/utils', 'dao/organizations', 'dao/departments', 'dao/zones', './ui_add_department', './ui_edit_department', 'core/logger'], function ($, React, ReactDOM, Pager, ControlPanel, Departments, Utils, OrganizationsDao, DepartmentsDao, ZonesDao, AddDepartment, EditDepartment, Log) {
  let ORGANIZATIONS_LIMIT = 1000;

  return React.createClass({
    getInitialState: function () {
      return {
        view: 'browse',
        organizations: [],
        zones: [],
        departments: [],
        selectedDepartment: null,
        pagerLimit: 10
      };
    },
    componentDidMount: function () {
      let self = this;

      let params = {
        limit: ORGANIZATIONS_LIMIT
      };

      OrganizationsDao.getAll(params, function (data) {
        self.setState({ organizations: data.rows });
      });

      ZonesDao.getAll({}, function (data) {
        self.setState({ zones: data.rows });
      });
    },
    loadDepartments: function (_params, clb) {
      let self = this;

      let params = {};

      if (_params) {
        params.offset = _params.offset;
        params.limit = _params.limit;
      }

      DepartmentsDao.getAll(params, function (data) {
        if (clb) {
          clb(data);
        }

        self.setState({
          departments: data.rows,
          selectedDepartment: !data.rows.length ? null : data.rows[0]
        });
      });
    },
    onPagerLimitChange: function (ev) {
      let el = $(ev.currentTarget);
      this.setState({
        pagerLimit: +el.attr('data-size')
      });
    },
    onSelect: function (id) {
      let res = Utils.findById(this.state.departments, +id);
      this.setState({ selectedDepartment: res });
    },
    onControlButtonClick: function (command, clb) {
      let self = this;

      if (command === 'delete') {
        DepartmentsDao.del({ id: self.state.selectedDepartment.id }, function (res) {
          Log.info('Отдел успешно удалён');
          clb ? clb() : 0;
        }, function () {
          Log.error('Ошибка удаления отдела');
          clb ? clb() : 0;
        });
      }

      self.setState({
        view: command
      });
    },
    renderAdd: function () {
      let self = this;

      let data = {
        onOk: function () {
          self.setState({ view: 'browse' });
        },
        onCancel: function () {
          self.setState({ view: 'browse' });
        },
        organizations: self.state.organizations,
        zones: self.state.zones
      };

      return React.createElement(
        'div',
        { className: 'content-wrapper' },
        React.createElement(
          'div',
          { className: 'content' },
          React.createElement(AddDepartment, { data: data })
        )
      );
    },
    renderEdit: function () {
      let self = this;

      let data = {
        onOk: function () {
          self.setState({ view: 'browse' });
        },
        onCancel: function () {
          self.setState({ view: 'browse' });
        },
        organizations: self.state.organizations,
        zones: self.state.zones,
        department: self.state.selectedDepartment
      };

      return React.createElement(
        'div',
        { className: 'content-wrapper' },
        React.createElement(
          'div',
          { className: 'content' },
          React.createElement(EditDepartment, { data: data })
        )
      );
    },
    renderBrowse: function () {
      let class10 = 'pager-item' + (this.state.pagerLimit === 10 ? ' selected' : ''),
          class25 = 'pager-item' + (this.state.pagerLimit === 25 ? ' selected' : ''),
          class50 = 'pager-item' + (this.state.pagerLimit === 50 ? ' selected' : '');

      return React.createElement(
        'div',
        { className: 'content-with-control-panel' },
        React.createElement(ControlPanel, { data: this.state, onClick: this.onControlButtonClick }),
        React.createElement(
          'div',
          { className: 'content-wrapper' },
          React.createElement(
            'div',
            { className: 'content' },
            React.createElement(
              'div',
              { className: 'departments-panel panel' },
              React.createElement(Departments, { data: this.state, onSelect: this.onSelect }),
              React.createElement(
                'div',
                { className: 'halign' },
                React.createElement(Pager, { onLoad: this.loadDepartments, limit: this.state.pagerLimit })
              ),
              React.createElement(
                'div',
                { className: 'ralign' },
                '\u042D\u043B\u0435\u043C\u0435\u043D\u0442\u043E\u0432 \u043D\u0430 \u0441\u0442\u0440\u0430\u043D\u0438\u0446\u0435:',
                React.createElement(
                  'span',
                  { className: class10, 'data-size': '10', onClick: this.onPagerLimitChange },
                  '10'
                ),
                React.createElement(
                  'span',
                  { className: class25, 'data-size': '25', onClick: this.onPagerLimitChange },
                  '25'
                ),
                React.createElement(
                  'span',
                  { className: class50, 'data-size': '50', onClick: this.onPagerLimitChange },
                  '50'
                )
              )
            )
          )
        )
      );
    },
    render: function () {
      switch (this.state.view) {
        case 'add':
          return this.renderAdd();
        case 'edit':
          return this.renderEdit();
        default:
          return this.renderBrowse();
      }
    }
  });
});