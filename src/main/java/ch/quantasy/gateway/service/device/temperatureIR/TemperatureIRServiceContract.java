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
package ch.quantasy.gateway.service.device.temperatureIR;

import ch.quantasy.gateway.service.device.DeviceServiceContract;
import ch.quantasy.tinkerforge.device.temperatureIR.TemperatureIRDevice;

/**
 *
 * @author reto
 */
public class TemperatureIRServiceContract extends DeviceServiceContract {

    public final String REACHED;
    public final String PERIOD;
    public final String CALLBACK_PERIOD;
    public final String THRESHOLD;

    public final String AMBIENT_TEMPERATURE;
    public final String STATUS_ANALOG_VALUE;
    public final String STATUS_AMBIENT_TEMPERATURE_THRESHOLD;
    public final String STATUS_AMBIENT_TEMPERATURE_CALLBACK_PERIOD;
    public final String EVENT_AMBIENT_TEMPERATURE;
    public final String EVENT_AMBIENT_TEMPERATURE_REACHED;
    public final String INTENT_ANALOG_VALUE;
    public final String INTENT_AMBIENT_TEMPERATURE_THRESHOLD;
    public final String INTENT_AMBIENT_TEMPERATURE_CALLBACK_PERIOD;

    public final String OBJECT_TEMPERATURE;
    public final String STATUS_OBJECT_TEMPERATURE;
    public final String STATUS_OBJECT_TEMPERATURE_THRESHOLD;
    public final String STATUS_OBJECT_TEMPERATURE_CALLBACK_PERIOD;
    public final String EVENT_OBJECT_TEMPERATURE;
    public final String EVENT_OBJECT_TEMPERATURE_REACHED;
    public final String INTENT_OBJECT_TEMPERATURE;
    public final String INTENT_OBJECT_TEMPERATURE_THRESHOLD;
    public final String INTENT_OBJECT_TEMPERATURE_CALLBACK_PERIOD;

    public final String DEBOUNCE;
    public final String STATUS_DEBOUNCE;
    public final String EVENT_DEBOUNCE;
    public final String INTENT_DEBOUNCE;
    public final String INTENT_DEBOUNCE_PERIOD;
    public final String STATUS_DEBOUNCE_PERIOD;

    public TemperatureIRServiceContract(TemperatureIRDevice device) {
        super(device);

        PERIOD = "period";
        CALLBACK_PERIOD = "callbackPeriod";
        THRESHOLD = "threshold";

        REACHED = "reached";
        AMBIENT_TEMPERATURE = "ambientTemperature";
        STATUS_ANALOG_VALUE = STATUS + "/" + AMBIENT_TEMPERATURE;
        STATUS_AMBIENT_TEMPERATURE_THRESHOLD = STATUS_ANALOG_VALUE + "/" + THRESHOLD;
        STATUS_AMBIENT_TEMPERATURE_CALLBACK_PERIOD = STATUS_ANALOG_VALUE + "/" + CALLBACK_PERIOD;
        EVENT_AMBIENT_TEMPERATURE = EVENT + "/" + AMBIENT_TEMPERATURE;
        EVENT_AMBIENT_TEMPERATURE_REACHED = EVENT_AMBIENT_TEMPERATURE + "/" + REACHED;
        INTENT_ANALOG_VALUE = INTENT + "/" + AMBIENT_TEMPERATURE;
        INTENT_AMBIENT_TEMPERATURE_THRESHOLD = INTENT_ANALOG_VALUE + "/" + THRESHOLD;
        INTENT_AMBIENT_TEMPERATURE_CALLBACK_PERIOD = INTENT_ANALOG_VALUE + "/" + CALLBACK_PERIOD;

        OBJECT_TEMPERATURE = "objectTemperature";
        STATUS_OBJECT_TEMPERATURE = STATUS + "/" + OBJECT_TEMPERATURE;
        STATUS_OBJECT_TEMPERATURE_THRESHOLD = STATUS_OBJECT_TEMPERATURE + "/" + THRESHOLD;
        STATUS_OBJECT_TEMPERATURE_CALLBACK_PERIOD = STATUS_OBJECT_TEMPERATURE + "/" + CALLBACK_PERIOD;
        EVENT_OBJECT_TEMPERATURE = EVENT + "/" + OBJECT_TEMPERATURE;
        EVENT_OBJECT_TEMPERATURE_REACHED = EVENT_OBJECT_TEMPERATURE + "/" + REACHED;
        INTENT_OBJECT_TEMPERATURE = INTENT + "/" + OBJECT_TEMPERATURE;
        INTENT_OBJECT_TEMPERATURE_THRESHOLD = INTENT_OBJECT_TEMPERATURE + "/" + THRESHOLD;
        INTENT_OBJECT_TEMPERATURE_CALLBACK_PERIOD = INTENT_OBJECT_TEMPERATURE + "/" + CALLBACK_PERIOD;

        DEBOUNCE = "debounce";
        STATUS_DEBOUNCE = STATUS + "/" + DEBOUNCE;
        STATUS_DEBOUNCE_PERIOD = STATUS_DEBOUNCE + "/" + PERIOD;
        EVENT_DEBOUNCE = EVENT + "/" + DEBOUNCE;
        INTENT_DEBOUNCE = INTENT + "/" + DEBOUNCE;
        INTENT_DEBOUNCE_PERIOD = INTENT_DEBOUNCE + "/" + PERIOD;
    }
}