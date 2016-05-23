/*
 *   "TiMqWay"
 *
 *    TiMqWay(tm): A gateway to provide an MQTT-View for the Tinkerforge(tm) world (Tinkerforge-MQTT-Gateway).
 *
 *    Copyright (c) 2015 Bern University of Applied Sciences (BFH),
 *    Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *    Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *    Licensed under Dual License consisting of:
 *    1. GNU Affero General Public License (AGPL) v3
 *    and
 *    2. Commercial license
 *
 *
 *    1. This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *    2. Licensees holding valid commercial licenses for TiMqWay may use this file in
 *     accordance with the commercial license agreement provided with the
 *     Software or, alternatively, in accordance with the terms contained in
 *     a written agreement between you and Bern University of Applied Sciences (BFH),
 *     Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *     Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *     For further information contact <e-mail: reto.koenig@bfh.ch>
 *
 *
 */
package ch.quantasy.gateway.service.device.analogOutV2;

import ch.quantasy.gateway.service.device.AbstractDeviceService;
import ch.quantasy.tinkerforge.device.analogOutV2.AnalogOutV2Device;
import ch.quantasy.tinkerforge.device.analogOutV2.AnalogOutV2DeviceCallback;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 *
 * @author reto
 */
public class AnalogOutV2Service extends AbstractDeviceService<AnalogOutV2Device, AnalogOutV2ServiceContract> implements AnalogOutV2DeviceCallback {

    public AnalogOutV2Service(AnalogOutV2Device device, URI mqttURI) throws MqttException {
        super(device, new AnalogOutV2ServiceContract(device), mqttURI);
        addDescription(getServiceContract().INTENT_OUTPUT_VOLTAGE, "[0..12000]");
        addDescription(getServiceContract().STATUS_OUTPUT_VOLTAGE, "[0..12000]");
    }

    @Override
    public void messageArrived(String string, MqttMessage mm) {
        byte[] payload = mm.getPayload();
        if (payload == null) {
            return;
        }
        try {
            if (string.startsWith(getServiceContract().INTENT_OUTPUT_VOLTAGE)) {
                Integer outputVoltage = getMapper().readValue(payload, Integer.class);
                getDevice().setOutputVoltage(outputVoltage);
            }
        } catch (Exception ex) {
            Logger.getLogger(AnalogOutV2Service.class
                    .getName()).log(Level.SEVERE, null, ex);
            return;
        }

    }
    
    @Override
    public void outputVoltageChanged(Integer voltage) {
        addEvent(getServiceContract().STATUS_OUTPUT_VOLTAGE, voltage);
    }

}