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
package ch.quantasy.gateway.service.device.ambientLightV2;

import ch.quantasy.gateway.service.device.AbstractDeviceService;
import ch.quantasy.tinkerforge.device.ambientLightV2.AmbientLightV2Device;
import ch.quantasy.tinkerforge.device.ambientLightV2.AmbientLightV2DeviceCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import ch.quantasy.tinkerforge.device.ambientLightV2.DeviceIlluminanceCallbackThreshold;
import ch.quantasy.tinkerforge.device.ambientLightV2.DeviceConfiguration;
import java.net.URI;

/**
 *
 * @author reto
 */
public class AmbientLightV2Service extends AbstractDeviceService<AmbientLightV2Device, AmbientLightV2ServiceContract> implements AmbientLightV2DeviceCallback {

    public AmbientLightV2Service(AmbientLightV2Device device, URI mqttURI) throws MqttException {

        super(mqttURI, device, new AmbientLightV2ServiceContract(device));
    }

    @Override
    public void messageReceived(String string, byte[] payload) throws Exception {

        if (string.startsWith(getContract().INTENT_DEBOUNCE_PERIOD)) {

            Long period = getMapper().readValue(payload, Long.class);
            getDevice().setDebouncePeriod(period);
        }
        if (string.startsWith(getContract().INTENT_ILLUMINANCE_CALLBACK_PERIOD)) {

            Long period = getMapper().readValue(payload, Long.class);
            getDevice().setIlluminanceCallbackPeriod(period);
        }

        if (string.startsWith(getContract().INTENT_ILLUMINANCE_THRESHOLD)) {

            DeviceIlluminanceCallbackThreshold threshold = getMapper().readValue(payload, DeviceIlluminanceCallbackThreshold.class);
            getDevice().setIlluminanceCallbackThreshold(threshold);
        }

        if (string.startsWith(getContract().INTENT_CONFIGURATION)) {

            DeviceConfiguration configuration = getMapper().readValue(payload, DeviceConfiguration.class);
            getDevice().setConfiguration(configuration);
        }
    }

    @Override
    public void debouncePeriodChanged(long period) {
        publishStatus(getContract().STATUS_DEBOUNCE_PERIOD, period);
    }

    @Override
    public void illuminanceCallbackPeriodChanged(long period) {
        publishStatus(getContract().STATUS_ILLUMINANCE_CALLBACK_PERIOD, period);
    }

    @Override
    public void illuminanceCallbackThresholdChanged(DeviceIlluminanceCallbackThreshold threshold) {
        publishStatus(getContract().STATUS_ILLUMINANCE_THRESHOLD, threshold);
    }

    @Override
    public void configurationChanged(DeviceConfiguration configuration) {
        publishStatus(getContract().STATUS_CONFIGURATION, configuration);
    }

    @Override
    public void illuminance(long i) {
        publishEvent(getContract().EVENT_IllUMINANCE, i);
    }

    @Override
    public void illuminanceReached(long i) {
        publishEvent(getContract().EVENT_ILLUMINANCE_REACHED, i);
    }

}
