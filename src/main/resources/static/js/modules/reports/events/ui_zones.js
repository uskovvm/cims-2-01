'use strict';

define(['jquery', 'react', 'react-dom', 'ui/ui_elements', 'dao/organizations', 'dao/departments', 'dao/accounts', 'dao/zones', 'utils/time', 'core/events', 'dao/reports', 'ui/ui_pager', 'utils/utils', 'objects'], function ($, React, ReactDOM, Elements, OrganizationsDao, DepartmentsDao, AccountsDao, ZonesDao, Time, Events, ReportsDao, Pager, Utils, Objects) {
  return React.createClass({
    getInitialState: function () {
      return {
        '$callback': null,
        startDate: Time.rollDays(new Date(), -30),
        endDate: new Date(),
        showPager: false,
        pagerLimit: 10,
        organizations: [],
        selectedOrganizationId: 0,
        departments: [],
        selectedDepartmentId: 0,
        accounts: [],
        selectedAccountId: 0,
        zones: [],
        selectedZoneId: 0,
        content: []
      };
    },
    componentDidMount: function () {
      this.loadOrganizations();
      this.loadZones();

      let callback = Events.addCallback(Events.EVENT_REPORT_MAKE, this.onMakeReport);
      this.setState({ '$callback': callback });
    },
    componentWillUnmount: function () {
      Events.removeCallback(Events.EVENT_REPORT_MAKE, this.state['$callback']);
    },
    onMakeReport: function (data) {
      let params = {
        id: 2,
        startDate: this.state.startDate.getTime(),
        endDate: this.state.endDate.getTime(),
        format: data.format
      };

      if (this.state.selectedOrganizationId !== 0) {
        params.organizationId = this.state.selectedOrganizationId;
      }

      if (this.state.selectedDepartmentId !== 0) {
        params.departmentId = this.state.selectedDepartmentId;
      }

      if (this.state.selectedAccountId !== 0) {
        params.accountId = this.state.selectedAccountId;
      }

      if (this.state.selectedZoneId !== 0) {
        params.zoneId = this.state.selectedZoneId;
      }

      window.open('/reports/api/report?' + Utils.jsonToUrl(params));
    },
    loadOrganizations: function () {
      let self = this;

      OrganizationsDao.getAll({}, function (res) {
        let organizations = res.rows;
        organizations.push({
          id: 0,
          name: 'Все'
        });

        self.setState({
          organizations: organizations,
          selectedOrganizationId: 0,
          departments: [],
          selectedDepartmentId: 0,
          accounts: [],
          selectedAccountId: 0
        }, self.loadDepartments);
      });
    },
    loadDepartments: function () {
      let self = this;

      if (self.state.selectedOrganizationId === 0) {
        self.setState({
          departments: [],
          selectedDepartmentId: 0,
          accounts: [],
          selectedAccountId: 0
        });
        return;
      }

      DepartmentsDao.getAll({ organizationId: self.state.selectedOrganizationId }, function (res) {
        let departments = res.rows;
        departments.push({
          id: 0,
          name: 'Все'
        });

        self.setState({
          departments: departments,
          selectedDepartmentId: 0,
          accounts: [],
          selectedAccountId: 0
        }, self.loadAccounts);
      });
    },
    loadAccounts: function () {
      let self = this;

      if (self.state.selectedDepartmentId === 0) {
        self.setState({
          accounts: [],
          selectedAccountId: 0
        });
        return;
      }

      AccountsDao.getAll({ departmentId: self.state.selectedDepartmentId }, function (res) {
        let accounts = res.rows.map(function (el, idx) {
          return {
            id: el.id,
            name: el.lastName + ' ' + el.firstName + ' ' + el.middleName
          };
        });
        accounts.push({
          id: 0,
          name: 'Все'
        });

        self.setState({
          accounts: accounts,
          selectedAccountId: 0
        });
      });
    },
    loadZones: function () {
      let self = this;

      ZonesDao.getAll({}, function (res) {
        let zones = res.rows;
        zones.push({
          id: 0,
          name: 'Все'
        });

        self.setState({ zones: zones });
      });
    },
    loadReportData: function (inheritParams, clb) {
      let self = this;

      let params = {
        id: 2,
        startDate: self.state.startDate.getTime(),
        endDate: self.state.endDate.getTime()
      };

      if (self.state.selectedOrganizationId !== 0) {
        params.organizationId = self.state.selectedOrganizationId;
      }

      if (self.state.selectedDepartmentId !== 0) {
        params.departmentId = self.state.selectedDepartmentId;
      }

      if (self.state.selectedAccountId !== 0) {
        params.accountId = self.state.selectedAccountId;
      }

      if (self.state.selectedZoneId !== 0) {
        params.zoneId = self.state.selectedZoneId;
      }

      ReportsDao.get(Objects.merge(params, inheritParams), function (res) {
        self.setState({
          content: res.rows,
          showPager: !(inheritParams.offset === 0 && inheritParams.limit >= res.pager.totalRows)
        });
        clb ? clb(res) : 0;
      });
    },
    makeReport: function () {
      Events.dispatchEvent(Events.EVENT_PAGER_CLICK, { id: 1 });
    },
    onChangeStartDate: function (date) {
      if (date) {
        this.setState({ startDate: new Date(date) });
      }
    },
    onChangeEndDate: function (date) {
      if (date) {
        this.setState({ endDate: new Date(date) });
      }
    },
    onChangeOrganization: function (id) {
      this.setState({ selectedOrganizationId: +id }, this.loadDepartments);
    },
    onChangeDepartment: function (id) {
      this.setState({ selectedDepartmentId: +id }, this.loadAccounts);
    },
    onChangeAccount: function (id) {
      this.setState({ selectedAccountId: +id });
    },
    onChangeZone: function (id) {
      this.setState({ selectedZoneId: +id });
    },
    onChangePagerLimit: function (ev) {
      let el = $(ev.currentTarget);
      this.setState({ pagerLimit: +el.attr('data-size') });
    },
    render: function () {
      let self = this;

      let style = {
        position: 'absolute',
        bottom: '-10px',
        right: '0'
      };

      let okButtonConfig = {
        className: 'button-report-html',
        dataTooltip: 'Сформировать отчёт',
        onClick: self.makeReport
      };

      let pagerConfig = {
        firstClick: false,
        id: 1
      };

      let content = null;
      if (self.state.content && self.state.content.length > 0) {
        let data = self.state.content.map(function (el, idx) {
          let totalTime = Math.floor((el.exitTime - el.enterTime) / 60000);
          return React.createElement(
            'tr',
            null,
            React.createElement(
              'td',
              null,
              el.zoneName
            ),
            React.createElement(
              'td',
              null,
              el.employeeFullName
            ),
            React.createElement(
              'td',
              null,
              Time.format('DDMMYYYYHHMMSS', el.enterTime)
            ),
            React.createElement(
              'td',
              null,
              Time.format('DDMMYYYYHHMMSS', el.exitTime)
            ),
            React.createElement(
              'td',
              null,
              Time.formatTime(totalTime)
            )
          );
        });

        content = React.createElement(
          'table',
          { className: 'reports-table' },
          React.createElement(
            'thead',
            null,
            React.createElement(
              'tr',
              null,
              React.createElement(
                'th',
                null,
                '\u0417\u043E\u043D\u0430'
              ),
              React.createElement(
                'th',
                null,
                '\u041F\u0435\u0440\u0441\u043E\u043D\u0430\u043B'
              ),
              React.createElement(
                'th',
                null,
                '\u0412\u0440\u0435\u043C\u044F \u0432\u0445\u043E\u0434\u0430'
              ),
              React.createElement(
                'th',
                null,
                '\u0412\u0440\u0435\u043C\u044F \u0432\u044B\u0445\u043E\u0434\u0430'
              ),
              React.createElement(
                'th',
                null,
                '\u041F\u0440\u043E\u0432\u0435\u0434\u0435\u043D\u043E \u0432 \u0437\u043E\u043D\u0435'
              )
            )
          ),
          React.createElement(
            'tbody',
            null,
            data
          )
        );
      } else {
        content = React.createElement(
          'div',
          { className: 'with-margin-top20' },
          React.createElement('hr', null),
          React.createElement(
            'div',
            { className: 'halign' },
            '\u041D\u0435\u0442 \u0434\u0430\u043D\u043D\u044B\u0445 \u0434\u043B\u044F \u043E\u0442\u043E\u0431\u0440\u0430\u0436\u0435\u043D\u0438\u044F'
          )
        );
      }

      return React.createElement(
        'div',
        null,
        React.createElement(
          'div',
          { className: 'reports-settings' },
          React.createElement(Elements.Datepicker, { date: self.state.startDate, caption: 'Начальная дата', onChange: self.onChangeStartDate }),
          React.createElement(Elements.Datepicker, { date: self.state.endDate, caption: 'Конечная дата', onChange: self.onChangeEndDate }),
          React.createElement(
            'div',
            { className: 'ui-element-item' },
            React.createElement(
              'div',
              { className: 'header2' },
              '\u041E\u0440\u0433\u0430\u043D\u0438\u0437\u0430\u0446\u0438\u044F'
            ),
            React.createElement(Elements.Select, { className: 'with-element-width', values: self.state.organizations, selectedValue: self.state.selectedOrganizationId, onChange: self.onChangeOrganization })
          ),
          React.createElement(
            'div',
            { className: 'ui-element-item' },
            React.createElement(
              'div',
              { className: 'header2' },
              '\u041E\u0442\u0434\u0435\u043B'
            ),
            React.createElement(Elements.Select, { className: 'with-element-width', values: self.state.departments, selectedValue: self.state.selectedDepartmentId, onChange: self.onChangeDepartment })
          ),
          React.createElement(
            'div',
            { className: 'ui-element-item' },
            React.createElement(
              'div',
              { className: 'header2' },
              '\u041F\u0435\u0440\u0441\u043E\u043D\u0430\u043B'
            ),
            React.createElement(Elements.Select, { className: 'with-element-width', values: self.state.accounts, selectedValue: self.state.selectedAccountId, onChange: self.onChangeAccount })
          ),
          React.createElement(
            'div',
            { className: 'ui-element-item' },
            React.createElement(
              'div',
              { className: 'header2' },
              '\u0417\u043E\u043D\u0430 \u0434\u043E\u0441\u0442\u0443\u043F\u0430'
            ),
            React.createElement(Elements.Select, { className: 'with-element-width', values: self.state.zones, selectedValue: self.state.selectedZoneId, onChange: self.onChangeZone })
          ),
          React.createElement(
            'div',
            { className: 'ui-element-item', style: style },
            React.createElement(Elements.ControlButton, { data: okButtonConfig })
          )
        ),
        content,
        React.createElement(
          'div',
          { hidden: !self.state.showPager },
          React.createElement(
            'div',
            { className: 'halign' },
            React.createElement(Pager, { config: pagerConfig, onLoad: self.loadReportData, limit: self.state.pagerLimit })
          ),
          React.createElement(
            'div',
            { className: 'ralign' },
            '\u042D\u043B\u0435\u043C\u0435\u043D\u0442\u043E\u0432 \u043D\u0430 \u0441\u0442\u0440\u0430\u043D\u0438\u0446\u0435:',
            React.createElement(
              'span',
              { className: 'pager-item', 'data-size': '10', onClick: self.onChangePagerLimit },
              '10'
            ),
            React.createElement(
              'span',
              { className: 'pager-item', 'data-size': '50', onClick: self.onChangePagerLimit },
              '50'
            ),
            React.createElement(
              'span',
              { className: 'pager-item', 'data-size': '100', onClick: self.onChangePagerLimit },
              '100'
            )
          )
        )
      );
    }
  });
});