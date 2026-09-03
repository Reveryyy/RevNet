package dev.reveryy.revnet.repository;

import dev.reveryy.revdata.Repository;
import dev.reveryy.revnet.models.Device;
import inet.ipaddr.IPAddress;
import inet.ipaddr.ipv4.IPv4Address;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends Repository<Device, Long> {
}
