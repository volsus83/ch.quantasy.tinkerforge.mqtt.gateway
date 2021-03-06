/*
 * /*
 *  *   "TiMqWay"
 *  *
 *  *    TiMqWay(tm): A gateway to provide an MQTT-View for the Tinkerforge(tm) world (Tinkerforge-MQTT-Gateway).
 *  *
 *  *    Copyright (c) 2016 Bern University of Applied Sciences (BFH),
 *  *    Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *  *    Quellgasse 21, CH-2501 Biel, Switzerland
 *  *
 *  *    Licensed under Dual License consisting of:
 *  *    1. GNU Affero General Public License (AGPL) v3
 *  *    and
 *  *    2. Commercial license
 *  *
 *  *
 *  *    1. This program is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU Affero General Public License as published by
 *  *     the Free Software Foundation, either version 3 of the License, or
 *  *     (at your option) any later version.
 *  *
 *  *     This program is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU Affero General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU Affero General Public License
 *  *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  *
 *  *
 *  *    2. Licensees holding valid commercial licenses for TiMqWay may use this file in
 *  *     accordance with the commercial license agreement provided with the
 *  *     Software or, alternatively, in accordance with the terms contained in
 *  *     a written agreement between you and Bern University of Applied Sciences (BFH),
 *  *     Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *  *     Quellgasse 21, CH-2501 Biel, Switzerland.
 *  *
 *  *
 *  *     For further information contact <e-mail: reto.koenig@bfh.ch>
 *  *
 *  *
 */
package ch.quantasy.gateway.service.device;

import ch.quantasy.gateway.service.TinkerForgeServiceContract;
import ch.quantasy.tinkerforge.device.TinkerforgeDeviceClass;
import ch.quantasy.tinkerforge.device.generic.GenericDevice;
import java.util.Map;

/**
 *
 * @author reto
 */
public abstract class DeviceServiceContract extends TinkerForgeServiceContract {

    public final String STATUS_POSITION;
    public final String STATUS_FIRMWARE;
    public final String STATUS_HARDWARE;

    public DeviceServiceContract(GenericDevice device) {
        this(device.getUid(), TinkerforgeDeviceClass.getDevice(device.getDevice()).toString());
    }

    public DeviceServiceContract(String id, String device) {
        super(device, id);
        STATUS_POSITION = STATUS + "/position";
        STATUS_FIRMWARE = STATUS + "/firmware";
        STATUS_HARDWARE = STATUS + "/hardware";
    }

    @Override
    public void describe(Map<String, String> descriptions) {
        descriptions.put(STATUS_POSITION, "[0|1|2|3|4|5|6|7|8|a|b|c|d]");
        descriptions.put(STATUS_FIRMWARE, "[[" + Short.MIN_VALUE + "..." + Short.MAX_VALUE + "]]_*");
        descriptions.put(STATUS_HARDWARE, "[[" + Short.MIN_VALUE + "..." + Short.MAX_VALUE + "]]_*");
        descirbeMore(descriptions);
    }

    protected abstract void descirbeMore(Map<String, String> descriptions);
}
