package com.carddex.sims2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carddex.sims2.model.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {

}
