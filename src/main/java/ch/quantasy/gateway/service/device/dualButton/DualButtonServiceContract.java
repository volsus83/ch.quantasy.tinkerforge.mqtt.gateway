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
package ch.quantasy.gateway.service.device.dualButton;

import ch.quantasy.gateway.service.device.DeviceServiceContract;
import ch.quantasy.tinkerforge.device.TinkerforgeDeviceClass;
import ch.quantasy.tinkerforge.device.dualButton.DualButtonDevice;
import java.util.Map;

/**
 *
 * @author reto
 */
public class DualButtonServiceContract extends DeviceServiceContract {

    public final String LED_STATE;
    public final String INTENT_LED_STATE;
    public final String STATUS_LED_STATE;

    public final String SELECTED_LED_STATE;
    public final String INTENT_SELECTED_LED_STATE;

    public final String STATE_CHANGED;
    public final String EVENT_STATE_CHANGED;

    public final String MONOFLOP;
    public final String INTENT_MONOFLOP;

    public DualButtonServiceContract(DualButtonDevice device) {
        this(device.getUid(), TinkerforgeDeviceClass.getDevice(device.getDevice()).toString());
    }

    public DualButtonServiceContract(String id) {
        this(id, TinkerforgeDeviceClass.DualButton.toString());
    }

    public DualButtonServiceContract(String id, String device) {
        super(id, device);

        STATE_CHANGED = "stateChanged";
        EVENT_STATE_CHANGED = EVENT + "/" + STATE_CHANGED;

        MONOFLOP = "monoflop";
        INTENT_MONOFLOP = INTENT + "/" + MONOFLOP;

        LED_STATE = "LEDState";
        INTENT_LED_STATE = INTENT + "/" + LED_STATE;
        STATUS_LED_STATE = STATUS + "/" + LED_STATE;

        SELECTED_LED_STATE = "selectedLEDState";
        INTENT_SELECTED_LED_STATE = INTENT + "/" + SELECTED_LED_STATE;

    }

    @Override
    protected void descirbeMore(Map<String, String> descriptions) {
        descriptions.put(INTENT_LED_STATE, "leftLED: [AutoToggleOn|AutoToggleOff|On|Off]\n rightLED: [AutoToggleOn|AutoToggleOff|On|Off] ");
        descriptions.put(INTENT_SELECTED_LED_STATE, "led: [AutoToggleOn|AutoToggleOff|On|Off]");

        descriptions.put(EVENT_STATE_CHANGED, "- timestamp: [0.." + Long.MAX_VALUE + "]\n  value:\n    led1: [AutoToggleOn|AutoToggleOff|On|Off]\n    led2: [AutoToggleOn|AutoToggleOff|On|Off]\n   switch1: [0|1]\n   switch2: [0|1]");
        descriptions.put(STATUS_LED_STATE, "led1: [AutoToggleOn|AutoToggleOff|On|Off]\n led2: [AutoToggleOn|AutoToggleOff|On|Off]");
    }
}
