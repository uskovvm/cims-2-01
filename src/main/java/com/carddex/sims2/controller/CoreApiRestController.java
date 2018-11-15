package com.carddex.sims2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.carddex.sims2.model.Module;
import com.carddex.sims2.rest.dto.BaseConnectionDto;
import com.carddex.sims2.rest.dto.ConnectionTypeDto;
import com.carddex.sims2.rest.dto.DeviceDto;
import com.carddex.sims2.rest.dto.DeviceTypeDto;
import com.carddex.sims2.rest.dto.PagedResponse;
import com.carddex.sims2.rest.dto.PermissionObjects;
import com.carddex.sims2.rest.dto.ProtocolDto;
import com.carddex.sims2.rest.dto.StatusResponse;
import com.carddex.sims2.rest.model.ModuleStatus;
import com.carddex.sims2.rest.model.PortInfo;
import com.carddex.sims2.security.JwtAuthenticationRequest;
import com.carddex.sims2.service.ConnectionService;
import com.carddex.sims2.service.DeviceService;
import com.carddex.sims2.service.ModuleService;

@RestController
public class CoreApiRestController {

	@Value("${jwt.header}")
	private String tokenHeader;

	@Autowired
	private ModuleService moduleService;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	private ConnectionService connectionService;

	@RequestMapping(value = "/core/api/modules", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<Module>> getModules() {

		return ResponseEntity.ok(moduleService.loadAllModules());
	}

	@RequestMapping(value = "/core/api/modules/enabled", method = RequestMethod.POST)
	public ResponseEntity<Object> setModuleStatus(@RequestBody List<ModuleStatus> list) {

		try {
			if (moduleService.setModuleStatus(list)) {
				return ResponseEntity.ok(new StatusResponse("success"));
			} else {
				throw (new Exception());
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new StatusResponse("error"));
		}
	}

	@RequestMapping(value = { "/core/api/permission/objects/&&{roleid}&{privilegeid}",
			"/core/api/permission/objects/{userid}&&{privilegeid}",
			"/core/api/permission/objects/{userid}&{roleid}&{privilegeid}" }, method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity getPermissionObjects(@PathVariable(required = false) Optional<Long> userid,
			@PathVariable(required = false) Optional<Long> roleid, @PathVariable(required = true) Long privilegeid) {

		if (userid.isPresent() && userid.get().longValue() > 0)
			return ResponseEntity.status(HttpStatus.OK)
					.body(deviceService.getPermissionObjectsByUserAndPrivilege(userid.get(), privilegeid));
		else if (roleid.isPresent() && roleid.get().longValue() > 0)
			return ResponseEntity.status(HttpStatus.OK)
					.body(deviceService.getPermissionObjectsByRoleAndPrivilege(roleid.get(), privilegeid));
		else
			return ResponseEntity.status(HttpStatus.OK).body(new ArrayList<Long>());
	}

	@RequestMapping(value = "/core/api/permission/objects", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity setPermissionObjects(@RequestBody PermissionObjects permissionObjects) {

		try {
			if (deviceService.setPermissionObjects(permissionObjects)) {
				return ResponseEntity.ok(new StatusResponse("success"));
			} else {
				throw (new Exception());
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(new StatusResponse("error"));
		}
	}

	@RequestMapping(value = "/core/api/device/types", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<DeviceTypeDto>> getDeviceTypes() {
		return ResponseEntity.status(HttpStatus.OK).body(deviceService.findAllDeviceType());
	}

	@RequestMapping(value = "/core/api/identification/types", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<DeviceTypeDto>> getIdentificationTypes() {

		List<DeviceTypeDto> list = new ArrayList<DeviceTypeDto>();
		list.add(new DeviceTypeDto(new Long(1), "id type 1", "identif type one"));
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@RequestMapping(value = "/core/api/protocols", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<ProtocolDto>> getProtocols() {

		List<ProtocolDto> list = new ArrayList<ProtocolDto>();
		list.add(new ProtocolDto(new Long(1), "protocol 1", "descr prot type one"));
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	@RequestMapping(value = "/core/api/connections", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<BaseConnectionDto>> getConnections() {

		List<BaseConnectionDto> list = connectionService.findAllConnection();
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	//
	@RequestMapping(value = "/core/api/connection/types", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<ConnectionTypeDto>> getConnectionTypes() {

		List<ConnectionTypeDto> list = connectionService.findAllConnectionType();
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	//
	@RequestMapping(value = "/core/api/ports", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<List<PortInfo>> getPorts() {

		List<PortInfo> list = connectionService.findAllPorts();
		return ResponseEntity.status(HttpStatus.OK).body(list);
	}

	//
	@RequestMapping(value = "/acs/api/devices", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<PagedResponse> getDevices() {

		List<DeviceDto> list = deviceService.findAllDevice();
		PagedResponse response = new PagedResponse(list);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
