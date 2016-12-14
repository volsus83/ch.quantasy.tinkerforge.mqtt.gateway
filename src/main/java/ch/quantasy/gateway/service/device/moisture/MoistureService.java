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
package ch.quantasy.gateway.service.device.moisture;

import ch.quantasy.gateway.service.device.AbstractDeviceService;
import ch.quantasy.mqtt.gateway.client.GCEvent;
import org.eclipse.paho.client.mqttv3.MqttException;
import ch.quantasy.tinkerforge.device.moisture.MoistureDevice;
import ch.quantasy.tinkerforge.device.moisture.MoistureDeviceCallback;
import ch.quantasy.tinkerforge.device.moisture.DeviceMoistureCallbackThreshold;
import com.fasterxml.jackson.core.type.TypeReference;
import java.net.URI;

/**
 *
 * @author reto
 */
public class MoistureService extends AbstractDeviceService<MoistureDevice, MoistureServiceContract> implements MoistureDeviceCallback {

    public MoistureService(MoistureDevice device, URI mqttURI) throws MqttException {
        super(mqttURI, device, new MoistureServiceContract(device));
        publishDescription(getContract().INTENT_DEBOUNCE_PERIOD, "[0.." + Long.MAX_VALUE + "]");
        publishDescription(getContract().INTENT_MOISTURE_CALLBACK_PERIOD, "[0.." + Long.MAX_VALUE + "]");
        publishDescription(getContract().INTENT_MOISTURE_THRESHOLD, "option: [x|o|i|<|>]\n min: [0..4095]\n max: [0..4095]");
        publishDescription(getContract().INTENT_MOVING_AVERAGE, "[0..100]");

        publishDescription(getContract().EVENT_MOISTURE, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: [0..4095]\n");
        publishDescription(getContract().EVENT_MOISTURE_REACHED, "timestamp: [0.." + Long.MAX_VALUE + "]\n value: [0..4095]");
        publishDescription(getContract().STATUS_MOISTURE_CALLBACK_PERIOD, "[0.." + Long.MAX_VALUE + "]");
        publishDescription(getContract().STATUS_MOISTURE_THRESHOLD, "option: [x|o|i|<|>]\n min: [0..4095]\n max: [0..4095]");
        publishDescription(getContract().STATUS_DEBOUNCE_PERIOD, "[0.." + Long.MAX_VALUE + "]");
        publishDescription(getContract().STATUS_MOVING_AVERAGE, "[0..100]");

    }

    @Override
    public void messageReceived(String string, byte[] payload) throws Exception {

        if (string.startsWith(getContract().INTENT_DEBOUNCE_PERIOD)) {
            Long period = getMapper().readValue(payload, Long.class);
            getDevice().setDebouncePeriod(period);
        }
        if (string.startsWith(getContract().INTENT_MOISTURE_CALLBACK_PERIOD)) {
            Long period = getMapper().readValue(payload, Long.class);
            getDevice().setMoistureCallbackPeriod(period);
        }

        if (string.startsWith(getContract().INTENT_MOISTURE_THRESHOLD)) {
            DeviceMoistureCallbackThreshold threshold = getMapper().readValue(payload, DeviceMoistureCallbackThreshold.class);
            getDevice().setMoistureCallbackThreshold(threshold);
        }

        if (string.startsWith(getContract().INTENT_MOVING_AVERAGE)) {
            Short average = getMapper().readValue(payload, Short.class);
            getDevice().setMovingAverage(average);
        }
    }

    @Override
    public void debouncePeriodChanged(long period) {
        publishStatus(getContract().STATUS_DEBOUNCE_PERIOD, period);
    }

    @Override
    public void moistureCallbackPeriodChanged(long period) {
        publishStatus(getContract().STATUS_MOISTURE_CALLBACK_PERIOD, period);
    }

    @Override
    public void moistureCallbackThresholdChanged(DeviceMoistureCallbackThreshold threshold) {
        publishStatus(getContract().STATUS_MOISTURE_THRESHOLD, threshold);
    }

    @Override
    public void movingAverageChanged(short average) {
        publishStatus(getContract().STATUS_MOVING_AVERAGE, average);
    }

    @Override
    public void moisture(int i) {
        publishEvent(getContract().EVENT_MOISTURE, i);
    }

    @Override
    public void moistureReached(int i) {
        publishEvent(getContract().EVENT_MOISTURE_REACHED, i);
    }

}
